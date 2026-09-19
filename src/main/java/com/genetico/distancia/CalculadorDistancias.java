package com.genetico.distancia;

import com.genetico.model.AlgoritmoGeneticoRequest;
import com.genetico.model.Endereco;
import com.genetico.model.MatrizDistancias;

import java.util.List;
import java.util.OptionalDouble;

public class CalculadorDistancias {
    private CalculadorDistancias() {

    }

    public static void inicializar(MetodoCalculoDistancia metodoCalculoDistancia, AlgoritmoGeneticoRequest algoritmoGeneticoResponse) {
        metodoCalculoDistancia.registrarDistancias(algoritmoGeneticoResponse);
    }

    public static OptionalDouble obterDistanciaEntreEnderecos(int indiceEnderecoOrigem, int indiceEnderecoDestino) {
        return MatrizDistancias.getDistancia(indiceEnderecoOrigem, indiceEnderecoDestino);
    }
}