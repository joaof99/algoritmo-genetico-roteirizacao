package com.genetico.distancia;

import com.genetico.exception.RotaClientException;
import com.genetico.model.Endereco;
import com.genetico.model.MatrizDistancias;
import com.genetico.factory.RotaClientFactory;

import java.util.List;

public class CalculadorDistancias {
    private static List<Endereco> enderecos;
    private static MatrizDistancias matrizDistancias;

    private CalculadorDistancias() {

    }

    public static void inicializar(MetodoCalculoDistancia metodoCalculoDistancia, int idRota) throws RotaClientException {
        enderecos = RotaClientFactory.getRotaClient().buscarEnderecosRota(idRota);
        matrizDistancias = metodoCalculoDistancia.inicializarDistancias(enderecos);
    }

    public static double obterDistanciaEntreEnderecos(int indiceEnderecoOrigem, int indiceEnderecoDestino) {
        return matrizDistancias.getDistancia(indiceEnderecoOrigem, indiceEnderecoDestino);
    }

    public static MatrizDistancias getMatrizDistancias() {
        return matrizDistancias;
    }

    public static int getEnderecoIdRealBanco(int indice) {
        return enderecos.get(indice).id();
    }

    public static int getQuantidadeEnderecos() {
        return enderecos.size();
    }
}