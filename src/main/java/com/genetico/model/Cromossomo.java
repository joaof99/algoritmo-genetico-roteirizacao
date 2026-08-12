package com.genetico.model;

import com.genetico.CalculadorDistancias;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.StringJoiner;

public class Cromossomo {
    public static final int QTDE_MAXIMA_GENES = CalculadorDistancias.getQuantidadeEnderecos();
    private static final int POSICAO_CORTE_INICIO = 0;
    private static final int POSICAO_CORTE_FIM = 1;
    private final int[] genes;
    private Random randomizador;
    private final double fitness;

    public Cromossomo() {
        this.randomizador = new Random();
        this.genes = inicializarGenes();
        this.fitness = calcularFitness();
    }

    public Cromossomo(int[] genes) {
        if (genes.length > QTDE_MAXIMA_GENES) {
            throw new IllegalArgumentException(String.format("A quantidade de genes não deve ultrapassar a capacidade máxima de: %s", QTDE_MAXIMA_GENES));
        }

        this.genes = genes;
        this.fitness = calcularFitness();
        this.randomizador = new Random();
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

            fitness += CalculadorDistancias.obterDistanciaEntreDuasCidades(indiceCidadeOrigem, indiceCidadeDestino);
        }

        return fitness;
    }

    public void atualizarFitness() {
        calcularFitness();
    }

    Cromossomo[] realizarCrossoverPmx(Cromossomo pai2) {
        var tamanhoGenes = pai2.getGenes().length;

        var pontosDeCorte = gerarPontosDeCorte(tamanhoGenes);
        var pontoCorteInicio = pontosDeCorte[0];
        var pontoCorteFim = pontosDeCorte[1];

        var genesPai1 = getGenes();
        var genesPai2 = pai2.getGenes();

        var genesFilho1 = new int[tamanhoGenes];
        var genesFilho2 = new int[tamanhoGenes];

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

        for (int indice = 0; indice < tamanhoGenes; indice++) {
            if (indiceEstaNaRegiaoDeCorte(indice, pontosDeCorte)) continue;

            var gene = genesPai1[indice];

            while (contemGeneRepetido(genesFilho1, gene)) {
                gene = mapeamentoPai2ParaPai1.get(gene);
            }

            genesFilho1[indice] = gene;
        }

        for (int indice = 0; indice < tamanhoGenes; indice++) {
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
            pontoCorte1 = getRandomizador().nextInt(tamanhoCromossomo);
            pontoCorte2 = getRandomizador().nextInt(tamanhoCromossomo);
        } while (pontosDeCorteSaoInvalidos(pontoCorte1, pontoCorte2));

        return new int[]{pontoCorte1, pontoCorte2};
    }

    private boolean indiceEstaNaRegiaoDeCorte(int indice, int[] pontosDeCorte) {
        return (indice > pontosDeCorte[POSICAO_CORTE_INICIO] && indice <= pontosDeCorte[POSICAO_CORTE_FIM]);
    }

    private boolean contemGeneRepetido(int[] genes, int geneASerSubstituido) {
        for (int gene : genes) {
            if (gene == geneASerSubstituido) {
                return true;
            }
        }

        return false;
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
        return this.genes;
    }

    public double getFitness() {
        return fitness;
    }

    public Random getRandomizador() {
        return this.randomizador;
    }
}
