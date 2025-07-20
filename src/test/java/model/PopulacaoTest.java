package model;

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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

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
        var populacao = inicializarPopulacaoTeste();

        assertEquals(70, populacao.getCromossomos().get(0).getFitness());
        assertEquals(70, populacao.getCromossomos().get(1).getFitness());
        assertEquals(90, populacao.getCromossomos().get(2).getFitness());
        assertEquals(100, populacao.getCromossomos().get(3).getFitness());
        assertEquals(220, populacao.getCromossomos().get(4).getFitness());
    }

    @ParameterizedTest
    @DisplayName("Deve selecionar corretamente o pai na roleta")
    @MethodSource("casosDeTesteParaSelecacaoRoleta")
    public void deveSelecionarCorretamenteOPaiNaRoleta(int numeroAleatorio, String formatacaoGenesEsperado) {
        var populacao = spy(inicializarPopulacaoTeste());

        doReturn(numeroAleatorio).when(populacao).gerarNumeroAleatorio(anyInt());

        var cromossomoPai = populacao.selecionarCromossomoPaiPorRoleta();

        assertEquals(formatacaoGenesEsperado, cromossomoPai.formatarGenes());
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
