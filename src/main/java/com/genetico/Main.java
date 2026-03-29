package com.genetico;

import com.genetico.model.Populacao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("População inicial");
        var tamanhoPopulacao = 30;
        var chanceFixaOcorrenciaCrossover = 50;
        var chanceFixaOcorrenciaMutacao = 50;
        var populacaoInicial = new Populacao(tamanhoPopulacao, chanceFixaOcorrenciaCrossover, chanceFixaOcorrenciaMutacao);
        populacaoInicial.imprimirPopulacao();

        log.info("População final");
        var qtdeGeracoes = 50;
        var populacaoFinal = new Reproducao(qtdeGeracoes, populacaoInicial).reproduzir();
        populacaoFinal.imprimirPopulacao();
    }
}