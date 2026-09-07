package com.genetico.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genetico.exception.AlgoritmoGeneticoClientException;
import com.genetico.model.AlgoritmoGeneticoResponse;
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

public class AlgoritmoGeneticoClient {
    private static final Logger log = LogManager.getLogger();
    private final HttpClient httpClient;
    private final String urlBase;
    private final ObjectMapper objectMapper;

    public AlgoritmoGeneticoClient() throws AlgoritmoGeneticoClientException {
        urlBase = definirUrlBase();
        httpClient = inicializarHttpClient();
        objectMapper = new ObjectMapper();
    }

    private String definirUrlBase() throws AlgoritmoGeneticoClientException {
        var urlAgAdministrativo = System.getenv("AG_ADMINISTRATIVO_URL");

        if (urlAgAdministrativo == null || urlAgAdministrativo.isBlank()) {
            throw new AlgoritmoGeneticoClientException("Variável de ambiente: AG_ADMINISTRATIVO_URL não configurada");
        }

        return urlAgAdministrativo;
    }

    private HttpClient inicializarHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public AlgoritmoGeneticoResponse buscarDadosAlgoritmoGenetico(int idRota) throws AlgoritmoGeneticoClientException {
        var response = buscarDados(idRota);

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new AlgoritmoGeneticoClientException(String.format("AG administrativo retornou HTTP %d", response.statusCode()));
        }

        var json = parsearResposta(response);

        return extrairDadosAlgoritmoGenetico(json);
    }

    private HttpResponse<String> buscarDados(int idRota) throws AlgoritmoGeneticoClientException {
        try {
            var uri = URI.create(urlBase + "/rotas/" + idRota + "/enderecos");
            log.info("Buscando dados do Algoritmo Genético para a rota de ID {} em {} ", idRota, uri);

            var request = criarRequest(uri);

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AlgoritmoGeneticoClientException(String.format("Thread interrompida ao buscar dados do Algoritmo Genético em %s", urlBase), e);
        } catch (IOException e) {
            throw new AlgoritmoGeneticoClientException(String.format("Não foi possível conectar ao AG administrativo em %s", urlBase), e);
        }
    }

    private HttpRequest criarRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
    }

    public double buscarDistanciaEntreEnderecosApi(int origemId, int destinoId) throws AlgoritmoGeneticoClientException {
        var response = buscarDistanciaAPI(origemId, destinoId);

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new AlgoritmoGeneticoClientException(String.format("AG administrativo retornou HTTP %d ao buscar distância", response.statusCode()));
        }

        try {
            return Double.parseDouble(response.body());
        } catch (NumberFormatException e) {
            throw new AlgoritmoGeneticoClientException(String.format("Resposta inválida ao buscar distância entre %d e %d: %s", origemId, destinoId, response.body()), e);
        }
    }

    private HttpResponse<String> buscarDistanciaAPI(int origemId, int destinoId) throws AlgoritmoGeneticoClientException {
        try {
            var uri = URI.create(urlBase + "/distancias/" + origemId + "/" + destinoId);
            log.info("Buscando distância entre endereços {} e {} em {}", origemId, destinoId, uri);

            return httpClient.send(criarRequest(uri), HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AlgoritmoGeneticoClientException(String.format("Thread interrompida ao buscar distância em %s", urlBase), e);
        } catch (IOException e) {
            throw new AlgoritmoGeneticoClientException(String.format("Não foi possível conectar ao AG administrativo em %s", urlBase), e);
        }
    }

    public List<DistanciaResponse> buscarDistanciasEnderecos(List<Integer> idsEnderecos) throws AlgoritmoGeneticoClientException {
        if (idsEnderecos.isEmpty()) {
            throw new AlgoritmoGeneticoClientException("Lista de IDs de endereços não pode ser vazia");
        }

        var quantidadeEnderecos = idsEnderecos.size();
        if (quantidadeEnderecos > 25) {
            throw new AlgoritmoGeneticoClientException(String.format("O máximo de endereços suportado numa requisição é 25. Encontrado: %d", quantidadeEnderecos));
        }

        log.info("Buscando matriz de distâncias para {} endereços", idsEnderecos.size());

        try {
            var idsFormatados = idsEnderecos.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            var uri = URI.create(urlBase + "/distancias/matrix?idsEnderecos=" + idsFormatados);
            var response = httpClient.send(criarRequest(uri), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new AlgoritmoGeneticoClientException(String.format(
                        "AG administrativo retornou HTTP %d ao buscar matriz de distâncias",
                        response.statusCode()));
            }

            return objectMapper.readValue(response.body(),
                    new TypeReference<>() {
                    });
        } catch (IOException e) {
            throw new AlgoritmoGeneticoClientException("Erro de comunicação ao buscar matriz de distâncias", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AlgoritmoGeneticoClientException("Thread interrompida ao buscar matriz de distâncias", e);
        }
    }

    private JsonNode parsearResposta(HttpResponse<String> response) throws AlgoritmoGeneticoClientException {
        try {
            return objectMapper.readTree(response.body());
        } catch (JsonProcessingException e) {
            throw new AlgoritmoGeneticoClientException(String.format("Resposta inválida do AG Administrativo %d", response.statusCode()), e);
        }
    }

    private static AlgoritmoGeneticoResponse extrairDadosAlgoritmoGenetico(JsonNode json) {
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

        return new AlgoritmoGeneticoResponse(distancias, enderecos);
    }

}

