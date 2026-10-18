package com.genetico;

import com.genetico.factory.GraficoServiceFactory;
import com.genetico.model.Endereco;
import com.genetico.model.EstatisticasFitness;
import com.genetico.model.Populacao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AlgoritmoGenetico {
    private static final Logger log = LogManager.getLogger();
    private final int quantidadeGeracoes;
    private final Endereco[] enderecos;
    private final int tamanhoPopulacao;
    private final int chanceOcorrenciaCrossover;
    private final int chanceOcorrenciaMutacao;
    private Populacao populacao;
    private EstatisticasFitness estatisticasFitness;

    private AlgoritmoGenetico(Builder builder) {
        this.tamanhoPopulacao = builder.tamanhoPopulacao;
        this.quantidadeGeracoes = builder.qtdeGeracoes;
        this.enderecos = builder.enderecos;
        this.chanceOcorrenciaCrossover = builder.chanceOcorrenciaCrossover;
        this.chanceOcorrenciaMutacao = builder.chanceOcorrenciaMutacao;
    }

    public Populacao reproduzir() {
        log.info("=========== Inicializando roteirização do algoritmo genético com os seguintes parâmetros =============================");
        log.info("Quantidade de gerações {}", quantidadeGeracoes);
        log.info("Tamanho da população {}", tamanhoPopulacao);
        log.info("Chance de ocorrência de crossover {}%", chanceOcorrenciaCrossover);
        log.info("Chance de ocorrência de mutação {}%", chanceOcorrenciaMutacao);
        log.info("=================================================================================================================");

        populacao = new Populacao(tamanhoPopulacao, chanceOcorrenciaCrossover, chanceOcorrenciaMutacao, enderecos);
        log.info("População inicial");
        populacao.imprimirPopulacao();

        var melhoresFitnessPorGeracao = new ArrayList<Double>();
        melhoresFitnessPorGeracao.add(populacao.getCromossomos()[0].getFitness());

        for (int geracao = 1; geracao <= quantidadeGeracoes; geracao++) {
            populacao = populacao.gerarPopulacaoFilha();
            melhoresFitnessPorGeracao.add(populacao.getCromossomos()[0].getFitness());
        }

        estatisticasFitness = new EstatisticasFitness(melhoresFitnessPorGeracao);

        gerarGraficoEvolucaoFitness(melhoresFitnessPorGeracao);

        return populacao;
    }

    private void gerarGraficoEvolucaoFitness(List<Double> melhoresFitnessPorGeracao) {
        var indicesGeracoes = IntStream.rangeClosed(0, quantidadeGeracoes)
                .asDoubleStream()
                .toArray();

        var valoresFitness = melhoresFitnessPorGeracao.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        try {
            GraficoServiceFactory
                    .getGraficoService()
                    .gerarGraficoEvolucaoFitness(indicesGeracoes, valoresFitness);
        } catch (RuntimeException e) {
            log.warn("Não foi possível gerar o gráfico de evolução do fitness. A roteirização segue sem o gráfico", e);
        }
    }

    public EstatisticasFitness getEstatisticasFitness() {
        return estatisticasFitness;
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