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
        var sp = new Endereco(1, -23.5505, -46.6333, "");
        var rj = new Endereco(2, -22.9068, -43.1729, "");
        assertEquals(360.74882490989955, CalculadorDistancias.calcularDistanciaHaversine(sp, rj));
    }

    @Test
    @DisplayName("Matriz de distâncias deve ser criada corretamente")
    public void matrizDeDistanciasDeveSerCriadaCorretamente() throws IOException, InterruptedException {
        var rotaClient = mock(RotaClient.class);

        var hospital = new Endereco(1, -5.3, -8.2, "Hospital");
        var barbearia = new Endereco(3, -90.3, -50.9, "Barbearia");
        var shopping  = new Endereco(2, -90.3, -50.9, "Shopping");
        var padaria = new Endereco(4, -90.3, -50.9, "Padaria");
        var armazem = new Endereco(5, -90.3, -50.9, "Armazém");

        var enderecos = List.of(hospital, barbearia, shopping, padaria, armazem);
        var rota = new Rota(enderecos);

        when(rotaClient.buscarTodasRotas()).thenReturn(List.of(rota));

        var calculadorDistancias = new CalculadorDistancias(rotaClient);

        assertEquals(20, calculadorDistancias.getMatrizDistancias().getQuantidadeDistancias());
    }
}