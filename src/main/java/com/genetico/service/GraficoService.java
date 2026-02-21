package com.genetico.service;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GraficoService {
    private final XYChart chart;

    public GraficoService() {
        this.chart = configurarChart();
        criarPastaGraficos();
    }

    private void criarPastaGraficos() {
        try {
            Files.createDirectories(Paths.get("graficos_fitness"));
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o diretório.", e);
        }
    }

    private XYChart configurarChart() {
        var chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Evolução do Melhor Fitness")
                .xAxisTitle("Geração")
                .yAxisTitle("Fitness")
                .build();

        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setMarkerSize(6);
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setXAxisDecimalPattern("#0");
        chart.getStyler().setYAxisDecimalPattern("#0.00");

        return chart;
    }

    public void gerarGraficoEvolucaoFitness(double[] indicesGeracoes, double[] melhoresFitnessPopulacoes) {
        if (indicesGeracoes.length != melhoresFitnessPopulacoes.length) {
            throw new IllegalArgumentException(String.format("A quantidade de gerações devem ser iguais a quantidade de valores de fitness." +
                    " Há %d gerações e %d valores de fitness", indicesGeracoes.length, melhoresFitnessPopulacoes.length));
        }

        checarSeHaIndicesGeracoesRepetidos(indicesGeracoes);

        criarImagemGrafico(indicesGeracoes, melhoresFitnessPopulacoes);
        new SwingWrapper<>(chart).displayChart();
    }

    private void checarSeHaIndicesGeracoesRepetidos(double[] indicesGeracoes) {
        for (int indice = 0; indice < indicesGeracoes.length; indice++) {
            if (indicesGeracoes[indice] != indice) {
                var mensagemExcecao = String
                        .format("Índice %d com valor %.0f não está na ordem. Aqui deveria ser: %d",
                                indice, indicesGeracoes[indice], indice);

                throw new IllegalArgumentException(mensagemExcecao);
            }
        }
    }

    private void criarImagemGrafico(double[] indicesGeracoes, double[] melhoresFitnessPopulacoes) {
        try {
            var dataAtualFormatada = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss"));
            var diretorioCompletoGraficoFitness = "graficos_fitness" + "/" + "evolucao_fitness_" + dataAtualFormatada;

            chart.addSeries("Melhor Fitness", indicesGeracoes, melhoresFitnessPopulacoes).setMarker(SeriesMarkers.CIRCLE);
            BitmapEncoder.saveBitmap(chart, diretorioCompletoGraficoFitness, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException ioException) {
            System.out.println("Houve um erro de entrada e saída " + ioException.getMessage());
            throw new RuntimeException(ioException);
        }
    }
}
