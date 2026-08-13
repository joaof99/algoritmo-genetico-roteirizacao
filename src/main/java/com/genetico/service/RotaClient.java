package com.genetico.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genetico.model.Endereco;
import com.genetico.model.Rota;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RotaClient {
    private static final Logger log = LogManager.getLogger();
    private static final String URL_BASE = System.getenv("AG_ADMINISTRATIVO_URL") != null
            ? System.getenv("AG_ADMINISTRATIVO_URL")
            : "http://localhost:8080";

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public List<Endereco> buscarTodosEnderecos() {
        HttpResponse<String> response;

        try {
            var uri = URI.create(URL_BASE + "/rotas/todas");
            log.info("Buscando rotas em {} ", uri.toString());

            var request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Thread interrompida ao buscar rotas em {}", URL_BASE, e);

            throw new RuntimeException("Busca de rotas interrompida", e);
        } catch (IOException e) {
            log.error("Erro de I/O ao buscar rotas em {}", URL_BASE, e);

            throw new RuntimeException("Não foi possível buscar as rotas", e);
        }

        JsonNode json;
        try {
            json = OBJECT_MAPPER.readTree(response.body());
        } catch (JsonProcessingException e) {
            log.error("Resposta inválida do serviço de rotas. HTTP status: {}", response.statusCode(), e);

            throw new RuntimeException("Resposta inválida do serviço de rotas", e);
        }

        var rotas = new ArrayList<Rota>();

        json.forEach(rota -> {
            var enderecos = new ArrayList<Endereco>();

            rota.get("enderecos").forEach(endereco -> {
                var id = endereco.get("id").asInt();
                var latitude = endereco.get("latitude").asDouble();
                var longitude = endereco.get("longitude").asDouble();
                var cidade = endereco.get("cidade").asText();

                enderecos.add(new Endereco(id, latitude, longitude, cidade));
            });

            rotas.add(new Rota(enderecos));
        });

        var enderecos = rotas
                .stream()
                .flatMap(rota -> rota.enderecos().stream())
                .distinct()
                .toList();

        log.info("Foram encontradas {} endereços", enderecos.size());

        return enderecos;
    }
}

