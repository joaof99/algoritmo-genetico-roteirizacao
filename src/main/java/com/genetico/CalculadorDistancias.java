package com.genetico;

import com.genetico.model.Cromossomo;
import com.genetico.model.Endereco;

import java.util.Random;

public class CalculadorDistancias {

    private static final int[][] distancias = inicializarDistanciasAleatoriamente();
    private static final double RAIO_TERRA_KM = 6371.0;

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

    public static int obterDistanciaEntreDuasCidades(int indiceCidadeOrigem, int indiceCidadeDestino) {
        return distancias[indiceCidadeOrigem][indiceCidadeDestino];
    }

    public static double calcularDistanciaHaversine(Endereco origem, Endereco destino) {
        double latitude1 = origem.latitude();
        double latitude2 = destino.latitude();

        double longitude1 = origem.longitude();
        double longitude2 = destino.longitude();

        var dLat = Math.toRadians(latitude2 - latitude1);
        var dLon = Math.toRadians(longitude2 - longitude1);

        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAIO_TERRA_KM * c;
    }
}