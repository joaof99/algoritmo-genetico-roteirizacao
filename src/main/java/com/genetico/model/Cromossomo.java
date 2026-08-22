package com.genetico.model;

import com.genetico.crossover.CrossoverPMX;
import com.genetico.crossover.MetodoCrossover;
import com.genetico.distancia.CalculadorDistancias;
import com.genetico.factory.RandomizadorFactory;

import java.util.Random;
import java.util.StringJoiner;

public class Cromossomo {
    public static final int QTDE_MAXIMA_GENES = CalculadorDistancias.getQuantidadeEnderecos();
    private static final int POSICAO_CORTE_INICIO = 0;
    private static final int POSICAO_CORTE_FIM = 1;
    private final int[] genes;
    private final Random randomizador = RandomizadorFactory.getRandomizador();
    private final double fitness;
    private final MetodoCrossover metodoCrossover = new CrossoverPMX();

    public Cromossomo() {
        genes = inicializarGenes();
        fitness = calcularFitness();
    }

    public Cromossomo(int[] genes) {
        if (genes.length > QTDE_MAXIMA_GENES) {
            throw new IllegalArgumentException(String.format("A quantidade de genes não deve ultrapassar a capacidade máxima de: %s", QTDE_MAXIMA_GENES));
        }

        this.genes = genes;
        fitness = calcularFitness();
    }

    private int[] inicializarGenes() {
        var genes = new int[QTDE_MAXIMA_GENES];

        for (int i = 0; i < genes.length; i++) {
            genes[i] = CalculadorDistancias.getEnderecoId(i);
        }

        embaralharGenes(genes);

        return genes;
    }

    private void embaralharGenes(int[] genes) {
        for (int i = genes.length - 1; i > 0; i--) {
            var indiceAleatorio = randomizador.nextInt(i + 1);

            var temp = genes[i];
            genes[i] = genes[indiceAleatorio];
            genes[indiceAleatorio] = temp;
        }
    }

    private double calcularFitness() {
        var fitness = 0.0;

        for (int indice = 0; indice < this.genes.length - 1; indice++) {
            var indiceCidadeOrigem = this.genes[indice];
            var indiceCidadeDestino = this.genes[indice + 1];

            fitness += CalculadorDistancias.obterDistanciaEntreEnderecos(indiceCidadeOrigem, indiceCidadeDestino);
        }

        return fitness;
    }

    public void atualizarFitness() {
        calcularFitness();
    }

    public Cromossomo[] realizarCrossover(Cromossomo pai2) {
        return metodoCrossover.realizarCrossover(this, pai2);
    }

    void realizarMutacaoSwap() {
        int indiceAleatorioGene1, indiceAleatorioGene2;

        do {
            indiceAleatorioGene1 = getRandomizador().nextInt(1, getGenes().length);
            indiceAleatorioGene2 = getRandomizador().nextInt(1, getGenes().length);
        } while (indiceAleatorioGene1 == indiceAleatorioGene2);

        var genesAtuais = getGenes();
        var valorGeneAnteriorIndice1 = genesAtuais[indiceAleatorioGene1];

        genesAtuais[indiceAleatorioGene1] = genesAtuais[indiceAleatorioGene2];
        genesAtuais[indiceAleatorioGene2] = valorGeneAnteriorIndice1;
    }

    private boolean pontosDeCorteSaoInvalidos(int pontoCorte1, int pontoCorte2) {
        return pontoCorte1 == pontoCorte2 || pontoCorte1 > pontoCorte2;
    }

    public String formatarGenes() {
        var stringJoiner = new StringJoiner(" | ");

        for (int gene : getGenes()) {
            stringJoiner.add(String.valueOf(gene));
        }

        stringJoiner.add(String.valueOf(getFitness()));

        return stringJoiner.toString();
    }

    public int[] getGenes() {
        return genes;
    }

    public double getFitness() {
        return fitness;
    }

    public Random getRandomizador() {
        return randomizador;
    }
}
