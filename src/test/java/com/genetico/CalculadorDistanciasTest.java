package com.genetico;

import com.genetico.model.Endereco;
import com.genetico.model.Rota;
import com.genetico.service.RotaClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CalculadorDistanciasTest {

    @Test
    @DisplayName("Deve calcular a distância de Haversine corretamente")
    public void deveCalcularDistanciaHaversineCorretamente() {
        var sp = new Endereco(1L, -23.5505, -46.6333, "");
        var rj = new Endereco(2L, -22.9068, -43.1729, "");
        assertEquals(360.74882490989955, CalculadorDistancias.calcularDistanciaHaversine(sp, rj));
    }

    @Test
    @DisplayName("Matriz de distâncias deve ser criada corretamente")
    public void matrizDeDistanciasDeveSerCriadaCorretamente() throws IOException, InterruptedException {
        var rotaClient = mock(RotaClient.class);
        var enderecos = List.of(new Endereco(1L, -3.2, -4.5, "Hospital"), new Endereco(3L, -90.3, -50.9, "Barbearia"));
        var rota = new Rota(enderecos);

        when(rotaClient.buscarTodasRotas()).thenReturn(List.of(rota));

        var calculadorDistancias = new CalculadorDistancias(rotaClient);
        calculadorDistancias.inicializarDistanciasHaversine();
    }
}