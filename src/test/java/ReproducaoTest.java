import com.genetico.Reproducao;
import com.genetico.model.Cromossomo;
import com.genetico.model.Populacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReproducaoTest {
    @Test
    @DisplayName("Reprodução deve gerar população de tamanho fixo, ordenada e com fitness melhorado")
    public void reproducaoDasPopulacoesDeveOcorrerDeFormaCorreta() {
        var populacaoInicial = new Populacao(30, 50, 50);

        var reproducao = new Reproducao(30, populacaoInicial);

        var populacaoFinal = reproducao.reproduzir();

        var cromossomosPopulacaoFinal = populacaoFinal.getCromossomos();
        var cromossomosEsperados = cromossomosPopulacaoFinal
                .stream()
                .sorted(Comparator.comparingInt(Cromossomo::getFitness))
                .toList();

        var melhorFitnessInicial = populacaoInicial.getCromossomos().getFirst().getFitness();
        var melhorFitnessFinal = populacaoFinal.getCromossomos().getFirst().getFitness();

        assertTrue(melhorFitnessFinal < melhorFitnessInicial, "População não evoluiu");
        assertEquals(30, populacaoFinal.getCromossomos().size());
        assertEquals(cromossomosEsperados, populacaoFinal.getCromossomos());
    }
}
