package com.genetico;

import com.genetico.model.Cromossomo;
import com.genetico.model.Populacao;

import java.util.ArrayList;

public class Reproducao {

    public static final int QTDE_GERACOES = 20;

    public static void main(String[] args) {
        reproduzir();
    }

    private static void reproduzir(){
        var populacao = new Populacao();
        var contadorGeracoes = 0;
        var contadorPopulacao = 0;

        var segundaPopulacao = new ArrayList<Cromossomo>();

        do {
            System.out.println("Geração: " + (contadorGeracoes + 1));

            do {
                var pai1 = populacao.selecionarCromossomoPaiPorRoleta();
                var pai2 = populacao.selecionarCromossomoPaiPorRoleta();

                segundaPopulacao.add(pai1);
                segundaPopulacao.add(pai2);

                contadorPopulacao++;
            } while (contadorPopulacao < Populacao.TAMANHO_POPULACAO / 2);

            contadorPopulacao = 0;

            System.out.println("Imprimindo população de filhos, de tamanho: " + segundaPopulacao.size());
            segundaPopulacao
                    .forEach(cromossomo -> {
                        System.out.println(cromossomo.formatarGenes());
                    });

            segundaPopulacao.clear();

            contadorGeracoes++;
        } while (contadorGeracoes < QTDE_GERACOES);
    }
}