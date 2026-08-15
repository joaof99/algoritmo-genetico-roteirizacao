package com.genetico;

import com.genetico.distancia.Haversine;
import com.genetico.model.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HaversineTest {
    @Test
    @DisplayName("Deve calcular a distância de Haversine corretamente")
    public void deveCalcularDistanciaHaversineCorretamente() {
        var haversine = new Haversine();
        var sp = new Endereco(1, -23.5505, -46.6333, "");
        var rj = new Endereco(2, -22.9068, -43.1729, "");
        assertEquals(360.74882490989955, haversine.calcularDistancia(sp, rj));
    }
}