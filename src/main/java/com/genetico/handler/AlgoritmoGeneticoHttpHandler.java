package com.genetico.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genetico.model.AlgoritmoGeneticoRequest;
import com.genetico.model.DistanciaResponse;
import com.genetico.model.Endereco;
import com.genetico.service.RoteirizacaoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AlgoritmoGeneticoHttpHandler implements HttpHandler {
    private static final Logger log = LogManager.getLogger();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final RoteirizacaoService roteirizacaoService;
    private final Executor executorRoteirizacoes;

    public AlgoritmoGeneticoHttpHandler() {
        this(new RoteirizacaoService(), Executors.newSingleThreadExecutor());
    }

    AlgoritmoGeneticoHttpHandler(RoteirizacaoService roteirizacaoService, Executor executorRoteirizacoes) {
        this.roteirizacaoService = roteirizacaoService;
        this.executorRoteirizacoes = executorRoteirizacoes;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var status = 202;
        var resposta = "{\"status\":\"Pedido de roteirização recebido com sucesso\"}";

        try {
            log.info("Recebendo requisição ...");

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                status = 405;
                resposta = "{\"erro\":\"Método não permitido\"}";
            } else {
                var json = OBJECT_MAPPER.readTree(exchange.getRequestBody());
                var request = extrairDadosAlgoritmoGenetico(json);
                executorRoteirizacoes.execute(() -> executarRoteirizacao(request));
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

    private void executarRoteirizacao(AlgoritmoGeneticoRequest request) {
        try {
            roteirizacaoService.executar(request);
        } catch (Exception e) {
            log.error("Erro ao executar a roteirização em segundo plano", e);
        }
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
