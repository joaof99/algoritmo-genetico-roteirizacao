package com.genetico.vaadin;

import com.genetico.model.AlgoritmoGeneticoRequest;
import com.genetico.model.EstatisticasFitness;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VaadinClientTest {
    private final HttpClient httpClient = mock(HttpClient.class);
    private final VaadinClient vaadinClient = new VaadinClient(httpClient, new RoteirizacaoBodyBuilder());
    private final AlgoritmoGeneticoRequest request = new AlgoritmoGeneticoRequest(List.of(), List.of(), 10, 50, 5, 80);
    private final EstatisticasFitness estatisticasFitness = new EstatisticasFitness(100.0, 40.0);

    @AfterEach
    void limparFlagDeInterrupcao() {
        Thread.interrupted();
    }

    @Test
    @DisplayName("Deve enviar a requisição via POST para o endpoint padrão do Vaadin")
    void deveEnviarRequisicaoPostParaOEndpointPadrao() throws IOException, InterruptedException {
        var respostaHttp = mock(HttpResponse.class);
        when(respostaHttp.statusCode())
                .thenReturn(200);
        when(httpClient.send(any(), any()))
                .thenReturn(respostaHttp);

        vaadinClient.enviarResultadoRoteirizacao(request, estatisticasFitness);

        var requisicaoCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requisicaoCaptor.capture(), any());

        var requisicaoEnviada = requisicaoCaptor.getValue();
        assertEquals(URI.create("http://localhost:8080/roteirizacao/armazenar"), requisicaoEnviada.uri());
        assertEquals("POST", requisicaoEnviada.method());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando ocorrer erro de I/O ao enviar a requisição")
    void naoDeveLancarExcecaoQuandoOcorrerErroDeIO() throws IOException, InterruptedException {
        doThrow(new IOException("Falha de conexão"))
                .when(httpClient)
                .send(any(), any());

        vaadinClient.enviarResultadoRoteirizacao(request, estatisticasFitness);

        verify(httpClient).send(any(), any());
    }

    @Test
    @DisplayName("Deve restaurar a flag de interrupção quando o envio for interrompido")
    void deveRestaurarFlagDeInterrupcaoQuandoEnvioForInterrompido() throws IOException, InterruptedException {
        doThrow(new InterruptedException())
                .when(httpClient)
                .send(any(), any());

        vaadinClient.enviarResultadoRoteirizacao(request, estatisticasFitness);

        assertTrue(Thread.currentThread().isInterrupted());
    }
}
