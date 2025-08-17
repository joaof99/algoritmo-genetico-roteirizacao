package com.genetico.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Populacao {
    private List<Cromossomo> cromossomos;
    public static final int TAMANHO_POPULACAO = 30;
    public final Random randomizador;

    public Populacao() {
        this.cromossomos = avaliarPopulacao(iniciarPopulacaoAleatoriamente());
        this.randomizador = new Random();
    }

    public Populacao(List<Cromossomo> cromossomos) {
        this.cromossomos = avaliarPopulacao(cromossomos);
        this.randomizador = new Random();
    }

    private List<Cromossomo> iniciarPopulacaoAleatoriamente() {
        var cromossomos = new ArrayList<Cromossomo>();

        for (int indice = 0; indice < TAMANHO_POPULACAO; indice++) {
            var cromossomo = new Cromossomo();

            cromossomos.add(cromossomo);
        }

        return cromossomos;
    }

    private List<Cromossomo> avaliarPopulacao(List<Cromossomo> cromossomos) {
        var cromossomoOrdenado = new ArrayList<>(cromossomos);
        cromossomoOrdenado.sort(Comparator.comparingInt(Cromossomo::getFitness));

        return cromossomoOrdenado;
    }

    public void imprimirPopulacao() {
        for (var cromossomo : cromossomos) System.out.println(cromossomo.formatarGenes());
    }

    public Populacao gerarPopulacaoFilha() {
        var filhos = new ArrayList<Cromossomo>();

        do {
            var pai1 = selecionarCromossomoPaiPorRoleta();
            var pai2 = selecionarCromossomoPaiPorRoleta();

            var filhosCrossover = pai1.realizarCrossoverPmx(pai2, new Random());

            var filho1Crossover = filhosCrossover.getFirst();
            var filho2Crossover = filhosCrossover.getLast();

            filho1Crossover.atualizarFitness();
            filho2Crossover.atualizarFitness();

            filhos.add(filho1Crossover);
            filhos.add(filho2Crossover);

        } while (filhos.size() < Populacao.TAMANHO_POPULACAO);

        var todosOsCromossomos = new ArrayList<Cromossomo>();
        todosOsCromossomos.addAll(getCromossomos());
        todosOsCromossomos.addAll(filhos);

        var populacaoPaiComFilhos = new Populacao(todosOsCromossomos);

        var melhoresCromosomosNovaPopulacao = populacaoPaiComFilhos
                .getCromossomos()
                .subList(0, populacaoPaiComFilhos.getCromossomos().size() / 2);

        return new Populacao(melhoresCromosomosNovaPopulacao);
    }

    public Cromossomo selecionarCromossomoPaiPorRoleta() {
        var indicePaiEscolhido = 0;
        var fitnessTotalPopulacao = calcularSomaFitnessTotalDaPopulacao();

        var somaTotalFitnessIteracoes = 0;

        var numeroAleatorioMaximoFitness = gerarNumeroAleatorioMaximoFitness(fitnessTotalPopulacao);

        for (int indice = 0; indice < TAMANHO_POPULACAO; indice++) {
            somaTotalFitnessIteracoes += this.cromossomos.get(indice).getFitness();

            if (somaTotalFitnessIteracoes >= numeroAleatorioMaximoFitness) {
                indicePaiEscolhido = indice;
                break;
            }
        }

        return this.cromossomos.get(indicePaiEscolhido);
    }

    public int gerarNumeroAleatorioMaximoFitness(int fitnessTotalPopulacao) {
        return randomizador.nextInt(fitnessTotalPopulacao);
    }

    private int calcularSomaFitnessTotalDaPopulacao() {
        var fitnessTotalCromosssomos = 0;

        for (var cromossomo : this.cromossomos) {
            fitnessTotalCromosssomos += cromossomo.getFitness();
        }

        return fitnessTotalCromosssomos;
    }

    public List<Cromossomo> getCromossomos() {
        return this.cromossomos;
    }
}
