package com.genetico;

import com.genetico.exception.AlgoritmoGeneticoClientException;
import com.genetico.factory.GraficoServiceFactory;
import com.genetico.model.Populacao;
import com.genetico.service.GraficoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AlgoritmoGenetico {
    private static final Logger log = LogManager.getLogger();
    private final int qtdeGeracoes;
    private final int qtdeGenesCromossomo;
    private final int tamanhoPopulacao;
    private final int chanceOcorrenciaCrossover;
    private final int chanceOcorrenciaMutacao;
    private final GraficoService graficoService = GraficoServiceFactory.getGraficoService();
    private Populacao populacao;

    private AlgoritmoGenetico(Builder builder) {
        this.tamanhoPopulacao = builder.tamanhoPopulacao;
        this.qtdeGeracoes = builder.qtdeGeracoes;
        this.qtdeGenesCromossomo = builder.qtdeGenesCromossomo;
        this.chanceOcorrenciaCrossover = builder.chanceOcorrenciaCrossover;
        this.chanceOcorrenciaMutacao = builder.chanceOcorrenciaMutacao;
    }

    public Populacao reproduzir() {
        populacao = new Populacao(tamanhoPopulacao, chanceOcorrenciaCrossover, chanceOcorrenciaMutacao, qtdeGenesCromossomo);
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

    public static class Builder {
        private int tamanhoPopulacao;
        private int qtdeGeracoes;
        private int qtdeGenesCromossomo;
        private int chanceOcorrenciaCrossover;
        private int chanceOcorrenciaMutacao;

        public Builder tamanhoPopulacao(int tamanhoPopulacao) {
            this.tamanhoPopulacao = tamanhoPopulacao;
            return this;
        }

        public Builder qtdeGeracoes(int qtdeGeracoes) {
            this.qtdeGeracoes = qtdeGeracoes;
            return this;
        }

        public Builder qtdeGenesCromossomo(int qtdeGenesCromossomo) {
            this.qtdeGenesCromossomo = qtdeGenesCromossomo;
            return this;
        }

        public Builder chanceOcorrenciaCrossover(int chanceOcorrenciaCrossover) {
            this.chanceOcorrenciaCrossover = chanceOcorrenciaCrossover;
            return this;
        }

        public Builder chanceOcorrenciaMutacao(int chanceOcorrenciaMutacao) {
            this.chanceOcorrenciaMutacao = chanceOcorrenciaMutacao;
            return this;
        }

        public AlgoritmoGenetico build() throws AlgoritmoGeneticoClientException {
            return new AlgoritmoGenetico(this);
        }
    }
}