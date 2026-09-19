package com.genetico;

import com.genetico.factory.GraficoServiceFactory;
import com.genetico.model.Endereco;
import com.genetico.model.Populacao;
import com.genetico.service.GraficoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AlgoritmoGenetico {
    private static final Logger log = LogManager.getLogger();
    private final int quantidadeGeracoes;
    private final Endereco[] enderecos;
    private final int tamanhoPopulacao;
    private final int chanceOcorrenciaCrossover;
    private final int chanceOcorrenciaMutacao;
    private final GraficoService graficoService = GraficoServiceFactory.getGraficoService();
    private Populacao populacao;

    private AlgoritmoGenetico(Builder builder) {
        this.tamanhoPopulacao = builder.tamanhoPopulacao;
        this.quantidadeGeracoes = builder.qtdeGeracoes;
        this.enderecos = builder.enderecos;
        this.chanceOcorrenciaCrossover = builder.chanceOcorrenciaCrossover;
        this.chanceOcorrenciaMutacao = builder.chanceOcorrenciaMutacao;
    }

    public Populacao reproduzir() {
        log.info("Iniciando roteirização do algoritmo genético com os seguintes parâmetros:");

        log.info("==========================================================================");
        log.info("Quantidade de gerações {}", quantidadeGeracoes);
        log.info("Tamanho da população {}", tamanhoPopulacao);
        log.info("Chance de ocorrência de crossover {}%", chanceOcorrenciaCrossover);
        log.info("Chance de ocorrência de mutação {}%", chanceOcorrenciaMutacao);
        log.info("==========================================================================");

        populacao = new Populacao(tamanhoPopulacao, chanceOcorrenciaCrossover, chanceOcorrenciaMutacao, enderecos);
        log.info("População inicial");
        populacao.imprimirPopulacao();

        var indicesGeracoes = new double[quantidadeGeracoes];
        var melhoresFitnessPopulacoes = new double[quantidadeGeracoes];

        for (int indiceGeracao = 0; indiceGeracao < quantidadeGeracoes; indiceGeracao++) {
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
        private int chanceOcorrenciaCrossover;
        private int chanceOcorrenciaMutacao;
        private Endereco[] enderecos;

        public Builder tamanhoPopulacao(int tamanhoPopulacao) {
            this.tamanhoPopulacao = tamanhoPopulacao;
            return this;
        }

        public Builder qtdeGeracoes(int qtdeGeracoes) {
            this.qtdeGeracoes = qtdeGeracoes;
            return this;
        }

        public Builder enderecos(Endereco[] enderecos) {
            this.enderecos = enderecos;
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

        public AlgoritmoGenetico build() {
            return new AlgoritmoGenetico(this);
        }
    }
}