package com.genetico.crossover;

import com.genetico.factory.RandomizadorFactory;
import com.genetico.model.Cromossomo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class CrossoverPMX implements MetodoCrossover {
    private final Random randomizador = RandomizadorFactory.getRandomizador();

    @Override
    public Cromossomo[] realizarCrossover(Cromossomo pai1, Cromossomo pai2) {
        var quantidadeGenesPai1 = pai1.getGenes().length;
        var quantidadeGenesPai2 = pai2.getGenes().length;

        if (quantidadeGenesPai1 != quantidadeGenesPai2) {
            throw new IllegalArgumentException(String.format("Pai 1 e pai 2 devem ter exatamente a mesma quantidade de genes." +
                    " Encontrado: %d e %d respectivamente", quantidadeGenesPai1, quantidadeGenesPai2));
        }

        var pontosDeCorte = gerarPontosDeCorte(quantidadeGenesPai1);
        var pontoCorteInicio = pontosDeCorte[0];
        var pontoCorteFim = pontosDeCorte[1];

        var genesPai1 = pai1.getGenes();
        var genesPai2 = pai2.getGenes();

        var genesFilho1 = new int[quantidadeGenesPai1];
        var genesFilho2 = new int[quantidadeGenesPai1];

        Arrays.fill(genesFilho1, -1);
        Arrays.fill(genesFilho2, -1);

        for (int indice = pontoCorteInicio + 1; indice <= pontoCorteFim; indice++) {
            genesFilho1[indice] = genesPai2[indice];
            genesFilho2[indice] = genesPai1[indice];
        }

        var mapeamentoPai2ParaPai1 = new HashMap<Integer, Integer>();
        var mapeamentoPai1ParaPai2 = new HashMap<Integer, Integer>();

        for (int i = pontoCorteInicio + 1; i <= pontoCorteFim; i++) {
            mapeamentoPai2ParaPai1.put(genesPai2[i], genesPai1[i]);
            mapeamentoPai1ParaPai2.put(genesPai1[i], genesPai2[i]);
        }

        for (int indice = 0; indice < quantidadeGenesPai1; indice++) {
            if (indiceEstaNaRegiaoDeCorte(indice, pontosDeCorte)) continue;

            var gene = genesPai1[indice];

            while (contemGeneRepetido(genesFilho1, gene)) {
                gene = mapeamentoPai2ParaPai1.get(gene);
            }

            genesFilho1[indice] = gene;
        }

        for (int indice = 0; indice < quantidadeGenesPai1; indice++) {
            if (indiceEstaNaRegiaoDeCorte(indice, pontosDeCorte)) continue;

            var gene = genesPai2[indice];

            while (contemGeneRepetido(genesFilho2, gene)) {
                gene = mapeamentoPai1ParaPai2.get(gene);
            }

            genesFilho2[indice] = gene;
        }

        return new Cromossomo[]{new Cromossomo(genesFilho1), new Cromossomo(genesFilho2)};
    }

    private int[] gerarPontosDeCorte(int tamanhoCromossomo) {
        int pontoCorte1, pontoCorte2;
        do {
            pontoCorte1 = randomizador.nextInt(tamanhoCromossomo);
            pontoCorte2 = randomizador.nextInt(tamanhoCromossomo);
        } while (pontosDeCorteSaoInvalidos(pontoCorte1, pontoCorte2));

        return new int[]{pontoCorte1, pontoCorte2};
    }

    private boolean pontosDeCorteSaoInvalidos(int pontoCorte1, int pontoCorte2) {
        return pontoCorte1 == pontoCorte2 || pontoCorte1 > pontoCorte2;
    }

    private boolean contemGeneRepetido(int[] genes, int geneASerSubstituido) {
        for (int gene : genes) {
            if (gene == geneASerSubstituido) {
                return true;
            }
        }

        return false;
    }

    private boolean indiceEstaNaRegiaoDeCorte(int indice, int[] pontosDeCorte) {
        int POSICAO_CORTE_INICIO = 0;
        int POSICAO_CORTE_FIM = 1;

        return (indice > pontosDeCorte[POSICAO_CORTE_INICIO] && indice <= pontosDeCorte[POSICAO_CORTE_FIM]);
    }
}
