package com.genetico;

import com.genetico.model.Populacao;
import com.genetico.service.GraficoService;

public class Reproducao {
    private final int qtdeGeracoes;
    private final GraficoService graficoService;
    private Populacao populacao;

    public Reproducao(int qtdeGeracoes, Populacao populacao, GraficoService graficoService) {
        this.qtdeGeracoes = qtdeGeracoes;
        this.populacao = populacao;
        this.graficoService = graficoService;
    }

    public Populacao reproduzir() {
        var indicesGeracoes = new double[qtdeGeracoes];
        var melhoresFitnessPopulacoes = new double[qtdeGeracoes];

        for (int indiceGeracao = 0; indiceGeracao < qtdeGeracoes; indiceGeracao++) {
            preencherValoresGraficoFitness(indicesGeracoes, indiceGeracao, melhoresFitnessPopulacoes);

            populacao = populacao.gerarPopulacaoFilha();
        }

        graficoService.gerarGraficoEvolucaoFitness(indicesGeracoes, melhoresFitnessPopulacoes);

        return populacao;
    }

    private void preencherValoresGraficoFitness(double[] indicesGeracoes, int indiceGeracao, double[] melhoresFitnessPopulacoes) {
        indicesGeracoes[indiceGeracao] = indiceGeracao;

        var cromossomoComMelhorFitness = populacao.getCromossomos()[0];

        var melhorFitnessPopulacaoAtual = cromossomoComMelhorFitness.getFitness();
        melhoresFitnessPopulacoes[indiceGeracao] = melhorFitnessPopulacaoAtual;
    }
}