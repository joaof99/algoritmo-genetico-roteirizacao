package com.genetico.distancia;

import com.genetico.exception.RotaClientException;
import com.genetico.model.Endereco;
import com.genetico.model.MatrizDistancias;

import java.util.List;

public class CalculadorDistancias {
    private static List<Endereco> enderecos;

    private CalculadorDistancias() {

    }

    public static void inicializar(MetodoCalculoDistancia metodoCalculoDistancia, List<Endereco> enderecosRota) throws RotaClientException {
        enderecos = enderecosRota;
        metodoCalculoDistancia.registrarDistancias(enderecos);
    }

    public static double obterDistanciaEntreEnderecos(int indiceEnderecoOrigem, int indiceEnderecoDestino) {
        return MatrizDistancias.getDistancia(indiceEnderecoOrigem, indiceEnderecoDestino);
    }

    public static int getEnderecoIdRealBanco(int indice) {
        return enderecos.get(indice).id();
    }
}