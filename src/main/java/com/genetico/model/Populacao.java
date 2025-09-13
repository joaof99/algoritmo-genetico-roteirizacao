package com.genetico.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Populacao {
    private final List<Cromossomo> cromossomos;
    private final int tamanhoPopulacao;
    private final Random randomizador;
    private static final int PROBABILIDADE_DE_OCORRER_OCORRENCIA_CROSSOVER = 50;
    private static final int PROBABILIDADE_FIXA_DE_OCORRER_MUTACAO = 50;

    public Populacao(int tamanhoPopulacao) {
        this.tamanhoPopulacao = tamanhoPopulacao;
        this.cromossomos = avaliarPopulacao(iniciarPopulacaoAleatoriamente());
        this.randomizador = new Random();
    }

    public Populacao(List<Cromossomo> cromossomos) {
        this.tamanhoPopulacao = cromossomos.size();
        this.cromossomos = avaliarPopulacao(cromossomos);
        this.randomizador = new Random();
    }

    private List<Cromossomo> iniciarPopulacaoAleatoriamente() {
        var cromossomos = new ArrayList<Cromossomo>();

        for (int indice = 0; indice < getTamanhoPopulacao(); indice++) {
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
        var cromossomosFilhos = gerarCromossomosFilhos();

        var todosOsCromossomos = new ArrayList<Cromossomo>();
        todosOsCromossomos.addAll(this.getCromossomos());
        todosOsCromossomos.addAll(cromossomosFilhos);

        var populacaoPaiComFilhos = new Populacao(todosOsCromossomos);

        var melhoresCromosomosNovaPopulacao = new ArrayList<>(
                populacaoPaiComFilhos
                        .getCromossomos()
                        .subList(0, getTamanhoPopulacao())
        );

        return new Populacao(melhoresCromosomosNovaPopulacao);
    }

    private List<Cromossomo> gerarCromossomosFilhos() {
        var filhos = new ArrayList<Cromossomo>();
        var randomizador = new Random();

        do {
            var chanceAleatoriaDeOcorrerCrossover = getRandomizador().nextInt(100) + 1;
            var pai1 = selecionarCromossomoPaiPorRoleta();
            var pai2 = selecionarCromossomoPaiPorRoleta();
            Cromossomo filho1;
            Cromossomo filho2;

            if (chanceAleatoriaDeOcorrerCrossover <= PROBABILIDADE_DE_OCORRER_OCORRENCIA_CROSSOVER) {
                var filhosCrossover = pai1.realizarCrossoverPmx(pai2, randomizador);
                filho1 = filhosCrossover.getFirst();
                filho2 = filhosCrossover.getLast();

                filho1.atualizarFitness();
                filho2.atualizarFitness();
            } else {
                filho1 = pai1;
                filho2 = pai2;
            }

            var chanceAleatoriaDeOcorrerMutacao = getRandomizador().nextInt(100) + 1;

            if (chanceAleatoriaDeOcorrerMutacao <= PROBABILIDADE_FIXA_DE_OCORRER_MUTACAO) {
                filho1.realizarMutacao();
                filho2.realizarMutacao();

                filho1.atualizarFitness();
                filho2.atualizarFitness();
            }

            filhos.add(filho1);
            filhos.add(filho2);

        } while (filhos.size() < getTamanhoPopulacao());

        return filhos;
    }

    public Cromossomo selecionarCromossomoPaiPorRoleta() {
        var indicePaiEscolhido = 0;
        var fitnessTotalPopulacao = calcularSomaFitnessTotalDaPopulacao();

        var somaTotalFitnessIteracoes = 0;

        var numeroAleatorioMaximoFitness = gerarNumeroAleatorioMaximoFitness(fitnessTotalPopulacao);

        for (int indice = 0; indice < getTamanhoPopulacao(); indice++) {
            somaTotalFitnessIteracoes += this.cromossomos.get(indice).getFitness();

            if (somaTotalFitnessIteracoes >= numeroAleatorioMaximoFitness) {
                indicePaiEscolhido = indice;
                break;
            }
        }

        return this.cromossomos.get(indicePaiEscolhido);
    }

    private int gerarNumeroAleatorioMaximoFitness(int fitnessTotalPopulacao) {
        return getRandomizador().nextInt(fitnessTotalPopulacao);
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

    public int getTamanhoPopulacao() {
        return this.tamanhoPopulacao;
    }

    public Random getRandomizador() {
        return this.randomizador;
    }
}
