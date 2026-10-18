package com.genetico.factory;

import java.util.Random;

public class RandomizadorFactory {
    private static Random randomizador;

    private RandomizadorFactory() {}

    public static Random getRandomizador() {
        if (randomizador == null) {
            randomizador = new Random();
        }

        return randomizador;
    }
}
