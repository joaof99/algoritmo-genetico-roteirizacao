package com.genetico;

import com.genetico.model.Populacao;
import com.genetico.service.GraficoService;

public class Main {
    public static void main(String[] args) {
        System.out.println("População inicial");
        var populacaoInicial = new Populacao(30, 50, 50);
        populacaoInicial.imprimirPopulacao();

        System.out.println("População final");
        var populacaoFinal = new Reproducao(50, populacaoInicial, new GraficoService("graficos_fitness", "evolucao_fitness")).reproduzir();
        populacaoFinal.imprimirPopulacao();
    }
}