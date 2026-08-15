package com.genetico;

import com.genetico.model.Populacao;
import com.genetico.service.GraficoService;
import com.genetico.service.GraficoServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AlgoritmoGenetico {
    private static final Logger log = LogManager.getLogger();
    private final int qtdeGeracoes;
    private int tamanhoPopulacao;
    private int chanceOcorrenciaCrossover;
    private int chanceOcorrenciaMutacao;
    private final GraficoService graficoService = GraficoServiceFactory.getGraficoService();
    private Populacao populacao;

    public AlgoritmoGenetico(int tamanhoPopulacao, int qtdeGeracoes,int chanceOcorrenciaCrossover, int chanceOcorrenciaMutacao) {
        this.tamanhoPopulacao = tamanhoPopulacao;
        this.qtdeGeracoes = qtdeGeracoes;
        this.chanceOcorrenciaCrossover = chanceOcorrenciaCrossover;
        this.chanceOcorrenciaMutacao = chanceOcorrenciaMutacao;
    }

    public Populacao reproduzir() {
        populacao = new Populacao(tamanhoPopulacao, chanceOcorrenciaCrossover, chanceOcorrenciaMutacao);
        log.info("População inicial");
        populacao.imprimirPopulacao();

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