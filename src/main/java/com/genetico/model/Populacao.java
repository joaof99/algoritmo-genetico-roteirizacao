package com.genetico.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Populacao {
    private final Cromossomo[] cromossomos;
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

    public Populacao(Cromossomo[] cromossomos, int chanceFixaOcorrenciaCrossover, int chanceFixaOcorrenciaMutacao) {
        this.tamanhoPopulacao = cromossomos.length;
        this.cromossomos = avaliarPopulacao(cromossomos);
        this.randomizador = new Random();
        this.chanceFixaOcorrenciaCrossover = chanceFixaOcorrenciaCrossover;
        this.chanceFixaOcorrenciaMutacao = chanceFixaOcorrenciaMutacao;
    }

    private Cromossomo[] iniciarCromossomosPopulacaoAleatoriamente() {
        var cromossomos = new Cromossomo[getTamanhoPopulacao()];

        for (int indice = 0; indice < getTamanhoPopulacao(); indice++) {
            cromossomos[indice] = new Cromossomo();
        }

        return cromossomos;
    }

    private Cromossomo[] avaliarPopulacao(Cromossomo[] cromossomos) {
        var cromossomoOrdenado = Arrays.copyOf(cromossomos, cromossomos.length);
        Arrays.sort(cromossomoOrdenado, Comparator.comparingInt(Cromossomo::getFitness));

        return cromossomoOrdenado;
    }

    public void imprimirPopulacao() {
        for (var cromossomo : cromossomos) System.out.println(cromossomo.formatarGenes());
    }

    public Populacao gerarPopulacaoFilha() {
        var cromossomosFilhos = new Cromossomo[getTamanhoPopulacao()];
        var contadorIndice = 0;

        do {
            var pai1 = selecionarCromossomoPaiPorRoleta();
            var pai2 = selecionarCromossomoPaiPorRoleta();

            var filhos = realizarCrossover(pai1, pai2);

            if (filhos.length != 2) {
                throw new IllegalStateException("Após um crossover deve ter exatamente 2 filhos. Encontrado: " + filhos.length);
            }

            var filho1 = filhos[0];
            var filho2 = filhos[1];

            realizarMutacao(filho1, filho2);

            cromossomosFilhos[contadorIndice] = new Cromossomo(filho1.getGenes());
            contadorIndice++;

            cromossomosFilhos[contadorIndice] = new Cromossomo(filho2.getGenes());
            contadorIndice++;

        } while (contadorIndice < getTamanhoPopulacao());

        var todosOsCromossomos = unirCromossomosPaisEFilhos(cromossomosFilhos);

        var populacaoPaiComFilhos = new Populacao(todosOsCromossomos, getChanceFixaOcorrenciaCrossover(), getChanceFixaOcorrenciaMutacao());

        var melhoresCromosomosNovaPopulacao = Arrays.copyOfRange(populacaoPaiComFilhos.getCromossomos(), 0, getTamanhoPopulacao());

        return new Populacao(melhoresCromosomosNovaPopulacao, getChanceFixaOcorrenciaCrossover(), getChanceFixaOcorrenciaMutacao());
    }

    Cromossomo selecionarCromossomoPaiPorRoleta() {
        var indicePaiEscolhido = 0;
        var fitnessTotalPopulacao = calcularSomaFitnessTotalDaPopulacao();

        var somaTotalFitnessIteracoes = 0;

        var numeroAleatorioMaximoFitness = gerarNumeroAleatorioMaximoFitness(fitnessTotalPopulacao);

        for (int indice = 0; indice < getTamanhoPopulacao(); indice++) {
            somaTotalFitnessIteracoes += this.cromossomos[indice].getFitness();

            if (somaTotalFitnessIteracoes >= numeroAleatorioMaximoFitness) {
                indicePaiEscolhido = indice;
                break;
            }
        }

        return this.cromossomos[indicePaiEscolhido];
    }

    private int gerarNumeroAleatorioMaximoFitness(int fitnessTotalPopulacao) {
        return getRandomizador().nextInt(fitnessTotalPopulacao);
    }

    private Cromossomo[] realizarCrossover(Cromossomo pai1, Cromossomo pai2) {
        Cromossomo filho1;
        Cromossomo filho2;

        var chanceAleatoriaDeOcorrerCrossover = getRandomizador().nextInt(100) + 1;

        if (chanceAleatoriaDeOcorrerCrossover <= getChanceFixaOcorrenciaCrossover()) {
            var filhosCrossover = pai1.realizarCrossoverPmx(pai2);
            filho1 = filhosCrossover[0];
            filho2 = filhosCrossover[1];

            filho1.atualizarFitness();
            filho2.atualizarFitness();

            return new Cromossomo[]{new Cromossomo(filho1.getGenes()), new Cromossomo(filho2.getGenes())};
        } else {
            return new Cromossomo[]{pai1, pai2};
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

    private Cromossomo[] unirCromossomosPaisEFilhos(Cromossomo[] cromossomosFilhos) {
        var tamanhoTotal = getCromossomos().length + cromossomosFilhos.length;

        var todosOsCromossomos = new Cromossomo[tamanhoTotal];

        System.arraycopy(this.getCromossomos(), 0, todosOsCromossomos, 0, this.getCromossomos().length);
        System.arraycopy(cromossomosFilhos, 0, todosOsCromossomos, this.getCromossomos().length, cromossomosFilhos.length);

        return todosOsCromossomos;
    }

    private int calcularSomaFitnessTotalDaPopulacao() {
        var fitnessTotalCromosssomos = 0;

        for (var cromossomo : this.cromossomos) {
            fitnessTotalCromosssomos += cromossomo.getFitness();
        }

        return fitnessTotalCromosssomos;
    }

    public Cromossomo[] getCromossomos() {
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
