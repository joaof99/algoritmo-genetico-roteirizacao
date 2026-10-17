package com.genetico.aginterface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.genetico.model.EstatisticasFitness;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AgInterfaceClient {
    private static final Logger log = LogManager.getLogger();
    private static final String VARIAVEL_AMBIENTE_URL_BASE = "AG_INTERFACE_URL";
    private static final String URL_BASE_PADRAO = "http://localhost:8080";
    private static final int QUANTIDADE_MAXIMA_TENTATIVAS = 6;
    private static final Duration TEMPO_LIMITE_REQUISICAO = Duration.ofSeconds(10);

    private final HttpClient httpClient;
    private final RoteirizacaoBodyBuilder roteirizacaoBodyBuilder;
    private final Duration intervaloInicialEntreTentativas;

    public AgInterfaceClient() {
        this(HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(2))
                        .build(),
                new RoteirizacaoBodyBuilder(),
                Duration.ofSeconds(2));
    }

    AgInterfaceClient(HttpClient httpClient, RoteirizacaoBodyBuilder roteirizacaoBodyBuilder, Duration intervaloInicialEntreTentativas) {
        this.httpClient = httpClient;
        this.roteirizacaoBodyBuilder = roteirizacaoBodyBuilder;
        this.intervaloInicialEntreTentativas = intervaloInicialEntreTentativas;
    }

    public void enviarConclusao(int idRoteirizacao, EstatisticasFitness estatisticasFitness, int[] melhorRota) {
        try {
            enviar(idRoteirizacao, "concluir", roteirizacaoBodyBuilder.montarConclusao(estatisticasFitness, melhorRota));
        } catch (JsonProcessingException e) {
            log.error("Erro ao montar o corpo da conclusão da roteirização de ID {}", idRoteirizacao, e);
        }
    }

    public void enviarFalha(int idRoteirizacao, String mensagem) {
        try {
            enviar(idRoteirizacao, "falhar", roteirizacaoBodyBuilder.montarFalha(mensagem));
        } catch (JsonProcessingException e) {
            log.error("Erro ao montar o corpo da falha da roteirização de ID {}", idRoteirizacao, e);
        }
    }

    private void enviar(int idRoteirizacao, String acao, String corpo) {
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/api/roteirizacao/%d/%s", obterUrlBase(), idRoteirizacao, acao)))
                .timeout(TEMPO_LIMITE_REQUISICAO)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(corpo))
                .build();

        var intervaloEntreTentativas = intervaloInicialEntreTentativas;

        try {
            for (int tentativa = 1; tentativa <= QUANTIDADE_MAXIMA_TENTATIVAS; tentativa++) {
                try {
                    var resposta = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                    if (resposta.statusCode() < 500) {
                        if (resposta.statusCode() >= 300) {
                            log.error("AgInterface recusou a requisição {}. Status: {}, corpo: {}", httpRequest.uri(), resposta.statusCode(), resposta.body());
                        }

                        return;
                    }

                    log.warn("AgInterface retornou erro na tentativa {} de {} da requisição {}. Status: {}, corpo: {}",
                            tentativa, QUANTIDADE_MAXIMA_TENTATIVAS, httpRequest.uri(), resposta.statusCode(), resposta.body());
                } catch (IOException e) {
                    log.warn("Erro de I/O na tentativa {} de {} da requisição {}", tentativa, QUANTIDADE_MAXIMA_TENTATIVAS, httpRequest.uri(), e);
                }

                if (tentativa < QUANTIDADE_MAXIMA_TENTATIVAS) {
                    Thread.sleep(intervaloEntreTentativas.toMillis());
                    intervaloEntreTentativas = intervaloEntreTentativas.multipliedBy(2);
                }
            }

            log.error("Não foi possível enviar ao AgInterface a requisição {} após {} tentativas", httpRequest.uri(), QUANTIDADE_MAXIMA_TENTATIVAS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Envio da requisição {} ao AgInterface foi interrompido", httpRequest.uri(), e);
        }
    }

    private String obterUrlBase() {
        var urlConfigurada = System.getenv(VARIAVEL_AMBIENTE_URL_BASE);
        return urlConfigurada == null || urlConfigurada.isBlank() ? URL_BASE_PADRAO : urlConfigurada;
    }
}
