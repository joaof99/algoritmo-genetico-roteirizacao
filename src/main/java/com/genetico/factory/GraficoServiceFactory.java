package com.genetico.factory;

import com.genetico.service.GraficoService;

public class GraficoServiceFactory {
    private static GraficoService graficoService;

    private GraficoServiceFactory() {

    }

    public static GraficoService getGraficoService() {
        if (graficoService == null) {
            graficoService = new GraficoService();
        }

        return graficoService;
    }

}
