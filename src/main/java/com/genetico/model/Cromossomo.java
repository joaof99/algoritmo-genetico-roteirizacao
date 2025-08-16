package com.genetico.model;

import com.genetico.CalculadorDistancias;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

public class Cromossomo {
    public static final int QTDE_MAXIMA_GENES = 10;
    public static final int VALOR_GENE_ORIGEM = 0;
    private static final int POSICAO_CORTE_INICIO = 0;
    private static final int POSICAO_CORTE_FIM = 1;
    private final int[] genes;
    private int fitness;

    public Cromossomo() {
        this.genes = inicializarGenes();
        this.fitness = calcularFitness();
    }

    public Cromossomo(int[] genes) {
        if (genes.length > QTDE_MAXIMA_GENES) {
            throw new IllegalArgumentException("A quantidade de genes não deve ultrapassar a capacidade máxima de: " + QTDE_MAXIMA_GENES);
        }

        this.genes = genes;
        this.fitness = calcularFitness();
    }

    private int[] inicializarGenes() {
        var genes = new int[QTDE_MAXIMA_GENES];
        genes[0] = VALOR_GENE_ORIGEM;

        for (int indice = 1; indice < genes.length; indice++) {
            genes[indice] = indice;
        }

        var random = new Random();

        for (int indice = 1; indice < genes.length; indice++) {
            int indiceAleatorio = random.nextInt(indice) + 1;

            int valorGeneAtual = genes[indice];

            genes[indice] = genes[indiceAleatorio];
            genes[indiceAleatorio] = valorGeneAtual;
        }

        return genes;
    }

    private int calcularFitness() {
        var fitness = 0;

        for (int i = 0; i < this.genes.length - 1; i++) {
            var cidadeOrigem = this.genes[i];
            var cidadeDestino = this.genes[i + 1];

            fitness += CalculadorDistancias.getDistancias()[cidadeOrigem][cidadeDestino];
        }

        return fitness;
    }

    public List<Cromossomo> realizarCrossoverPmx(Cromossomo pai2, Random random) {
        var pontosDeCorte = gerarPontosDeCorte(random);
        var genesPai1 = getGenes();
        var genesPai2 = pai2.getGenes();
        var tamanhoGenes = genesPai1.length;

        var genesFilho1 = new int[tamanhoGenes];
        var genesFilho2 = new int[tamanhoGenes];

        Arrays.fill(genesFilho1, -1);
        Arrays.fill(genesFilho2, -1);

        for (int indice = pontosDeCorte[0] + 1; indice <= pontosDeCorte[1]; indice++) {
            genesFilho1[indice] = genesPai2[indice];
            genesFilho2[indice] = genesPai1[indice];
        }

        var mapeamentoPai2ParaPai1 = new int[tamanhoGenes];
        var mapeamentoPai1ParaPai2 = new int[tamanhoGenes];

        Arrays.fill(mapeamentoPai2ParaPai1, -1);
        Arrays.fill(mapeamentoPai1ParaPai2, -1);

        for (int indice = pontosDeCorte[0] + 1; indice <= pontosDeCorte[1]; indice++) {
            mapeamentoPai2ParaPai1[genesPai2[indice]] = genesPai1[indice];
            mapeamentoPai1ParaPai2[genesPai1[indice]] = genesPai2[indice];
        }

        for (int indice = 0; indice < tamanhoGenes; indice++) {
            if (indiceEstaNaRegiaoDeCorte(indice, pontosDeCorte)) continue;

            var gene = genesPai1[indice];

            while (contemGeneRepetido(genesFilho1, gene)) {
                gene = mapeamentoPai2ParaPai1[gene];
            }

            genesFilho1[indice] = gene;
        }

        for (int indice = 0; indice < tamanhoGenes; indice++) {
            if (indiceEstaNaRegiaoDeCorte(indice, pontosDeCorte)) continue;

            var gene = genesPai2[indice];

            while (contemGeneRepetido(genesFilho2, gene)) {
                gene = mapeamentoPai1ParaPai2[gene];
            }

            genesFilho2[indice] = gene;
        }

        return List.of(new Cromossomo(genesFilho1), new Cromossomo(genesFilho2));
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

    private int[] gerarPontosDeCorte(Random random) {
        int pontoCorte1, pontoCorte2;

        do {
            pontoCorte1 = random.nextInt(QTDE_MAXIMA_GENES);
            pontoCorte2 = random.nextInt(QTDE_MAXIMA_GENES);
        } while (pontosDeCorteSaoInvalidos(pontoCorte1, pontoCorte2));

        return new int[]{pontoCorte1, pontoCorte2};
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

    public int getFitness() {
        return fitness;
    }
}
