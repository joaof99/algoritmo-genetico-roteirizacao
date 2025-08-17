package com.genetico;

import com.genetico.model.Populacao;

public class Reproducao {

    public static final int QTDE_GERACOES = 20;

    public static void main(String[] args) {
        reproduzir();
    }

    private static void reproduzir() {
        var populacaoInicial = new Populacao();

        System.out.println("População inicial");
        populacaoInicial.imprimirPopulacao();

        var novaPopulacao = populacaoInicial.gerarPopulacaoFilha();

        System.out.println("População filha "+novaPopulacao.getCromossomos().size());
        novaPopulacao.imprimirPopulacao();

    }
}