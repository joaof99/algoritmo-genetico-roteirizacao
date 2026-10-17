package com.genetico.service;

import com.genetico.factory.AgInterfaceClientFactory;
import com.genetico.factory.GraficoServiceFactory;
import com.genetico.model.AlgoritmoGeneticoRequest;
import com.genetico.model.DistanciaRequest;
import com.genetico.model.Endereco;
import com.genetico.aginterface.AgInterfaceClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RoteirizacaoServiceTest {
    private final RoteirizacaoService roteirizacaoService = new RoteirizacaoService();
    private MockedStatic<GraficoServiceFactory> graficoServiceFactory;
    private MockedStatic<AgInterfaceClientFactory> agInterfaceClientFactory;
    private AgInterfaceClient agInterfaceClient;

    @BeforeEach
    void setUp() {
        graficoServiceFactory = Mockito.mockStatic(GraficoServiceFactory.class);
        graficoServiceFactory.when(GraficoServiceFactory::getGraficoService)
                .thenReturn(mock(GraficoService.class));

        agInterfaceClientFactory = Mockito.mockStatic(AgInterfaceClientFactory.class);
        agInterfaceClient = mock(AgInterfaceClient.class);
        agInterfaceClientFactory.when(AgInterfaceClientFactory::getAgInterfaceClient)
                .thenReturn(agInterfaceClient);
    }

    @AfterEach
    void tearDown() {
        graficoServiceFactory.close();
        agInterfaceClientFactory.close();
    }

    @Test
    @DisplayName("Deve enviar ao AgInterface a conclusão com a melhor rota contendo todos os endereços")
    void deveEnviarConclusaoComMelhorRotaContendoTodosOsEnderecos() {
        var enderecos = List.of(
                new Endereco(1, -23.5, -46.6),
                new Endereco(2, -23.6, -46.7)
        );

        var distancias = List.of(
                new DistanciaRequest(1, 2, 10.0),
                new DistanciaRequest(2, 1, 10.0)
        );

        roteirizacaoService.executar(new AlgoritmoGeneticoRequest(7, distancias, enderecos, 2, 1, 0, 0));

        var melhorRotaCaptor = ArgumentCaptor.forClass(int[].class);
        verify(agInterfaceClient).enviarConclusao(eq(7), any(), melhorRotaCaptor.capture());

        var melhorRotaOrdenada = melhorRotaCaptor.getValue().clone();
        Arrays.sort(melhorRotaOrdenada);
        assertArrayEquals(new int[]{1, 2}, melhorRotaOrdenada);
    }

    @Test
    @DisplayName("Deve enviar ao AgInterface a falha quando a roteirização não puder ser concluída")
    void deveEnviarFalhaQuandoRoteirizacaoNaoPuderSerConcluida() {
        var enderecosSemDistancias = List.of(
                new Endereco(50, -23.5, -46.6),
                new Endereco(60, -23.6, -46.7)
        );

        roteirizacaoService.executar(new AlgoritmoGeneticoRequest(7, List.of(), enderecosSemDistancias, 2, 1, 0, 0));

        verify(agInterfaceClient).enviarFalha(eq(7), contains("Distância não encontrada"));
    }
}
