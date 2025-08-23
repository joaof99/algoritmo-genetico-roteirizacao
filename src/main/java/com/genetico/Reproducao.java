package com.genetico;

import com.genetico.model.Populacao;

public class Reproducao {

    public static final int QTDE_GERACOES = 20;

    public static void main(String[] args) {
        reproduzir();
    }

    private static void reproduzir() {
        var populacao = new Populacao(30);
        var contadorGeracoes = 0;

        System.out.println("População inicial");
        populacao.imprimirPopulacao();

        while (contadorGeracoes < QTDE_GERACOES) {
            var novaPopulacao = populacao.gerarPopulacaoFilha();
            populacao = new Populacao(novaPopulacao.getCromossomos());

            System.out.println("Geração " + (contadorGeracoes + 1));
            populacao.imprimirPopulacao();
            contadorGeracoes++;
        }

        System.out.println("População final");
        populacao.imprimirPopulacao();
    }
}