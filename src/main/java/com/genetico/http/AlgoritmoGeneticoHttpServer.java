package com.genetico.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genetico.AlgoritmoGenetico;
import com.genetico.distancia.CalculadorDistancias;
import com.genetico.distancia.CalculoDistanciaAPI;
import com.genetico.exception.AlgoritmoGeneticoHttpServerException;
import com.genetico.model.AlgoritmoGeneticoResponse;
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

    private static final Logger log = LogManager.getLogger(AlgoritmoGeneticoHttpServer.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final int porta;
    private HttpServer server;
    private String hostname;

    public AlgoritmoGeneticoHttpServer(int porta, String hostname) {
        this.porta = porta;
        this.hostname = hostname;
    }

    public void iniciarServidor() throws AlgoritmoGeneticoHttpServerException {
        try {
            server = HttpServer.create(new InetSocketAddress(hostname, porta), 0);
            server.createContext("/ag/iniciar", this::processarRequisicao);
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

    private void inicializarAlgoritmoGenetico(AlgoritmoGeneticoResponse response) {
        CalculadorDistancias.inicializar(new CalculoDistanciaAPI(), response);

        var algoritmoGenetico = new AlgoritmoGenetico.Builder()
                .tamanhoPopulacao(30)
                .qtdeGeracoes(50)
                .qtdeGenesCromossomo(response.enderecos().size())
                .chanceOcorrenciaMutacao(50)
                .chanceOcorrenciaCrossover(50)
                .build();

        var populacaoFinal = algoritmoGenetico.reproduzir();

        log.info("População final:");

        populacaoFinal.imprimirPopulacao();
    }

    private AlgoritmoGeneticoResponse extrairDadosAlgoritmoGenetico(JsonNode json) {
        var enderecos = new ArrayList<Endereco>();
        var distancias = new ArrayList<DistanciaResponse>();

        json.get("enderecos").forEach(jsonNode -> {
            var id = jsonNode.get("id").asInt();
            var latitude = jsonNode.get("latitude").asDouble();
            var longitude = jsonNode.get("longitude").asDouble();
            var cidade = jsonNode.get("cidade").asText();

            enderecos.add(
                    new Endereco(
                            id,
                            latitude,
                            longitude,
                            cidade
                    )
            );
        });

        json.get("distancias").forEach(jsonNode -> {
            var idOrigem = jsonNode.get("idOrigem").asInt();
            var idDestino = jsonNode.get("idDestino").asInt();
            var distancia = jsonNode.get("distancia").asDouble();

            distancias.add(new DistanciaResponse(idOrigem, idDestino, distancia)
            );
        });

        log.info("Foram encontradas {} endereços e {} distâncias", enderecos.size(), distancias.size());

        return new AlgoritmoGeneticoResponse(distancias, enderecos);
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
