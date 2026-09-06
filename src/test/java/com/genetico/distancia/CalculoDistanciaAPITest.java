package com.genetico.distancia;

import com.genetico.model.AlgoritmoGeneticoResponse;
import com.genetico.model.DistanciaResponse;
import com.genetico.model.MatrizDistancias;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculoDistanciaAPITest {

    @BeforeEach
    public void beforeEach() {
        MatrizDistancias.getDistancias().clear();
    }

    @Test
    @DisplayName("Deve ser calculado todas as distâncias possíveis")
    public void deveSerCalculadoTodasAsDistanciasPossiveis() {
        var distancias = List.of(
                new DistanciaResponse(1, 2, 800),
                new DistanciaResponse(1, 3, 800),
                new DistanciaResponse(2, 1, 800),
                new DistanciaResponse(2, 3, 800),
                new DistanciaResponse(3, 1, 800),
                new DistanciaResponse(3, 2, 800)
        );

        new CalculoDistanciaAPI().registrarDistancias(new AlgoritmoGeneticoResponse(distancias, null));

        assertEquals(6, MatrizDistancias.getQuantidadeDistancias());
    }
}