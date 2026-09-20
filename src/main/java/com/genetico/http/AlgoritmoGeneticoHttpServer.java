package com.genetico.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genetico.AlgoritmoGenetico;
import com.genetico.distancia.GerenciadorDistancias;
import com.genetico.distancia.CalculoDistanciaAPI;
import com.genetico.exception.AlgoritmoGeneticoHttpServerException;
import com.genetico.model.AlgoritmoGeneticoRequest;
import com.genetico.model.DistanciaResponse;
import com.genetico.model.Endereco;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AlgoritmoGeneticoHttpServer {
    private static final Logger log = LogManager.getLogger();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String hostname;
    private final int porta;
    private final String url;
    private HttpServer server;

    public AlgoritmoGeneticoHttpServer(int porta, String hostname) throws AlgoritmoGeneticoHttpServerException {
        this.porta = porta;
        this.hostname = hostname;
        this.url = definirUrl();
    }

    private String definirUrl() throws AlgoritmoGeneticoHttpServerException {
        var urlAlgoritmoGeneticoHttpServer = System.getenv("AG_SERVER_URL");

        if (urlAlgoritmoGeneticoHttpServer == null || urlAlgoritmoGeneticoHttpServer.isBlank()) {
            throw new AlgoritmoGeneticoHttpServerException("Variável de ambiente: AG_SERVER não configurada");
        }

        return urlAlgoritmoGeneticoHttpServer;
    }

    public void iniciarServidor() throws AlgoritmoGeneticoHttpServerException {
        try {
            server = HttpServer.create(new InetSocketAddress(hostname, porta), 0);
            server.createContext(url, this::processarRequisicao);
            server.start();

            log.info("Algoritmo Genético aguardando rotas na porta {}...", porta);
        } catch (IOException e) {
            throw new AlgoritmoGeneticoHttpServerException("Houve um erro de I/O ao subir o servidor", e);
        }
    }

    private void processarRequisicao(HttpExchange exchange) throws IOException {
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

    private void inicializarAlgoritmoGenetico(AlgoritmoGeneticoRequest response) {
        GerenciadorDistancias.inicializar(new CalculoDistanciaAPI(), response);

        var enderecos = response.enderecos().toArray(Endereco[]::new);

        var algoritmoGenetico = new AlgoritmoGenetico.Builder()
                .tamanhoPopulacao(response.tamanhoPopulacao())
                .qtdeGeracoes(response.quantidadeGeracoes())
                .enderecos(enderecos)
                .chanceOcorrenciaMutacao(response.chanceOcorrenciaMutacao())
                .chanceOcorrenciaCrossover(response.chanceOcorrenciaCrossover())
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
            var cidade = jsonNode.get("cidade").asText();

            enderecos.add(new Endereco(id, latitude, longitude, cidade));
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

        return new AlgoritmoGeneticoRequest(distancias, enderecos, tamanhoPopulacao, quantidadeGeracoes, chanceOcorrenciaMutacao, chanceOcorrenciaCrossover);
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
