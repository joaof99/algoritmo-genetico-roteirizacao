package com.genetico;

import com.genetico.distancia.CalculadorDistancias;
import com.genetico.distancia.Haversine;
import com.genetico.exception.RotaClientException;
import com.genetico.model.Endereco;
import com.genetico.client.RotaClient;
import com.genetico.factory.RotaClientFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CalculadorDistanciasTest {
    private MockedStatic<RotaClientFactory> rotaClientFactory;

    @BeforeEach
    void setUp() {
        rotaClientFactory = Mockito.mockStatic(RotaClientFactory.class);
    }

    @AfterEach
    void tearDown() {
        rotaClientFactory.close();
    }

    @Test
    @DisplayName("Matriz de distâncias deve ser criada corretamente")
    public void matrizDeDistanciasDeveSerCriadaCorretamente() throws RotaClientException {
        var hospitalSP = new Endereco(1, -5.3, -8.2, "Hospital SP");
        var barbearia = new Endereco(3, -90.3, -50.9, "Barbearia");
        var shoppingSP  = new Endereco(2, -90.3, -50.9, "Shopping SP");
        var padaria = new Endereco(4, -90.3, -50.9, "Padaria");
        var armazem = new Endereco(5, -90.3, -50.9, "Armazém");
        var hospitalRJ = new Endereco(5, -90.3, -50.9, "Armazém");
        var pizzaria = new Endereco(5, -90.3, -50.9, "Pizzaria");
        var bar = new Endereco(5, -90.3, -50.9, "Bar");
        var estadio = new Endereco(5, -90.3, -50.9, "Estádio");
        var shoppingSC = new Endereco(5, -90.3, -50.9, "Shopping SC");

        var enderecos = List.of(hospitalSP, barbearia, shoppingSP, padaria, armazem, hospitalRJ, pizzaria, bar, estadio, shoppingSC);

        var rotaClient = mock(RotaClient.class);
        rotaClientFactory.when(RotaClientFactory::getRotaClient)
                .thenReturn(rotaClient);

        when(rotaClient.buscarEnderecosRota(anyInt())).thenReturn(enderecos);

        CalculadorDistancias.inicializar(new Haversine(), enderecos);

        assertEquals(21, CalculadorDistancias.getMatrizDistancias().getQuantidadeDistancias());
    }
}