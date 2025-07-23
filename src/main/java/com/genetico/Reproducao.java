package com.genetico;

import com.genetico.model.Cromossomo;
import com.genetico.model.Populacao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Reproducao {

    public static final int QTDE_GERACOES = 20;

    public static void main(String[] args) {
        reproduzir();
    }

    private static void reproduzir() {
        var populacao = new Populacao();
        var contadorGeracoes = 0;
        var contadorPopulacao = 0;

        var cromossomosFilhos = new ArrayList<Cromossomo>();

        System.out.println("============= POPULAÇÃO INICIAL ===========================================");
        populacao.imprimirPopulacao();
        System.out.println("========================================= ===========================================");

        do {
            System.out.println("Geração: " + (contadorGeracoes + 1));

            do {
                var pai1 = populacao.selecionarCromossomoPaiPorRoleta();
                var pai2 = populacao.selecionarCromossomoPaiPorRoleta();

                List<Cromossomo> filhos = pai1.realizarCrossoverPmx(pai2, new Random());

                cromossomosFilhos.add(filhos.get(0));
                cromossomosFilhos.add(filhos.get(1));

                contadorPopulacao++;
            } while (contadorPopulacao < Populacao.TAMANHO_POPULACAO / 2);

            contadorPopulacao = 0;

            System.out.println("Imprimindo população de filhos, de tamanho: " + cromossomosFilhos.size());
            cromossomosFilhos.forEach(cromossomo -> System.out.println(cromossomo.formatarGenes()));

            var todosOsCromossomos = new ArrayList<>(populacao.getCromossomos());
            todosOsCromossomos.addAll(cromossomosFilhos);

            var melhoresCromossomosPopulacao = todosOsCromossomos
                    .stream()
                    .sorted(Comparator.comparing(Cromossomo::getFitness))
                    .limit(Populacao.TAMANHO_POPULACAO)
                    .collect(Collectors.toList());

            populacao = new Populacao(melhoresCromossomosPopulacao);
            System.out.println("População fusão quantidade: " + populacao.getCromossomos().size());

            cromossomosFilhos.clear();

            contadorGeracoes++;
        } while (contadorGeracoes < QTDE_GERACOES);

        System.out.println("População final: ");

        populacao.imprimirPopulacao();
    }
}