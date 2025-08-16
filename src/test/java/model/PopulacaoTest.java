package model;

import com.genetico.CalculadorDistancias;
import com.genetico.model.Cromossomo;
import com.genetico.model.Populacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class PopulacaoTest {

    @Test
    @DisplayName("Tamanho da população deve ser inicializado corretamente")
    public void tamanhoDaPopulacaoDeveSerInicializadoCorretamente() {
        var populacao = new Populacao();

        assertEquals(Populacao.TAMANHO_POPULACAO, populacao.getCromossomos().size());
    }

    @Test
    @DisplayName(value = "deve ordenar os fitness dos cromossomos em ordem crescente")
    public void deveOrdenarOsFitnessDosCromossomosEmOrdemCrescente() {
        try (var calculadorDeDistancias = mockStatic(CalculadorDistancias.class)) {
            calculadorDeDistancias
                    .when(CalculadorDistancias::getDistancias)
                    .thenReturn(inicializarDistanciasFixas());

            var populacao = inicializarPopulacaoTeste();

            assertEquals(70, populacao.getCromossomos().get(0).getFitness());
            assertEquals(70, populacao.getCromossomos().get(1).getFitness());
            assertEquals(90, populacao.getCromossomos().get(2).getFitness());
            assertEquals(100, populacao.getCromossomos().get(3).getFitness());
            assertEquals(220, populacao.getCromossomos().get(4).getFitness());
        }
    }

    @ParameterizedTest
    @DisplayName("Deve selecionar corretamente o pai na roleta")
    @MethodSource("casosDeTesteParaSelecacaoRoleta")
    public void deveSelecionarCorretamenteOPaiNaRoleta(int numeroAleatorio, String formatacaoGenesEsperado) {
        try (var calculadorDeDistancias = mockStatic(CalculadorDistancias.class)) {
            calculadorDeDistancias
                    .when(CalculadorDistancias::getDistancias)
                    .thenReturn(inicializarDistanciasFixas());

            var populacao = spy(inicializarPopulacaoTeste());

            doReturn(numeroAleatorio).when(populacao).gerarNumeroAleatorio(anyInt());

            var cromossomoPai = populacao.selecionarCromossomoPaiPorRoleta();
            assertEquals(formatacaoGenesEsperado, cromossomoPai.formatarGenes());
        }
    }

    private int[][] inicializarDistanciasFixas() {
        var distancias = new int[10][10];

        distancias[0][0] = 10;
        distancias[0][1] = 10;
        distancias[0][2] = 20;
        distancias[0][3] = 30;
        distancias[0][4] = 40;
        distancias[0][5] = 50;
        distancias[0][6] = 60;
        distancias[0][7] = 70;
        distancias[0][8] = 80;
        distancias[0][9] = 90;

        distancias[1][0] = 15;
        distancias[1][2] = 15;
        distancias[1][3] = 25;
        distancias[1][4] = 35;
        distancias[1][5] = 45;
        distancias[1][6] = 55;
        distancias[1][7] = 65;
        distancias[1][8] = 75;
        distancias[1][9] = 85;

        distancias[2][0] = 10;
        distancias[2][1] = 10;
        distancias[2][2] = 10;
        distancias[2][4] = 20;
        distancias[2][5] = 30;
        distancias[2][6] = 40;
        distancias[2][7] = 50;
        distancias[2][8] = 60;
        distancias[2][9] = 70;

        distancias[3][0] = 5;
        distancias[3][1] = 5;
        distancias[3][2] = 5;
        distancias[3][3] = 5;
        distancias[3][4] = 5;
        distancias[3][5] = 15;
        distancias[3][6] = 25;
        distancias[3][7] = 35;
        distancias[3][8] = 45;
        distancias[3][9] = 55;

        distancias[4][0] = 10;
        distancias[4][1] = 10;
        distancias[4][2] = 10;
        distancias[4][3] = 10;
        distancias[4][4] = 10;
        distancias[4][5] = 10;
        distancias[4][6] = 20;
        distancias[4][7] = 30;
        distancias[4][8] = 40;
        distancias[4][9] = 50;

        distancias[5][0] = 5;
        distancias[5][1] = 5;
        distancias[5][2] = 5;
        distancias[5][3] = 5;
        distancias[5][4] = 5;
        distancias[5][5] = 5;
        distancias[5][6] = 5;
        distancias[5][7] = 15;
        distancias[5][8] = 25;
        distancias[5][9] = 35;

        distancias[6][0] = 10;
        distancias[6][1] = 10;
        distancias[6][2] = 10;
        distancias[6][3] = 10;
        distancias[6][4] = 10;
        distancias[6][5] = 10;
        distancias[6][6] = 10;
        distancias[6][7] = 10;
        distancias[6][8] = 20;
        distancias[6][9] = 30;

        distancias[7][0] = 5;
        distancias[7][1] = 5;
        distancias[7][2] = 5;
        distancias[7][3] = 5;
        distancias[7][4] = 5;
        distancias[7][5] = 5;
        distancias[7][6] = 5;
        distancias[7][7] = 5;
        distancias[7][8] = 5;
        distancias[7][9] = 15;

        distancias[8][0] = 10;
        distancias[8][1] = 10;
        distancias[8][2] = 10;
        distancias[8][3] = 10;
        distancias[8][4] = 10;
        distancias[8][5] = 10;
        distancias[8][6] = 10;
        distancias[8][7] = 10;
        distancias[8][8] = 10;
        distancias[8][9] = 10;

        distancias[9][0] = 10;
        distancias[9][1] = 10;
        distancias[9][2] = 10;
        distancias[9][3] = 10;
        distancias[9][4] = 10;
        distancias[9][5] = 10;
        distancias[9][6] = 10;
        distancias[9][7] = 10;
        distancias[9][8] = 10;
        distancias[9][9] = 10;

        return distancias;
    }

    private static Stream<Arguments> casosDeTesteParaSelecacaoRoleta() {
        return Stream.of(
                Arguments.of(230, "0 | 1 | 2 | 3 | 4 | 5 | 7 | 6 | 8 | 9 | 90"),
                Arguments.of(50, "0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 70")
        );
    }

    private Populacao inicializarPopulacaoTeste() {
        var genes1 = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        var genes2 = new int[]{0, 1, 2, 3, 4, 5, 7, 6, 8, 9};
        var genes3 = new int[]{0, 1, 2, 4, 3, 5, 6, 7, 8, 9};
        var genes4 = new int[]{0, 5, 4, 3, 2, 6, 7, 8, 1, 9};
        var genes5 = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        var cromossomo1 = new Cromossomo(genes1);
        var cromossomo2 = new Cromossomo(genes2);
        var cromossomo3 = new Cromossomo(genes3);
        var cromossomo4 = new Cromossomo(genes4);
        var cromossomo5 = new Cromossomo(genes5);

        var cromossomos = new ArrayList<Cromossomo>();

        cromossomos.add(cromossomo1);
        cromossomos.add(cromossomo2);
        cromossomos.add(cromossomo3);
        cromossomos.add(cromossomo4);
        cromossomos.add(cromossomo5);

        return new Populacao(cromossomos);
    }
}
