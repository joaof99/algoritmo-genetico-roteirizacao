package com.genetico.client;

import com.genetico.exception.AlgoritmoGeneticoClientException;
import com.genetico.model.DistanciaResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class AlgoritmoGeneticoClientTest {

    @Test
    @DisplayName("Dados do algoritmo genético response devem ser extraídos corretamente do JSON")
    public void dadosDoAlgoritmoGeneticoResponseDevemSerExtraidosCorretamenteDoJson() throws AlgoritmoGeneticoClientException, IOException, InterruptedException {
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
                {
                  "enderecos": [
                    {
                      "id": 1,
                      "latitude": -23.5505,
                      "longitude": -46.6333,
                      "cidade": "São Paulo"
                    },
                    {
                      "id": 2,
                      "latitude": -90.5505,
                      "longitude": -80.6333,
                      "cidade": "Curitiba"
                    },
                    {
                      "id": 3,
                      "latitude": -70.5505,
                      "longitude": -60.6333,
                      "cidade": "Sorocaba"
                    },
                    {
                      "id": 4,
                      "latitude": -40.5505,
                      "longitude": -30.6333,
                      "cidade": "Balneário Camboriú"
                    }
                  ],
                  "distancias": [
                    {
                      "idOrigem": 1,
                      "idDestino": 2,
                      "distancia": 1000.50
                    },
                    {
                      "idOrigem": 1,
                      "idDestino": 3,
                      "distancia": 800.75
                    },
                    {
                      "idOrigem": 1,
                      "idDestino": 4,
                      "distancia": 600.25
                    },
                    {
                      "idOrigem": 2,
                      "idDestino": 1,
                      "distancia": 1000.50
                    },
                    {
                      "idOrigem": 2,
                      "idDestino": 3,
                      "distancia": 300.40
                    },
                    {
                      "idOrigem": 2,
                      "idDestino": 4,
                      "distancia": 500.60
                    },
                    {
                      "idOrigem": 3,
                      "idDestino": 1,
                      "distancia": 800.75
                    },
                    {
                      "idOrigem": 3,
                      "idDestino": 2,
                      "distancia": 300.40
                    },
                    {
                      "idOrigem": 3,
                      "idDestino": 4,
                      "distancia": 200.30
                    },
                    {
                      "idOrigem": 4,
                      "idDestino": 1,
                      "distancia": 600.25
                    },
                    {
                      "idOrigem": 4,
                      "idDestino": 2,
                      "distancia": 500.60
                    },
                    {
                      "idOrigem": 4,
                      "idDestino": 3,
                      "distancia": 200.30
                    }
                  ]
                }
                """);

        Mockito.when(httpClient.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn(response);

        try (var httpClientEstatico = Mockito.mockStatic(HttpClient.class)) {
            httpClientEstatico.when(HttpClient::newBuilder)
                    .thenReturn(httpClientBuilder);

            var agResponse = new AlgoritmoGeneticoClient().buscarDadosAlgoritmoGenetico(1);
            var enderecos = agResponse.enderecos();

            assertEquals(4, enderecos.size());

            assertEquals(1, enderecos.getFirst().id());
            assertEquals(-23.5505, enderecos.getFirst().latitude());
            assertEquals(-46.6333, enderecos.getFirst().longitude());
            assertEquals("São Paulo", enderecos.getFirst().descricao());

            assertEquals(2, enderecos.get(1).id());
            assertEquals(-90.5505, enderecos.get(1).latitude());
            assertEquals(-80.6333, enderecos.get(1).longitude());
            assertEquals("Curitiba", enderecos.get(1).descricao());

            assertEquals(3, enderecos.get(2).id());
            assertEquals(-70.5505, enderecos.get(2).latitude());
            assertEquals(-60.6333, enderecos.get(2).longitude());
            assertEquals("Sorocaba", enderecos.get(2).descricao());

            assertEquals(4, enderecos.get(3).id());
            assertEquals(-40.5505, enderecos.get(3).latitude());
            assertEquals(-30.6333, enderecos.get(3).longitude());
            assertEquals("Balneário Camboriú", enderecos.get(3).descricao());

            var distancias = agResponse.distancias();
            assertEquals(12, distancias.size());

            assertEquals(new DistanciaResponse(1, 2, 1000.50), distancias.get(0));
            assertEquals(new DistanciaResponse(1, 3, 800.75), distancias.get(1));
            assertEquals(new DistanciaResponse(1, 4, 600.25), distancias.get(2));

            assertEquals(new DistanciaResponse(2, 1, 1000.50), distancias.get(3));
            assertEquals(new DistanciaResponse(2, 3, 300.40), distancias.get(4));
            assertEquals(new DistanciaResponse(2, 4, 500.60), distancias.get(5));

            assertEquals(new DistanciaResponse(3, 1, 800.75), distancias.get(6));
            assertEquals(new DistanciaResponse(3, 2, 300.40), distancias.get(7));
            assertEquals(new DistanciaResponse(3, 4, 200.30), distancias.get(8));

            assertEquals(new DistanciaResponse(4, 1, 600.25), distancias.get(9));
            assertEquals(new DistanciaResponse(4, 2, 500.60), distancias.get(10));
            assertEquals(new DistanciaResponse(4, 3, 200.30), distancias.get(11));
        }
    }

    @Test
    @DisplayName("Deve disparar erro caso seja retornado quantidade de distâncias incorretas")
    public void deveDisparrErroCasoSejaRetornadoQuantidadeDistanciasIncorretas() throws IOException, InterruptedException {
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
                {
                  "enderecos": [
                    {
                      "id": 1,
                      "latitude": -23.5505,
                      "longitude": -46.6333,
                      "cidade": "São Paulo"
                    },
                    {
                      "id": 2,
                      "latitude": -90.5505,
                      "longitude": -80.6333,
                      "cidade": "Curitiba"
                    },
                    {
                      "id": 3,
                      "latitude": -70.5505,
                      "longitude": -60.6333,
                      "cidade": "Sorocaba"
                    },
                    {
                      "id": 4,
                      "latitude": -40.5505,
                      "longitude": -30.6333,
                      "cidade": "Balneário Camboriú"
                    }
                  ],
                  "distancias": [
                    {
                      "idOrigem": 1,
                      "idDestino": 2,
                      "distancia": 1000.50
                    },
                    {
                      "idOrigem": 1,
                      "idDestino": 3,
                      "distancia": 800.75
                    },
                    {
                      "idOrigem": 1,
                      "idDestino": 4,
                      "distancia": 600.25
                    },
                    {
                      "idOrigem": 2,
                      "idDestino": 1,
                      "distancia": 1000.50
                    },
                    {
                      "idOrigem": 2,
                      "idDestino": 3,
                      "distancia": 300.40
                    },
                    {
                      "idOrigem": 2,
                      "idDestino": 4,
                      "distancia": 500.60
                    },
                    {
                      "idOrigem": 3,
                      "idDestino": 1,
                      "distancia": 800.75
                    },
                    {
                      "idOrigem": 3,
                      "idDestino": 2,
                      "distancia": 300.40
                    },
                    {
                      "idOrigem": 3,
                      "idDestino": 4,
                      "distancia": 200.30
                    },
                    {
                      "idOrigem": 4,
                      "idDestino": 1,
                      "distancia": 600.25
                    },
                    {
                      "idOrigem": 4,
                      "idDestino": 2,
                      "distancia": 500.60
                    }
                  ]
                }
                """);

        Mockito.when(httpClient.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn(response);

        try (var httpClientEstatico = Mockito.mockStatic(HttpClient.class)) {
            httpClientEstatico.when(HttpClient::newBuilder)
                    .thenReturn(httpClientBuilder);

            var exception = assertThrows(
                    AlgoritmoGeneticoClientException.class,
                    () -> new AlgoritmoGeneticoClient().buscarDadosAlgoritmoGenetico(1)
            );

            assertEquals("A quantidade de distâncias da resposta deveria ser 12 porém foi encontrado 11", exception.getMessage());
        }
    }
}

