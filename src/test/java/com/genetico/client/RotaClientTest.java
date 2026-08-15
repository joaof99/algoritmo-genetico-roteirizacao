package com.genetico.client;

import com.genetico.exception.RotaClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class RotaClientTest {

    @Test
    @DisplayName("Endereços devem ser extraídos corretamente do JSON")
    public void enderecosDevemSerExtraidosCorretamenteDoJson() throws RotaClientException, IOException, InterruptedException {
        var httpClient = Mockito.mock(HttpClient.class);
        var httpClientBuilder = Mockito.mock(HttpClient.Builder.class);
        var response = Mockito.mock(HttpResponse.class);

        Mockito.when(httpClientBuilder.connectTimeout(any(Duration.class)))
                .thenReturn(httpClientBuilder);

        Mockito.when(httpClientBuilder.build())
                .thenReturn(httpClient);

        Mockito.when(response.statusCode())
                .thenReturn(200);

        Mockito.when(response.body())
                .thenReturn("""
                        [
                          {
                            "enderecos": [
                              {
                                "id": 1,
                                "latitude": -23.5505,
                                "longitude": -46.6333,
                                "cidade": "São Paulo"
                              }
                            ]
                          }
                        ]
                        """);

        Mockito.when(httpClient.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn(response);

        try (var httpClientEstatico = Mockito.mockStatic(HttpClient.class)) {
            httpClientEstatico.when(HttpClient::newBuilder)
                    .thenReturn(httpClientBuilder);

            var rotaClient = new RotaClient();

            var enderecos = rotaClient.buscarTodosEnderecos();

            assertEquals(1, enderecos.size());
            assertEquals(1, enderecos.getFirst().id());
            assertEquals(-23.5505, enderecos.getFirst().latitude());
            assertEquals(-46.6333, enderecos.getFirst().longitude());
            assertEquals("São Paulo", enderecos.getFirst().descricao());
        }
    }
}

