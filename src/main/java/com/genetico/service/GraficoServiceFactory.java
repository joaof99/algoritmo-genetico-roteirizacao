package com.genetico.service;

public class GraficoServiceFactory {
    private static GraficoService instance;

    private GraficoServiceFactory() {

    }

    public static GraficoService getInstance() {
        if (instance == null) {
            instance = new GraficoService();
        }

        return instance;
    }

}
