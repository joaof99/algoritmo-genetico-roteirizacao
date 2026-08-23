package com.genetico.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.collections.impl.map.mutable.primitive.IntDoubleHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

public class MatrizDistancias {
    private static final Logger log = LogManager.getLogger();
    private static final IntObjectHashMap<IntDoubleHashMap> distancias = new IntObjectHashMap<>();

    private MatrizDistancias(){

    }

    public static void setDistancia(int idOrigem, int idDestino, double distancia) {
        log.info("Definindo distância entre os pontos de ID {} e ID {}", idOrigem, idDestino);
        distancias.getIfAbsentPut(idOrigem, IntDoubleHashMap::new).put(idDestino, distancia);
    }

    public static double getDistancia(int idOrigem, int idDestino) {
        var linha = distancias.get(idOrigem);

        if (linha == null || !linha.containsKey(idDestino)) {
            var mensagemErro = String.format("Distância não calculada entre pontos de ID %d e %d", idOrigem, idDestino);

            log.error(mensagemErro);
            throw new IllegalArgumentException(mensagemErro);
        }

        return linha.get(idDestino);
    }

    public static int getQuantidadeDistancias() {
        return distancias.values()
                .stream()
                .mapToInt(IntDoubleHashMap::size)
                .sum();
    }
}
