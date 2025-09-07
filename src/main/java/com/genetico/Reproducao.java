package com.genetico;

import com.genetico.model.Populacao;

public class Reproducao {
    private final int qtdeGeracoes;
    private Populacao populacao;

    public Reproducao(int qtdeGeracoes, Populacao populacao) {
        this.qtdeGeracoes = qtdeGeracoes;
        this.populacao = populacao;
    }

    public Populacao reproduzir() {
        var contadorGeracoes = 0;

        while (contadorGeracoes < qtdeGeracoes) {
            var novaPopulacao = populacao.gerarPopulacaoFilha();
            populacao = new Populacao(novaPopulacao.getCromossomos());

            contadorGeracoes++;
        }

        return populacao;
    }
}