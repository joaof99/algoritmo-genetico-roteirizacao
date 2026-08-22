package com.genetico.model;

import com.genetico.crossover.CrossoverPMX;
import com.genetico.crossover.MetodoCrossover;
import com.genetico.distancia.CalculadorDistancias;
import com.genetico.factory.RandomizadorFactory;
import com.genetico.mutacao.MetodoMutacao;
import com.genetico.mutacao.Swap;

import java.util.Random;
import java.util.StringJoiner;

public class Cromossomo {
    private final int quantidadeMaximaGenes;
    private final int[] genes;
    private final Random randomizador = RandomizadorFactory.getRandomizador();
    private final double fitness;
    private final MetodoCrossover metodoCrossover = new CrossoverPMX();
    private final MetodoMutacao metodoMutacao = new Swap();

    public Cromossomo(int quantidadeMaximaGenes) {
        this.quantidadeMaximaGenes = quantidadeMaximaGenes;
        genes = inicializarGenes();
        fitness = calcularFitness();
    }

    public Cromossomo(int[] genes) {
        quantidadeMaximaGenes = genes.length;
        this.genes = genes;
        fitness = calcularFitness();
    }

    private int[] inicializarGenes() {
        var genes = new int[quantidadeMaximaGenes];

        for (int i = 0; i < genes.length; i++) {
            genes[i] = CalculadorDistancias.getEnderecoIdRealBanco(i);
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

    Cromossomo[] realizarCrossover(Cromossomo pai2) {
        return metodoCrossover.realizarCrossover(this, pai2);
    }

    void realizarMutacao() {
        metodoMutacao.realizarMutacao(this);
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
}
