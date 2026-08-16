package com.genetico.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genetico.exception.RotaClientException;
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
    private final HttpClient HTTP_CLIENT;
    private final String URL_BASE;
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public RotaClient() throws RotaClientException {
        URL_BASE = definirUrlBase();
        HTTP_CLIENT = inicializarHttpClient();
    }

    private String definirUrlBase() throws RotaClientException {
        var urlAgAdministrativo = "AG_ADMINISTRATIVO_URL";
        if (System.getenv(urlAgAdministrativo) == null) {
            throw new RotaClientException(String.format("Variável de ambiente: %s não configurada", urlAgAdministrativo));
        }

        return System.getenv(urlAgAdministrativo);
    }

    private HttpClient inicializarHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public List<Endereco> buscarTodosEnderecos() throws RotaClientException {
        var response = buscarRotas();

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RotaClientException(String.format("AG administrativo retornou HTTP %d", response.statusCode()));
        }

        var json = parsearResposta(response);

        return extrairEnderecos(json);
    }

    private HttpResponse<String> buscarRotas() throws RotaClientException {
        try {
            var uri = URI.create(URL_BASE + "/rotas/todas");
            log.info("Buscando rotas em {} ", uri);

            var request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RotaClientException(String.format("Thread interrompida ao buscar rotas em %s", URL_BASE), e);
        } catch (IOException e) {
            throw new RotaClientException(String.format("Não foi possível conectar ao AG administrativo em %s", URL_BASE), e);
        }
    }

    private JsonNode parsearResposta(HttpResponse<String> response) throws RotaClientException {
        try {
            return OBJECT_MAPPER.readTree(response.body());
        } catch (JsonProcessingException e) {
            throw new RotaClientException(String.format("Resposta inválida do AG Administrativo %d", response.statusCode()), e);
        }
    }

    private static List<Endereco> extrairEnderecos(JsonNode json) {
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

