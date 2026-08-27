package com.genetico.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genetico.exception.RotaClientException;
import com.genetico.model.DistanciaResponse;
import com.genetico.model.Endereco;
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
import java.util.stream.Collectors;

public class RotaClient {
    private static final Logger log = LogManager.getLogger();
    private final HttpClient httpClient;
    private final String urlBase;
    private final ObjectMapper objectMapper;

    public RotaClient() throws RotaClientException {
        urlBase = definirUrlBase();
        httpClient = inicializarHttpClient();
        objectMapper = new ObjectMapper();
    }

    private String definirUrlBase() throws RotaClientException {
        var urlAgAdministrativo = System.getenv("AG_ADMINISTRATIVO_URL");

        if (urlAgAdministrativo == null || urlAgAdministrativo.isBlank()) {
            throw new RotaClientException(String.format("Variável de ambiente: %s não configurada", urlAgAdministrativo));
        }

        return urlAgAdministrativo;
    }

    private HttpClient inicializarHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public List<Endereco> buscarEnderecosRota(int idRota) throws RotaClientException {
        var response = buscarEnderecos(idRota);

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RotaClientException(String.format("AG administrativo retornou HTTP %d", response.statusCode()));
        }

        var json = parsearResposta(response);

        return extrairEnderecos(json);
    }

    private HttpResponse<String> buscarEnderecos(int idRota) throws RotaClientException {
        try {
            var uri = URI.create(urlBase + "/rotas/" + idRota + "/enderecos");
            log.info("Buscando endereços para a rota de ID {} em {} ", idRota, uri);

            var request = criarRequest(uri);

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RotaClientException(String.format("Thread interrompida ao buscar rotas em %s", urlBase), e);
        } catch (IOException e) {
            throw new RotaClientException(String.format("Não foi possível conectar ao AG administrativo em %s", urlBase), e);
        }
    }

    private HttpRequest criarRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
    }

    public double buscarDistanciaEntreEnderecosApi(int origemId, int destinoId) throws RotaClientException {
        var response = buscarDistanciaAPI(origemId, destinoId);

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RotaClientException(String.format("AG administrativo retornou HTTP %d ao buscar distância", response.statusCode()));
        }

        try {
            return Double.parseDouble(response.body());
        } catch (NumberFormatException e) {
            throw new RotaClientException(String.format("Resposta inválida ao buscar distância entre %d e %d: %s", origemId, destinoId, response.body()), e);
        }
    }

    private HttpResponse<String> buscarDistanciaAPI(int origemId, int destinoId) throws RotaClientException {
        try {
            var uri = URI.create(urlBase + "/distancias/" + origemId + "/" + destinoId);
            log.info("Buscando distância entre endereços {} e {} em {}", origemId, destinoId, uri);

            return httpClient.send(criarRequest(uri), HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RotaClientException(String.format("Thread interrompida ao buscar distância em %s", urlBase), e);
        } catch (IOException e) {
            throw new RotaClientException(String.format("Não foi possível conectar ao AG administrativo em %s", urlBase), e);
        }
    }

    public List<DistanciaResponse> buscarDistanciasEnderecos(List<Integer> idsEnderecos) throws RotaClientException {
        if (idsEnderecos.isEmpty()) {
            throw new RotaClientException("Lista de IDs de endereços não pode ser vazia");
        }

        log.info("Buscando matriz de distâncias para {} endereços", idsEnderecos.size());

        try {
            var idsFormatados = idsEnderecos.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            var uri = URI.create(urlBase + "/distancias/matrix?idsEnderecos=" + idsFormatados);
            var response = httpClient.send(criarRequest(uri), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RotaClientException(String.format(
                        "AG administrativo retornou HTTP %d ao buscar matriz de distâncias",
                        response.statusCode()));
            }

            return objectMapper.readValue(response.body(),
                    new TypeReference<>() {});
        } catch (IOException e) {
            throw new RotaClientException("Erro de comunicação ao buscar matriz de distâncias", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RotaClientException("Thread interrompida ao buscar matriz de distâncias", e);
        }
    }

    private JsonNode parsearResposta(HttpResponse<String> response) throws RotaClientException {
        try {
            return objectMapper.readTree(response.body());
        } catch (JsonProcessingException e) {
            throw new RotaClientException(String.format("Resposta inválida do AG Administrativo %d", response.statusCode()), e);
        }
    }

    private static List<Endereco> extrairEnderecos(JsonNode json) {
        var enderecos = new ArrayList<Endereco>();

        json.forEach(endereco -> {
            var id = endereco.get("id").asInt();
            var latitude = endereco.get("latitude").asDouble();
            var longitude = endereco.get("longitude").asDouble();
            var cidade = endereco.get("cidade").asText();

            enderecos.add(new Endereco(id, latitude, longitude, cidade));
        });

        log.info("Foram encontradas {} endereços", enderecos.size());
        return enderecos;
    }
}

