package com.genetico.service;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.File;
import java.io.IOException;

public class GraficoService {

    public GraficoService() {
        criarPastaGraficos();
    }

    public void gerarGraficoEvolucaoFitness(double[] indicesGeracoes, double[] valoresFitness) {
        if (indicesGeracoes.length != valoresFitness.length) {
            throw new IllegalArgumentException(String.format("A quantidade de gerações deve ser iguais a quantidade de valores de fitness." +
                    " Há %d gerações e %d valores de fitness", indicesGeracoes.length, valoresFitness.length));
        }

        var chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Evolução do Melhor Fitness")
                .xAxisTitle("Geração")
                .yAxisTitle("Fitness")
                .build();

        try {
            var diretorioGraficoFitness = "graficos/evolucao_fitness.png";
            chart.addSeries("Melhor Fitness", indicesGeracoes, valoresFitness).setMarker(SeriesMarkers.CIRCLE);
            BitmapEncoder.saveBitmap(chart, diretorioGraficoFitness, BitmapEncoder.BitmapFormat.PNG);

            System.out.println("Gráfico gerado com XChart em: " + diretorioGraficoFitness);
        } catch (IOException e) {
            System.out.println("Houve um erro de entrada e saída " + e.getMessage());
            throw new RuntimeException(e);
        }


        new SwingWrapper<>(chart).displayChart();
    }

    private void criarPastaGraficos() {
        var pastaGraficos = new File("graficos");

        if (!pastaGraficos.exists()) {
            if (!pastaGraficos.mkdirs()) {
                throw new RuntimeException("Diretório " + pastaGraficos + " não conseguiu ser criado");
            }
        }
    }
}
