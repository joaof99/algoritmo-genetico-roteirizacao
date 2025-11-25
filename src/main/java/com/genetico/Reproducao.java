package com.genetico;

import com.genetico.model.Populacao;
import com.genetico.service.GraficoService;

public class Reproducao {
    private final int qtdeGeracoes;
    private Populacao populacao;
    private final GraficoService graficoService;

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
        var melhorFitnessPopulacaoAtual = populacao.getCromossomos()[0].getFitness();
        melhoresFitnessPopulacoes[indiceGeracao] = melhorFitnessPopulacaoAtual;
    }
}