package com.genetico.distancia;

import com.genetico.exception.AlgoritmoGeneticoClientException;
import com.genetico.model.AlgoritmoGeneticoResponse;
import com.genetico.model.Endereco;
import com.genetico.model.MatrizDistancias;

import java.util.List;
import java.util.OptionalDouble;

public class CalculadorDistancias {
    private static List<Endereco> enderecos;

    private CalculadorDistancias() {

    }

    public static void inicializar(MetodoCalculoDistancia metodoCalculoDistancia, AlgoritmoGeneticoResponse algoritmoGeneticoResponse) throws AlgoritmoGeneticoClientException {
        enderecos = algoritmoGeneticoResponse.enderecos();
        metodoCalculoDistancia.registrarDistancias(algoritmoGeneticoResponse);
    }

    public static OptionalDouble obterDistanciaEntreEnderecos(int indiceEnderecoOrigem, int indiceEnderecoDestino) {
        return MatrizDistancias.getDistancia(indiceEnderecoOrigem, indiceEnderecoDestino);
    }

    public static int getEnderecoIdRealBanco(int indice) {
        return enderecos.get(indice).id();
    }
}