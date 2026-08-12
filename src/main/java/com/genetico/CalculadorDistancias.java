package com.genetico;

import com.genetico.model.Endereco;
import com.genetico.model.MatrizDistancias;
import com.genetico.service.RotaClient;

import java.util.List;

public class CalculadorDistancias {
    private static List<Endereco> enderecos;
    private static MatrizDistancias matrizDistancias;

    private  CalculadorDistancias(){

    }

    public static void inicializar(RotaClient rotaClient, MetodoCalculoDistancia metodoCalculoDistancia) {
        enderecos = rotaClient.buscarTodosEnderecos();
        matrizDistancias = metodoCalculoDistancia.inicializarDistancias(enderecos);
    }

    public static double obterDistanciaEntreEnderecos(int indiceEnderecoOrigem, int indiceEnderecoDestino) {
        return matrizDistancias.getDistancia(indiceEnderecoOrigem, indiceEnderecoDestino);
    }

    public static MatrizDistancias getMatrizDistancias() {
        return matrizDistancias;
    }

    public static int getEnderecoId(int indice) {
        return enderecos.get(indice).id();
    }

    public static int getQuantidadeEnderecos() {
        return enderecos.size();
    }
}