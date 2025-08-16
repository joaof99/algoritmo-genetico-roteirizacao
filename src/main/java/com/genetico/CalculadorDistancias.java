package com.genetico;

import com.genetico.model.Cromossomo;

import java.util.Random;

public class CalculadorDistancias {

    private static final int[][] distancias = inicializarDistanciasAleatoriamente();

    public static int[][] inicializarDistanciasAleatoriamente() {
        var distancias = new int[Cromossomo.QTDE_MAXIMA_GENES][Cromossomo.QTDE_MAXIMA_GENES];
        var randomizadorNumeros = new Random();

        for (int indiceTras = 0; indiceTras < Cromossomo.QTDE_MAXIMA_GENES; indiceTras++) {
            for (int indiceFrente = 0; indiceFrente < Cromossomo.QTDE_MAXIMA_GENES; indiceFrente++) {
                distancias[indiceTras][indiceFrente] = randomizadorNumeros.nextInt(5000);
            }
        }

        return distancias;
    }

    public static int[][] getDistancias() {
        return distancias;
    }
}