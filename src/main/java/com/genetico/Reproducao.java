package com.genetico;

import com.genetico.model.Populacao;
import com.genetico.service.GraficoService;

public class Reproducao {
    private final int qtdeGeracoes;
    private Populacao populacao;

    public Reproducao(int qtdeGeracoes, Populacao populacao) {
        this.qtdeGeracoes = qtdeGeracoes;
        this.populacao = populacao;
    }

    public Populacao reproduzir() {
        var indicesGeracoes = new double[qtdeGeracoes];
        var valoresFitness = new double[qtdeGeracoes];

        var contadorGeracoes = 0;

        while (contadorGeracoes < qtdeGeracoes) {
            indicesGeracoes[contadorGeracoes] = contadorGeracoes;
            var melhorFitnessPopulacaoAtual = populacao.getCromossomos()[0].getFitness();
            valoresFitness[contadorGeracoes] = melhorFitnessPopulacaoAtual;

            var novaPopulacao = populacao.gerarPopulacaoFilha();
            populacao = new Populacao(novaPopulacao.getCromossomos(),
                    populacao.getChanceFixaOcorrenciaCrossover(),
                    populacao.getChanceFixaOcorrenciaMutacao());

            contadorGeracoes++;
        }

        new GraficoService().gerarGraficoEvolucaoFitness(indicesGeracoes, valoresFitness);

        return populacao;
    }
}