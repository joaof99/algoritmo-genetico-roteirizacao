package com.genetico.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.collections.impl.map.mutable.primitive.LongDoubleHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;

public class MatrizDistancias {
    private static final Logger log = LogManager.getLogger();
    private final LongObjectHashMap<LongDoubleHashMap> distancias = new LongObjectHashMap<>();

    public void setDistancia(long idOrigem, long idDestino, double distancia) {
        log.info("Definindo distância entre os pontos de ID {} e ID {}", idOrigem, idDestino);
        distancias.getIfAbsentPut(idOrigem, LongDoubleHashMap::new).put(idDestino, distancia);
    }

    public double getDistancia(long idOrigem, long idDestino) {
        var linha = distancias.get(idOrigem);

        if (linha == null || !linha.containsKey(idDestino)) {
            var mensagemErro = String.format("Distância não calculada entre pontos de ID %d e %d", idOrigem, idDestino);

            log.error(mensagemErro);
            throw new IllegalArgumentException(mensagemErro);
        }

        return linha.get(idDestino);
    }

    public int getQuantidadeDistancias(){
        return distancias.values()
                .stream()
                .mapToInt(LongDoubleHashMap::size)
                .sum();
    }
}
