package com.genetico;

import com.genetico.model.Cromossomo;

import java.util.Random;

public class CalculadorDistancias {

    public static final int[][] distancias = inicializarDistancias();

    public static int[][] inicializarDistancias() {
        var distancias = new int[Cromossomo.QTDE_MAXIMA_GENES][Cromossomo.QTDE_MAXIMA_GENES];
        var randomizadorNumeros = new Random();

        for (int indiceTras = 0; indiceTras < Cromossomo.QTDE_MAXIMA_GENES; indiceTras++) {
            for (int indiceFrente = 0; indiceFrente < Cromossomo.QTDE_MAXIMA_GENES; indiceFrente++) {
                distancias[indiceTras][indiceFrente] = randomizadorNumeros.nextInt(5000);
            }
        }

        return distancias;
    }
}