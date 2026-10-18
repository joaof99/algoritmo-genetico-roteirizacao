package com.genetico.aginterface;

import com.genetico.model.EstatisticasFitness;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgInterfaceClientTest {
    private final HttpClient httpClient = mock(HttpClient.class);
    private final AgInterfaceClient agInterfaceClient = new AgInterfaceClient(httpClient, new RoteirizacaoBodyBuilder(), Duration.ZERO);

    @AfterEach
    void limparFlagDeInterrupcao() {
        Thread.interrupted();
    }

    @Test
    @DisplayName("Deve enviar a conclusão via POST para o endpoint de conclusão da roteirização")
    void deveEnviarConclusaoViaPostParaEndpointDeConclusao() throws IOException, InterruptedException {
        @SuppressWarnings("unchecked")
        HttpResponse<Object> resposta = mock(HttpResponse.class);

        when(resposta.statusCode())
                .thenReturn(204);

        when(httpClient.send(any(), any()))
                .thenReturn(resposta);

        agInterfaceClient.enviarConclusao(7, new EstatisticasFitness(List.of(100.0, 70.0, 40.0)), new int[]{30, 10, 20});

        var requisicaoCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requisicaoCaptor.capture(), any());

        var requisicaoEnviada = requisicaoCaptor.getValue();
        assertEquals(URI.create("http://localhost:8080/api/roteirizacao/7/concluir"), requisicaoEnviada.uri());
        assertEquals("POST", requisicaoEnviada.method());
    }

    @Test
    @DisplayName("Deve enviar a falha via POST para o endpoint de falha da roteirização")
    void deveEnviarFalhaViaPostParaEndpointDeFalha() throws IOException, InterruptedException {
        @SuppressWarnings("unchecked")
        HttpResponse<Object> resposta = mock(HttpResponse.class);

        when(resposta.statusCode())
                .thenReturn(204);

        when(httpClient.send(any(), any()))
                .thenReturn(resposta);

        agInterfaceClient.enviarFalha(7, "Erro");

        var requisicaoCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requisicaoCaptor.capture(), any());

        var requisicaoEnviada = requisicaoCaptor.getValue();
        assertEquals(URI.create("http://localhost:8080/api/roteirizacao/7/falhar"), requisicaoEnviada.uri());
        assertEquals("POST", requisicaoEnviada.method());
    }

    @Test
    @DisplayName("Deve tentar novamente quando o AgInterface retornar erro interno")
    void deveTentarNovamenteQuandoAgInterfaceRetornarErroInterno() throws IOException, InterruptedException {
        @SuppressWarnings("unchecked")
        HttpResponse<Object> respostaErroInterno = mock(HttpResponse.class);

        when(respostaErroInterno.statusCode())
                .thenReturn(500);

        @SuppressWarnings("unchecked")
        HttpResponse<Object> respostaSucesso = mock(HttpResponse.class);

        when(respostaSucesso.statusCode())
                .thenReturn(204);

        when(httpClient.send(any(), any()))
                .thenReturn(respostaErroInterno)
                .thenReturn(respostaSucesso);

        agInterfaceClient.enviarConclusao(7, new EstatisticasFitness(List.of(100.0, 70.0, 40.0)), new int[]{30, 10, 20});

        verify(httpClient, times(2)).send(any(), any());
    }

    @Test
    @DisplayName("Deve tentar novamente quando ocorrer erro de I/O")
    void deveTentarNovamenteQuandoOcorrerErroDeIO() throws IOException, InterruptedException {
        @SuppressWarnings("unchecked")
        HttpResponse<Object> respostaSucesso = mock(HttpResponse.class);

        when(respostaSucesso.statusCode())
                .thenReturn(204);

        when(httpClient.send(any(), any()))
                .thenThrow(new IOException("Falha de conexão"))
                .thenReturn(respostaSucesso);

        agInterfaceClient.enviarConclusao(7, new EstatisticasFitness(List.of(100.0, 70.0, 40.0)), new int[]{30, 10, 20});

        verify(httpClient, times(2)).send(any(), any());
    }

    @Test
    @DisplayName("Não deve tentar novamente quando o AgInterface recusar a requisição")
    void naoDeveTentarNovamenteQuandoAgInterfaceRecusarRequisicao() throws IOException, InterruptedException {
        @SuppressWarnings("unchecked")
        HttpResponse<Object> respostaConflito = mock(HttpResponse.class);

        when(respostaConflito.statusCode())
                .thenReturn(409);

        when(httpClient.send(any(), any()))
                .thenReturn(respostaConflito);

        agInterfaceClient.enviarConclusao(7, new EstatisticasFitness(List.of(100.0, 70.0, 40.0)), new int[]{30, 10, 20});

        verify(httpClient, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("Deve considerar entregue, sem registrar erro, quando o AgInterface responder 200")
    void deveConsiderarEntregueSemRegistrarErroQuandoAgInterfaceResponder200() throws IOException, InterruptedException {
        @SuppressWarnings("unchecked")
        HttpResponse<Object> respostaSucesso = mock(HttpResponse.class);

        when(respostaSucesso.statusCode())
                .thenReturn(200);

        when(httpClient.send(any(), any()))
                .thenReturn(respostaSucesso);

        var eventos = new ArrayList<LogEvent>();
        var appender = new AbstractAppender("memoria", null, null, true, Property.EMPTY_ARRAY) {
            @Override
            public void append(LogEvent evento) {
                eventos.add(evento.toImmutable());
            }
        };

        appender.start();
        var logger = (Logger) LogManager.getLogger(AgInterfaceClient.class);
        logger.addAppender(appender);

        try {
            agInterfaceClient.enviarConclusao(7, new EstatisticasFitness(List.of(100.0, 70.0, 40.0)), new int[]{30, 10, 20});
        } finally {
            logger.removeAppender(appender);
        }

        assertTrue(eventos.stream().noneMatch(evento -> evento.getLevel() == Level.ERROR));
    }

    @Test
    @DisplayName("Deve registrar erro quando o endereço do AgInterface não existir")
    void deveRegistrarErroQuandoEnderecoDoAgInterfaceNaoExistir() throws IOException, InterruptedException {
        @SuppressWarnings("unchecked")
        HttpResponse<Object> respostaNaoEncontrado = mock(HttpResponse.class);

        when(respostaNaoEncontrado.statusCode())
                .thenReturn(404);

        when(httpClient.send(any(), any()))
                .thenReturn(respostaNaoEncontrado);

        var eventos = new ArrayList<LogEvent>();
        var appender = new AbstractAppender("memoria", null, null, true, Property.EMPTY_ARRAY) {
            @Override
            public void append(LogEvent evento) {
                eventos.add(evento.toImmutable());
            }
        };

        appender.start();
        var logger = (Logger) LogManager.getLogger(AgInterfaceClient.class);
        logger.addAppender(appender);

        try {
            agInterfaceClient.enviarConclusao(7, new EstatisticasFitness(List.of(100.0, 70.0, 40.0)), new int[]{30, 10, 20});
        } finally {
            logger.removeAppender(appender);
        }

        assertTrue(eventos.stream().anyMatch(evento -> evento.getLevel() == Level.ERROR
                && evento.getMessage().getFormattedMessage().contains("Status: 404")));
    }

    @Test
    @DisplayName("Deve desistir do envio após seis tentativas sem sucesso")
    void deveDesistirDoEnvioAposSeisTentativasSemSucesso() throws IOException, InterruptedException {
        doThrow(new IOException("Falha de conexão"))
                .when(httpClient)
                .send(any(), any());

        agInterfaceClient.enviarConclusao(7, new EstatisticasFitness(List.of(100.0, 70.0, 40.0)), new int[]{30, 10, 20});

        verify(httpClient, times(6)).send(any(), any());
    }

    @Test
    @DisplayName("Deve restaurar a flag de interrupção quando o envio for interrompido")
    void deveRestaurarFlagDeInterrupcaoQuandoEnvioForInterrompido() throws IOException, InterruptedException {
        doThrow(new InterruptedException())
                .when(httpClient)
                .send(any(), any());

        agInterfaceClient.enviarConclusao(7, new EstatisticasFitness(List.of(100.0, 70.0, 40.0)), new int[]{30, 10, 20});

        assertTrue(Thread.currentThread().isInterrupted());
    }
}
