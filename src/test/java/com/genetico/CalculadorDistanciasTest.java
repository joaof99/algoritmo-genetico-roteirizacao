package com.genetico;

import com.genetico.model.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadorDistanciasTest {

    @Test
    @DisplayName("Deve calcular a distância de Haversine corretamente")
    public void deveCalcularDistanciaHaversineCorretamente() {
        var sp = new Endereco(1L, -23.5505, -46.6333, "");
        var rj = new Endereco(2L, -22.9068, -43.1729, "");
        assertEquals(360.74882490989955, CalculadorDistancias.calcularDistanciaHaversine(sp, rj));
    }
}