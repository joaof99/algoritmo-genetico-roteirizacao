package com.genetico.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Populacao {
    private final List<Cromossomo> cromossomos;
    private final int tamanhoPopulacao;
    private final int chanceFixaOcorrenciaCrossover;
    private final int chanceFixaOcorrenciaMutacao;
    private final Random randomizador;

    public Populacao(int tamanhoPopulacao, int chanceFixaOcorrenciaCrossover, int chanceFixaOcorrenciaMutacao) {
        this.tamanhoPopulacao = tamanhoPopulacao;
        this.cromossomos = avaliarPopulacao(iniciarCromossomosPopulacaoAleatoriamente());
        this.randomizador = new Random();
        this.chanceFixaOcorrenciaCrossover = chanceFixaOcorrenciaCrossover;
        this.chanceFixaOcorrenciaMutacao = chanceFixaOcorrenciaMutacao;
    }

    public Populacao(List<Cromossomo> cromossomos, int chanceFixaOcorrenciaCrossover, int chanceFixaOcorrenciaMutacao) {
        this.tamanhoPopulacao = cromossomos.size();
        this.cromossomos = avaliarPopulacao(cromossomos);
        this.randomizador = new Random();
        this.chanceFixaOcorrenciaCrossover = chanceFixaOcorrenciaCrossover;
        this.chanceFixaOcorrenciaMutacao = chanceFixaOcorrenciaMutacao;
    }

    private List<Cromossomo> iniciarCromossomosPopulacaoAleatoriamente() {
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
        var cromossomosFilhos = new ArrayList<Cromossomo>();

        do {
            var pai1 = selecionarCromossomoPaiPorRoleta();
            var pai2 = selecionarCromossomoPaiPorRoleta();

            var filhos = realizarCrossover(pai1, pai2);

            if (filhos.size() != 2) {
                throw new IllegalStateException("Após um crossover deve ter exatamente 2 filhos. Encontrado: " + filhos.size());
            }

            var filho1 = filhos.getFirst();
            var filho2 = filhos.getLast();

            realizarMutacao(filho1, filho2);

            cromossomosFilhos.add(filho1);
            cromossomosFilhos.add(filho2);

        } while (cromossomosFilhos.size() < getTamanhoPopulacao());

        var todosOsCromossomos = new ArrayList<>(this.getCromossomos());
        todosOsCromossomos.addAll(cromossomosFilhos);

        var populacaoPaiComFilhos = new Populacao(todosOsCromossomos, getChanceFixaOcorrenciaCrossover(), getChanceFixaOcorrenciaMutacao());

        var melhoresCromosomosNovaPopulacao = new ArrayList<>(populacaoPaiComFilhos.getCromossomos().subList(0, getTamanhoPopulacao()));

        return new Populacao(melhoresCromosomosNovaPopulacao, getChanceFixaOcorrenciaCrossover(), getChanceFixaOcorrenciaMutacao());
    }

    private List<Cromossomo> realizarCrossover(Cromossomo pai1, Cromossomo pai2) {
        Cromossomo filho1;
        Cromossomo filho2;

        var chanceAleatoriaDeOcorrerCrossover = getRandomizador().nextInt(100) + 1;

        if (chanceAleatoriaDeOcorrerCrossover <= getChanceFixaOcorrenciaCrossover()) {
            var filhosCrossover = pai1.realizarCrossoverPmx(pai2);
            filho1 = filhosCrossover.getFirst();
            filho2 = filhosCrossover.getLast();

            filho1.atualizarFitness();
            filho2.atualizarFitness();

            return List.of(filho1, filho2);
        } else {
            return List.of(pai1, pai2);
        }
    }

    private void realizarMutacao(Cromossomo filho1, Cromossomo filho2) {
        var chanceAleatoriaDeOcorrerMutacao = getRandomizador().nextInt(100) + 1;

        if (chanceAleatoriaDeOcorrerMutacao <= getChanceFixaOcorrenciaMutacao()) {
            filho1.realizarMutacaoSwap();
            filho2.realizarMutacaoSwap();

            filho1.atualizarFitness();
            filho2.atualizarFitness();
        }
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

    public int getChanceFixaOcorrenciaCrossover() {
        return this.chanceFixaOcorrenciaCrossover;
    }

    public int getChanceFixaOcorrenciaMutacao() {
        return this.chanceFixaOcorrenciaMutacao;
    }
}
