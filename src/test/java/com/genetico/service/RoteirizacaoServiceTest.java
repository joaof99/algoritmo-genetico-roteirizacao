package com.genetico.service;

import com.genetico.factory.VaadinClientFactory;
import com.genetico.factory.GraficoServiceFactory;
import com.genetico.model.AlgoritmoGeneticoRequest;
import com.genetico.model.DistanciaResponse;
import com.genetico.model.Endereco;
import com.genetico.vaadin.VaadinClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RoteirizacaoServiceTest {
    private final RoteirizacaoService roteirizacaoService = new RoteirizacaoService();
    private MockedStatic<GraficoServiceFactory> graficoServiceFactory;
    private MockedStatic<VaadinClientFactory> vaadinClientFactory;
    private VaadinClient vaadinClient;

    @BeforeEach
    void setUp() {
        graficoServiceFactory = Mockito.mockStatic(GraficoServiceFactory.class);
        graficoServiceFactory.when(GraficoServiceFactory::getGraficoService)
                .thenReturn(mock(GraficoService.class));

        vaadinClientFactory = Mockito.mockStatic(VaadinClientFactory.class);
        vaadinClient = mock(VaadinClient.class);
        vaadinClientFactory.when(VaadinClientFactory::getVaadinClient)
                .thenReturn(vaadinClient);
    }

    @AfterEach
    void tearDown() {
        graficoServiceFactory.close();
        vaadinClientFactory.close();
    }

    @Test
    @DisplayName("Deve executar o algoritmo genético e notificar o Vaadin com o resultado")
    void deveExecutarAlgoritmoGeneticoENotificarVaadin() {
        var enderecos = List.of(
                new Endereco(1, -23.5, -46.6),
                new Endereco(2, -23.6, -46.7)
        );
        var distancias = List.of(
                new DistanciaResponse(1, 2, 10.0),
                new DistanciaResponse(2, 1, 10.0)
        );

        var request = new AlgoritmoGeneticoRequest(7, distancias, enderecos, 2, 1, 0, 0);

        roteirizacaoService.executar(request);

        verify(vaadinClient).enviarResultadoRoteirizacao(eq(request), any());
    }
}
