package com.genetico.mutacao;

import com.genetico.factory.RandomizadorFactory;
import com.genetico.model.Cromossomo;

import java.util.Random;

public class Swap implements MetodoMutacao {
    private final Random randomizador = RandomizadorFactory.getRandomizador();

    @Override
    public void realizarMutacao(Cromossomo cromossomo) {
        int indiceAleatorioGene1, indiceAleatorioGene2;

        do {
            var quantidadeGenes = cromossomo.getGenes().length;
            indiceAleatorioGene1 = randomizador.nextInt(1, quantidadeGenes);
            indiceAleatorioGene2 = randomizador.nextInt(1, quantidadeGenes);
        } while (indiceAleatorioGene1 == indiceAleatorioGene2);

        var genesAtuais = cromossomo.getGenes();
        var valorGeneAnteriorIndice1 = genesAtuais[indiceAleatorioGene1];

        genesAtuais[indiceAleatorioGene1] = genesAtuais[indiceAleatorioGene2];
        genesAtuais[indiceAleatorioGene2] = valorGeneAnteriorIndice1;
    }
}
