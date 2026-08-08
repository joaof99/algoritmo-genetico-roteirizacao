package com.genetico.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genetico.model.Endereco;
import com.genetico.model.Rota;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class RotaClient {
    private static final String URL_BASE = System.getenv("AG_ADMINISTRATIVO_URL") != null
            ? System.getenv("AG_ADMINISTRATIVO_URL")
            : "http://localhost:8080";

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public List<Endereco> buscarTodosEnderecos() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/rotas/todas"))
                .GET()
                .build();

        HttpResponse<String> response;

        try {
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        JsonNode json;
        try {
            json = OBJECT_MAPPER.readTree(response.body());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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

        return rotas
                .stream()
                .flatMap(rota -> rota.enderecos().stream())
                .distinct()
                .toList();
    }
}

