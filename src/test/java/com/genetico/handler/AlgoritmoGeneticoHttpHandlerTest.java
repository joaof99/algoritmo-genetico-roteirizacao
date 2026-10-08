package com.genetico.handler;

import com.genetico.factory.GraficoServiceFactory;
import com.genetico.service.GraficoService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AlgoritmoGeneticoHttpHandlerTest {
    private final AlgoritmoGeneticoHttpHandler handler = new AlgoritmoGeneticoHttpHandler();
    private MockedStatic<GraficoServiceFactory> graficoServiceFactory;
    private GraficoService graficoService;

    @BeforeEach
    void setUp() {
        graficoServiceFactory = Mockito.mockStatic(GraficoServiceFactory.class);
        graficoService = mock(GraficoService.class);
        graficoServiceFactory.when(GraficoServiceFactory::getGraficoService)
                .thenReturn(graficoService);
    }

    @AfterEach
    void tearDown() {
        graficoServiceFactory.close();
    }

    @Test
    @DisplayName("Deve rejeitar método diferente de POST")
    void deveRejeitarMetodoDiferenteDePost() throws IOException {
        var exchange = criarExchange("GET", "");

        handler.handle(exchange);

        var resposta = "{\"erro\":\"Método não permitido\"}";
        verify(exchange).sendResponseHeaders(eq(405), anyLong());
        assertEquals("application/json", exchange.getResponseHeaders().getFirst("Content-Type"));
        assertEquals(resposta, exchange.getResponseBody().toString());
    }

    @Test
    @DisplayName("Deve responder erro quando o JSON for inválido")
    void deveResponderErroQuandoJsonForInvalido() throws IOException {
        var exchange = criarExchange("POST", "{");

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(400), anyLong());
        assertEquals("application/json", exchange.getResponseHeaders().getFirst("Content-Type"));
        assertEquals("{\"erro\":\"JSON inválido\"}", exchange.getResponseBody().toString());
    }

    @Test
    @DisplayName("Deve responder erro interno quando os dados estiverem incompletos")
    void deveResponderErroInternoQuandoDadosEstiveremIncompletos() throws IOException {
        var exchange = criarExchange("POST", "{}");

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(500), anyLong());

        assertEquals("application/json", exchange.getResponseHeaders().getFirst("Content-Type"));
        assertEquals("{\"erro\":\"Erro interno do servidor\"}", exchange.getResponseBody().toString());
    }

    @Test
    @DisplayName("Deve processar algoritmo e responder sucesso")
    void deveProcessarAlgoritmoEResponderSucesso() throws IOException {
        var corpo = """
                {
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

        var exchange = criarExchange("POST", corpo);

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        verify(graficoService).gerarGraficoEvolucaoFitness(Mockito.any(double[].class), Mockito.any(double[].class));

        assertEquals("application/json", exchange.getResponseHeaders().getFirst("Content-Type"));
        assertEquals("{\"status\":\"algoritmo finalizado\"}", exchange.getResponseBody().toString());
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
