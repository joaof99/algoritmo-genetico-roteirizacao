package com.genetico.distancia;

import com.genetico.model.AlgoritmoGeneticoResponse;
import com.genetico.model.Endereco;
import com.genetico.model.MatrizDistancias;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HaversineTest {

    @BeforeEach
    public void beforeEach(){
        MatrizDistancias.getDistancias().clear();
    }

    @Test
    @DisplayName("Deve calcular a distância de Haversine corretamente")
    public void deveCalcularDistanciaHaversineCorretamente() {
        var haversine = new Haversine();
        var sp = new Endereco(1, -23.5505, -46.6333, "");
        var rj = new Endereco(2, -22.9068, -43.1729, "");
        assertEquals(360.74882490989955, haversine.calcularDistancia(sp, rj));
    }

    @Test
    @DisplayName("Deve ser calculado todas as distâncias possíveis")
    public void deveSerCalculadoTodasAsDistanciasPossiveis() {
        var enderecos = List.of(new Endereco(1, -5.3, -8.2, "Hospital SP"),
                new Endereco(3, -90.3, -50.9, "Barbearia"),
                new Endereco(2, -90.3, -50.9, "Shopping SP"),
                new Endereco(4, -90.3, -50.9, "Padaria"),
                new Endereco(5, -90.3, -50.9, "Armazém"),
                new Endereco(6, -90.3, -50.9, "Hospital RJ"),
                new Endereco(7, -90.3, -50.9, "Pizzaria"),
                new Endereco(8, -90.3, -50.9, "Bar"),
                new Endereco(9, -90.3, -50.9, "Estádio"),
                new Endereco(10, -90.3, -50.9, "Shopping SC"));

        new Haversine().registrarDistancias(new AlgoritmoGeneticoResponse(null, enderecos));

        assertEquals(90, MatrizDistancias.getQuantidadeDistancias());
    }
}