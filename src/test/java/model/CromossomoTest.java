package model;

import com.genetico.model.Cromossomo;
import com.genetico.model.Individuo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CromossomoTest {

    @Test
    @DisplayName("Valor do fitness deve ser calculado corretamente")
    public void valorDoFitnessDeveSeCalculadoCorretamente() {
        var cromossomo = new Cromossomo(inicializarValoresGenesFixos());

        assertEquals(70, cromossomo.getFitness());
    }

    private int[] inicializarValoresGenesFixos() {
        var genesFixos = new int[Cromossomo.QTDE_MAXIMA_GENES];

        for (int indice = 0; indice < genesFixos.length; indice++) {
            genesFixos[indice] = indice;
        }

        return genesFixos;
    }

    @Test
    @DisplayName(value = "Genes devem ser inicializados com o tamanho correto definido na constante")
    public void genesDevemSerInicializadosComOTamanhoCorreto() {
        assertEquals(Cromossomo.QTDE_MAXIMA_GENES, new Individuo().getCromosso().getGenes().length);
    }

    @Test
    @DisplayName(value = "Genes devem ser formatados corretamente com caracter delimitador: |")
    public void genesDevemSerFormatadosCorretamenteAoImprimir() {
        var padraoImpressaoCromossomo = "^(\\d+\\s\\|\\s)*\\d+$";

        boolean impressaoCromossomoEstaNoPadrao = new Individuo()
                .getCromosso()
                .formatarGenes()
                .matches(padraoImpressaoCromossomo);

        assertTrue(impressaoCromossomoEstaNoPadrao);
    }

    @Test
    @DisplayName(value = "Não deve existir genes repetidos em um cromossomo")
    public void naoDeveExistirGenesRepetidosNoCromossomo() {
        var cromossomo = new Cromossomo();

        Set<Integer> genesSemRepeticao = new HashSet<>();

        for (int gene : cromossomo.getGenes()) {
            boolean foiAdicionado = genesSemRepeticao.add(gene);

            assertTrue(foiAdicionado);
        }
    }

    @Test
    @DisplayName(value = "O gene de origem do cromossomo deve sempre ser igual a 0")
    public void oGeneOrigemDoCromossomoDeveSerSempreZero() {
        assertEquals(0, new Cromossomo().getGenes()[0]);
    }

    @ParameterizedTest
    @MethodSource("casosDeTesteParaCrossoverPmx")
    @DisplayName("Deve realizar crossover PMX corretamente com diferentes combinações de genes")
    public void deveRealizarComSucessoOCrossoverPmx(
            int[] genesFixos1,
            int[] genesFixos2,
            int[] pontosCorteFixos,
            String genesEsperadosFilho1,
            String genesEsperadosFilho2) {

        var pai1 = spy(new Cromossomo(genesFixos1));
        var pai2 = new Cromossomo(genesFixos2);
        var random = mock(Random.class);

        when(random.nextInt(anyInt())).thenReturn(pontosCorteFixos[0], pontosCorteFixos[1]);

        doCallRealMethod().when(pai1).realizarCrossoverPmx(pai2, random);

        var filhos = pai1.realizarCrossoverPmx(pai2, random);

        assertEquals(genesEsperadosFilho1, filhos.getFirst().formatarGenes());
        assertEquals(genesEsperadosFilho2, filhos.getLast().formatarGenes());
    }

    private static Stream<Arguments> casosDeTesteParaCrossoverPmx() {
        return Stream.of(
                Arguments.of(
                        new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                        new int[]{0, 4, 3, 2, 1, 7, 6, 5, 8, 9},
                        new int[]{3, 5},
                        "0 | 4 | 2 | 3 | 1 | 7 | 6 | 5 | 8 | 9 | 170",
                        "0 | 1 | 3 | 2 | 4 | 5 | 6 | 7 | 8 | 9 | 100"
                ),
                Arguments.of(
                        new int[]{0, 1, 2, 3, 4, 5, 6, 7},
                        new int[]{0, 3, 2, 1, 4, 5, 6, 7},
                        new int[]{0, 3},
                        "0 | 3 | 2 | 1 | 4 | 5 | 6 | 7 | 105",
                        "0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 55"
                )
        );
    }
}
