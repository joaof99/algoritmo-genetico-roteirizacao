package com.genetico;

import com.genetico.model.Populacao;
import com.genetico.service.GraficoService;
import com.genetico.service.GraficoServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AlgoritmoGenetico {
    private static final Logger log = LogManager.getLogger();
    private MetodoCalculoDistancia metodoCalculoDistancia;
    private final int qtdeGeracoes;
    private final int tamanhoPopulacao;
    private final int chanceOcorrenciaCrossover;
    private final int chanceOcorrenciaMutacao;
    private final GraficoService graficoService = GraficoServiceFactory.getGraficoService();
    private Populacao populacao;

    private AlgoritmoGenetico(Builder builder) {
        this.tamanhoPopulacao = builder.tamanhoPopulacao;
        this.qtdeGeracoes = builder.qtdeGeracoes;
        this.chanceOcorrenciaCrossover = builder.chanceOcorrenciaCrossover;
        this.chanceOcorrenciaMutacao = builder.chanceOcorrenciaMutacao;
        this.metodoCalculoDistancia = builder.metodoCalculoDistancia;

        CalculadorDistancias.inicializar(metodoCalculoDistancia);
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

    public static class Builder {
        private MetodoCalculoDistancia metodoCalculoDistancia;
        private int tamanhoPopulacao;
        private int qtdeGeracoes;
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

        public Builder chanceOcorrenciaCrossover(int chanceOcorrenciaCrossover) {
            this.chanceOcorrenciaCrossover = chanceOcorrenciaCrossover;
            return this;
        }

        public Builder chanceOcorrenciaMutacao(int chanceOcorrenciaMutacao) {
            this.chanceOcorrenciaMutacao = chanceOcorrenciaMutacao;
            return this;
        }

        public Builder metodoCalculoDistancia(MetodoCalculoDistancia metodoCalculoDistancia) {
            this.metodoCalculoDistancia = metodoCalculoDistancia;
            return this;
        }

        public AlgoritmoGenetico build(){
            return new AlgoritmoGenetico(this);
        }
    }
}