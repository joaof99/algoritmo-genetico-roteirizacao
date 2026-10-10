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
        var status = 200;
        var resposta = "{\"status\":\"algoritmo finalizado\"}";

        try {
            log.info("Recebendo requisição ...");

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                status = 405;
                resposta = "{\"erro\":\"Método não permitido\"}";
            } else {
                var json = OBJECT_MAPPER.readTree(exchange.getRequestBody());
                inicializarAlgoritmoGenetico(extrairDadosAlgoritmoGenetico(json));
            }
        } catch (JsonProcessingException e) {
            log.error("Erro ao parsear JSON da requisição", e);
            status = 400;
            resposta = "{\"erro\":\"JSON inválido\"}";
        } catch (RequisicaoInvalidaException e) {
            log.warn("Requisição inválida: {}", e.getMessage());
            status = 400;
            resposta = "{\"erro\":\"Dados da requisição inválidos\"}";
        } catch (Exception e) {
            log.error("Erro inesperado ao processar requisição", e);
            status = 500;
            resposta = "{\"erro\":\"Erro interno do servidor\"}";
        }

        enviarResposta(exchange, status, resposta);
    }

    private void inicializarAlgoritmoGenetico(AlgoritmoGeneticoRequest request) {
        GerenciadorDistancias.inicializar(new CalculoDistanciaAPI(), request);

        var algoritmoGenetico = new AlgoritmoGenetico.Builder()
                .tamanhoPopulacao(request.tamanhoPopulacao())
                .qtdeGeracoes(request.quantidadeGeracoes())
                .enderecos(request.enderecos().toArray(Endereco[]::new))
                .chanceOcorrenciaMutacao(request.chanceOcorrenciaMutacao())
                .chanceOcorrenciaCrossover(request.chanceOcorrenciaCrossover())
                .build();

        var populacaoFinal = algoritmoGenetico.reproduzir();

        log.info("População final:");
        populacaoFinal.imprimirPopulacao();
    }

    private AlgoritmoGeneticoRequest extrairDadosAlgoritmoGenetico(JsonNode json) {
        if (json == null || !json.isObject()) {
            throw new RequisicaoInvalidaException("O corpo deve ser um objeto JSON");
        }

        var enderecos = new ArrayList<Endereco>();
        var distancias = new ArrayList<DistanciaResponse>();

        obterArray(json, "enderecos").forEach(jsonNode -> enderecos.add(new Endereco(
                obterInteiro(jsonNode, "id"),
                obterDecimal(jsonNode, "latitude"),
                obterDecimal(jsonNode, "longitude")
        )));

        obterArray(json, "distancias").forEach(jsonNode -> distancias.add(new DistanciaResponse(
                obterInteiro(jsonNode, "idOrigem"),
                obterInteiro(jsonNode, "idDestino"),
                obterDecimal(jsonNode, "distancia")
        )));

        log.info("Foram encontrados {} endereços e {} distâncias", enderecos.size(), distancias.size());

        return new AlgoritmoGeneticoRequest(
                distancias,
                enderecos,
                obterInteiro(json, "tamanhoPopulacao"),
                obterInteiro(json, "quantidadeGeracoes"),
                obterInteiro(json, "chanceOcorrenciaMutacao"),
                obterInteiro(json, "chanceOcorrenciaCrossover")
        );
    }

    private JsonNode obterArray(JsonNode json, String campo) {
        var valor = json.get(campo);
        if (valor == null || !valor.isArray()) {
            throw new RequisicaoInvalidaException("O campo '" + campo + "' deve ser um array");
        }
        return valor;
    }

    private int obterInteiro(JsonNode json, String campo) {
        var valor = json.get(campo);
        if (valor == null || !valor.isIntegralNumber() || !valor.canConvertToInt()) {
            throw new RequisicaoInvalidaException("O campo '" + campo + "' deve ser um inteiro");
        }
        return valor.intValue();
    }

    private double obterDecimal(JsonNode json, String campo) {
        var valor = json.get(campo);
        if (valor == null || !valor.isNumber() || !Double.isFinite(valor.doubleValue())) {
            throw new RequisicaoInvalidaException("O campo '" + campo + "' deve ser um número finito");
        }
        return valor.doubleValue();
    }

    private void enviarResposta(HttpExchange exchange, int status, String resposta) throws IOException {
        var bytes = resposta.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);

        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private static class RequisicaoInvalidaException extends RuntimeException {
        private RequisicaoInvalidaException(String message) {
            super(message);
        }
    }
}
