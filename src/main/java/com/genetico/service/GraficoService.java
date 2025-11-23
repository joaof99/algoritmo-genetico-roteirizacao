package com.genetico.service;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.File;
import java.io.IOException;

public class GraficoService {
    private final int larguraGraficoFitness = 800;
    private final int alturaGraficoFitness = 600;
    private final String tituloGraficoFitness = "Evolução do Melhor Fitness";
    private final String tituloEixoX = "Geração";
    private final String tituloEixoY = "Fitness";
    private final String nomePastaGraficos;
    private final String nomeArquivoGrafico;
    private final XYChart chart;

    public GraficoService(String nomePastaGraficos, String nomeArquivoGrafico) {
        this.nomePastaGraficos = nomePastaGraficos;
        this.nomeArquivoGrafico = nomeArquivoGrafico;
        criarPastaGraficos();

        chart = configurarChart();
    }

    private void criarPastaGraficos() {
        var pastaGraficos = new File(getNomePastaGraficos());

        if (!pastaGraficos.exists()) {
            if (!pastaGraficos.mkdirs()) {
                throw new RuntimeException("Diretório " + pastaGraficos + " não conseguiu ser criado");
            }
        }
    }

    private XYChart configurarChart() {
        var chart = new XYChartBuilder()
                .width(larguraGraficoFitness)
                .height(alturaGraficoFitness)
                .title(tituloGraficoFitness)
                .xAxisTitle(tituloEixoX)
                .yAxisTitle(tituloEixoY)
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
            throw new IllegalArgumentException(String.format("A quantidade de gerações deve ser iguais a quantidade de valores de fitness." +
                    " Há %d gerações e %d valores de fitness", indicesGeracoes.length, melhoresFitnessPopulacoes.length));
        }

        checarIndicesGeracoesRepetidos(indicesGeracoes);

        criarImagemGrafico(indicesGeracoes, melhoresFitnessPopulacoes);
        new SwingWrapper<>(chart).displayChart();
    }

    private void checarIndicesGeracoesRepetidos(double[] indicesGeracoes) {
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
            var diretorioGraficoFitness = getNomePastaGraficos() + "/" + getNomeArquivoGrafico();

            chart.addSeries("Melhor Fitness", indicesGeracoes, melhoresFitnessPopulacoes).setMarker(SeriesMarkers.CIRCLE);
            BitmapEncoder.saveBitmap(chart, diretorioGraficoFitness, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException ioException) {
            System.out.println("Houve um erro de entrada e saída " + ioException.getMessage());
            throw new RuntimeException(ioException);
        }
    }

    public String getNomePastaGraficos() {
        return this.nomePastaGraficos;
    }

    public String getNomeArquivoGrafico() {
        return this.nomeArquivoGrafico;
    }
}
