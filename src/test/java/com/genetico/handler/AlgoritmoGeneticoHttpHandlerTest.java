package com.genetico.handler;

import com.genetico.model.AlgoritmoGeneticoRequest;
import com.genetico.service.RoteirizacaoService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.RejectedExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AlgoritmoGeneticoHttpHandlerTest {
    private static final String CORPO_VALIDO = """
            {
              "idRoteirizacao": 7,
              "enderecos": [
                {"id": 1, "latitude": -23.5, "longitude": -46.6},
                {"id": 2, "latitude": -23.6, "longitude": -46.7}
              ],
              "distancias": [
                {"idOrigem": 1, "idDestino": 2, "distancia": 10.0},
                {"idOrigem": 2, "idDestino": 1, "distancia": 10.0}
              ],
              "tamanhoPopulacao": 2,
              "quantidadeGeracoes": 1,
              "chanceOcorrenciaMutacao": 0,
              "chanceOcorrenciaCrossover": 0
            }
            """;

    private static final String RESPOSTA_PEDIDO_ACEITO = "{\"status\":\"Pedido de roteirização recebido com sucesso\"}";

    private final RoteirizacaoService roteirizacaoService = mock(RoteirizacaoService.class);
    private final AlgoritmoGeneticoHttpHandler handler = new AlgoritmoGeneticoHttpHandler(roteirizacaoService, Runnable::run);

    @Test
    @DisplayName("Deve rejeitar método diferente de POST")
    void deveRejeitarMetodoDiferenteDePost() throws IOException {
        var exchange = criarExchange("GET", "");

        handler.handle(exchange);

        var statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(exchange).sendResponseHeaders(statusCaptor.capture(), anyLong());

        assertEquals(405, statusCaptor.getValue());
        assertEquals("application/json", exchange.getResponseHeaders().getFirst("Content-Type"));
        assertEquals("{\"erro\":\"Método não permitido\"}", exchange.getResponseBody().toString());
    }

    @Test
    @DisplayName("Deve responder erro quando o JSON for inválido")
    void deveResponderErroQuandoJsonForInvalido() throws IOException {
        var exchange = criarExchange("POST", "{");

        handler.handle(exchange);

        var statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(exchange).sendResponseHeaders(statusCaptor.capture(), anyLong());

        assertEquals(400, statusCaptor.getValue());
        assertEquals("application/json", exchange.getResponseHeaders().getFirst("Content-Type"));
        assertEquals("{\"erro\":\"JSON inválido\"}", exchange.getResponseBody().toString());
    }

    @Test
    @DisplayName("Deve responder requisição inválida quando os dados estiverem incompletos")
    void deveResponderRequisicaoInvalidaQuandoDadosEstiveremIncompletos() throws IOException {
        var exchange = criarExchange("POST", "{}");

        handler.handle(exchange);

        var statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(exchange).sendResponseHeaders(statusCaptor.capture(), anyLong());

        assertEquals(400, statusCaptor.getValue());
        assertEquals("application/json", exchange.getResponseHeaders().getFirst("Content-Type"));
        assertEquals("{\"erro\":\"Dados da requisição inválidos\"}", exchange.getResponseBody().toString());
    }

    @Test
    @DisplayName("Deve responder que o pedido de roteirização foi aceito")
    void deveResponderQuePedidoDeRoteirizacaoFoiAceito() throws IOException {
        var exchange = criarExchange("POST", CORPO_VALIDO);

        handler.handle(exchange);

        var statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(exchange).sendResponseHeaders(statusCaptor.capture(), anyLong());

        assertEquals(202, statusCaptor.getValue());
        assertEquals("application/json", exchange.getResponseHeaders().getFirst("Content-Type"));
        assertEquals(RESPOSTA_PEDIDO_ACEITO, exchange.getResponseBody().toString());
    }

    @Test
    @DisplayName("Deve extrair os dados da requisição e delegar a roteirização ao serviço")
    void deveExtrairDadosERepassarAoServicoDeRoteirizacao() throws IOException {
        handler.handle(criarExchange("POST", CORPO_VALIDO));

        verify(roteirizacaoService).executar(any(AlgoritmoGeneticoRequest.class));
    }

    @Test
    @DisplayName("Deve extrair da requisição o ID da roteirização")
    void deveExtrairDaRequisicaoIdDaRoteirizacao() throws IOException {
        handler.handle(criarExchange("POST", CORPO_VALIDO));

        var requestCaptor = ArgumentCaptor.forClass(AlgoritmoGeneticoRequest.class);
        verify(roteirizacaoService).executar(requestCaptor.capture());
        assertEquals(7, requestCaptor.getValue().idRoteirizacao());
    }

    @Test
    @DisplayName("Deve responder antes de a roteirização ser executada")
    void deveResponderAntesDeRoteirizacaoSerExecutada() throws IOException {
        var handlerSemExecucao = new AlgoritmoGeneticoHttpHandler(roteirizacaoService, _ -> {
        });
        var exchange = criarExchange("POST", CORPO_VALIDO);

        handlerSemExecucao.handle(exchange);

        var statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(exchange).sendResponseHeaders(statusCaptor.capture(), anyLong());

        assertEquals(202, statusCaptor.getValue());
        assertEquals("application/json", exchange.getResponseHeaders().getFirst("Content-Type"));
        assertEquals(RESPOSTA_PEDIDO_ACEITO, exchange.getResponseBody().toString());
        verifyNoInteractions(roteirizacaoService);
    }

    @Test
    @DisplayName("Não deve propagar a falha da roteirização executada em segundo plano")
    void naoDevePropagarFalhaDaRoteirizacaoExecutadaEmSegundoPlano() throws IOException {
        var exchange = criarExchange("POST", CORPO_VALIDO);

        doThrow(new RuntimeException("Falha simulada"))
                .when(roteirizacaoService)
                .executar(any());

        handler.handle(exchange);

        var statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(exchange).sendResponseHeaders(statusCaptor.capture(), anyLong());

        assertEquals(202, statusCaptor.getValue());
        assertEquals("application/json", exchange.getResponseHeaders().getFirst("Content-Type"));
        assertEquals(RESPOSTA_PEDIDO_ACEITO, exchange.getResponseBody().toString());
    }

    @Test
    @DisplayName("Deve responder erro interno quando a roteirização não puder ser agendada")
    void deveResponderErroInternoQuandoRoteirizacaoNaoPuderSerAgendada() throws IOException {
        var handlerComFilaRecusando = new AlgoritmoGeneticoHttpHandler(roteirizacaoService, _ -> {
            throw new RejectedExecutionException("Fila recusou a tarefa");
        });
        var exchange = criarExchange("POST", CORPO_VALIDO);

        handlerComFilaRecusando.handle(exchange);

        var statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(exchange).sendResponseHeaders(statusCaptor.capture(), anyLong());

        assertEquals(500, statusCaptor.getValue());
        assertEquals("application/json", exchange.getResponseHeaders().getFirst("Content-Type"));
        assertEquals("{\"erro\":\"Erro interno do servidor\"}", exchange.getResponseBody().toString());
    }

    private static HttpExchange criarExchange(String metodo, String corpo) {
        var exchange = mock(HttpExchange.class);

        when(exchange.getRequestMethod())
                .thenReturn(metodo);

        when(exchange.getRequestBody())
                .thenReturn(new ByteArrayInputStream(corpo.getBytes(StandardCharsets.UTF_8)));

        when(exchange.getResponseHeaders())
                .thenReturn(new Headers());

        when(exchange.getResponseBody())
                .thenReturn(new ByteArrayOutputStream());

        return exchange;
    }
}
