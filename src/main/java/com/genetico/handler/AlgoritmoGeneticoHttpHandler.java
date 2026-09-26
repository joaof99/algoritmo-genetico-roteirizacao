package com.genetico.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genetico.AlgoritmoGenetico;
import com.genetico.distancia.CalculoDistanciaAPI;
import com.genetico.distancia.GerenciadorDistancias;
import com.genetico.model.AlgoritmoGeneticoRequest;
import com.genetico.model.DistanciaResponse;
import com.genetico.model.Endereco;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AlgoritmoGeneticoHttpHandler implements HttpHandler {
    private static final Logger log = LogManager.getLogger();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                enviarResposta(exchange, 405, "{\"erro\":\"Método não permitido\"}");
                return;
            }

            var json = OBJECT_MAPPER.readTree(exchange.getRequestBody());

            var dadosAlgoritmoGenetico = extrairDadosAlgoritmoGenetico(json);
            inicializarAlgoritmoGenetico(dadosAlgoritmoGenetico);

            enviarResposta(exchange, 200, "{\"status\":\"algoritmo finalizado\"}");
        } catch (JsonProcessingException e) {
            log.error("Erro ao parsear JSON da requisição", e);

            enviarResposta(exchange, 400, "{\"erro\":\"JSON inválido\"}");
        } catch (Exception e) {
            log.error("Erro inesperado ao processar requisição", e);

            enviarResposta(exchange, 500, "{\"erro\":\"Erro interno do servidor\"}");
        }
    }

    private void inicializarAlgoritmoGenetico(AlgoritmoGeneticoRequest request) {
        GerenciadorDistancias.inicializar(new CalculoDistanciaAPI(), request);

        var enderecos = request.enderecos().toArray(com.genetico.model.Endereco[]::new);

        var algoritmoGenetico = new AlgoritmoGenetico.Builder()
                .tamanhoPopulacao(request.tamanhoPopulacao())
                .qtdeGeracoes(request.quantidadeGeracoes())
                .enderecos(enderecos)
                .chanceOcorrenciaMutacao(request.chanceOcorrenciaMutacao())
                .chanceOcorrenciaCrossover(request.chanceOcorrenciaCrossover())
                .build();

        var populacaoFinal = algoritmoGenetico.reproduzir();

        log.info("População final:");

        populacaoFinal.imprimirPopulacao();
    }

    private AlgoritmoGeneticoRequest extrairDadosAlgoritmoGenetico(JsonNode json) {
        var enderecos = new ArrayList<Endereco>();
        var distancias = new ArrayList<DistanciaResponse>();

        json.get("enderecos").forEach(jsonNode -> {
            var id = jsonNode.get("id").asInt();
            var latitude = jsonNode.get("latitude").asDouble();
            var longitude = jsonNode.get("longitude").asDouble();

            enderecos.add(new Endereco(id, latitude, longitude));
        });

        json.get("distancias").forEach(jsonNode -> {
            var idOrigem = jsonNode.get("idOrigem").asInt();
            var idDestino = jsonNode.get("idDestino").asInt();
            var distancia = jsonNode.get("distancia").asDouble();

            distancias.add(new DistanciaResponse(idOrigem, idDestino, distancia));
        });

        log.info("Foram encontradas {} endereços e {} distâncias", enderecos.size(), distancias.size());

        var tamanhoPopulacao = json.get("tamanhoPopulacao").asInt();
        var quantidadeGeracoes = json.get("quantidadeGeracoes").asInt();
        var chanceOcorrenciaMutacao = json.get("chanceOcorrenciaMutacao").asInt();
        var chanceOcorrenciaCrossover = json.get("chanceOcorrenciaCrossover").asInt();

        return new AlgoritmoGeneticoRequest(
                distancias,
                enderecos,
                tamanhoPopulacao,
                quantidadeGeracoes,
                chanceOcorrenciaMutacao,
                chanceOcorrenciaCrossover
        );
    }

    private void enviarResposta(HttpExchange exchange, int status, String resposta) throws IOException {
        var bytes = resposta.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);

        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
