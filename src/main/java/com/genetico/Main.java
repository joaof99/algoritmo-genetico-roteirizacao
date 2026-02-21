package com.genetico;

import com.genetico.model.Populacao;
import com.genetico.service.GraficoService;

public class Main {
    public static void main(String[] args) {
        System.out.println("População inicial");
        var tamanhoPopulacao = 30;
        var chanceFixaOcorrenciaCrossover = 50;
        var chanceFixaOcorrenciaMutacao = 50;
        var populacaoInicial = new Populacao(tamanhoPopulacao, chanceFixaOcorrenciaCrossover, chanceFixaOcorrenciaMutacao);
        populacaoInicial.imprimirPopulacao();

        System.out.println("População final");
        var qtdeGeracoes = 50;
        var populacaoFinal = new Reproducao(qtdeGeracoes, populacaoInicial, new GraficoService()).reproduzir();
        populacaoFinal.imprimirPopulacao();
    }
}