package com.genetico.model;

import java.util.List;

public record EstatisticasFitness(List<Double> melhoresFitnessPorGeracao) {
    public double melhorFitnessPopulacaoInicial() {
        return melhoresFitnessPorGeracao.getFirst();
    }

    public double melhorFitnessPopulacaoFinal() {
        return melhoresFitnessPorGeracao.getLast();
    }
}
