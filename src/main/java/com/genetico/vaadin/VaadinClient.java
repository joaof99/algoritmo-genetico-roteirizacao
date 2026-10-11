package com.genetico.vaadin;

import com.genetico.model.AlgoritmoGeneticoRequest;
import com.genetico.model.EstatisticasFitness;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class VaadinClient {
    private static final Logger log = LogManager.getLogger();
    private static final String VARIAVEL_AMBIENTE_URL_ENDPOINT = "VAADIN_ROTEIRIZACAO_URL";
    private static final String URL_ENDPOINT_PADRAO = "http://localhost:8080/roteirizacao/armazenar";

    private final HttpClient httpClient;
    private final RoteirizacaoBodyBuilder roteirizacaoBodyBuilder;

    public VaadinClient() {
        this(HttpClient.newHttpClient(), new RoteirizacaoBodyBuilder());
    }

    VaadinClient(HttpClient httpClient, RoteirizacaoBodyBuilder roteirizacaoBodyBuilder) {
        this.httpClient = httpClient;
        this.roteirizacaoBodyBuilder = roteirizacaoBodyBuilder;
    }

    public void enviarResultadoRoteirizacao(AlgoritmoGeneticoRequest request, EstatisticasFitness estatisticasFitness) {
        try {
            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(obterUrlEndpoint()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(roteirizacaoBodyBuilder.montar(request, estatisticasFitness)))
                    .build();

            var resposta = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (resposta.statusCode() >= 400) {
                log.error("Vaadin retornou erro ao armazenar a roteirização. Status: {}, corpo: {}", resposta.statusCode(), resposta.body());
            }
        } catch (IOException e) {
            log.error("Erro de I/O ao enviar resultado da roteirização ao projeto Vaadin", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Envio do resultado da roteirização ao projeto Vaadin foi interrompido", e);
        }
    }

    private String obterUrlEndpoint() {
        var urlConfigurada = System.getenv(VARIAVEL_AMBIENTE_URL_ENDPOINT);
        return urlConfigurada == null || urlConfigurada.isBlank() ? URL_ENDPOINT_PADRAO : urlConfigurada;
    }
}
