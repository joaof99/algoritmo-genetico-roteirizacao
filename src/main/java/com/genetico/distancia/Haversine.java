package com.genetico.distancia;

import com.genetico.model.AlgoritmoGeneticoResponse;
import com.genetico.model.Endereco;
import com.genetico.model.MatrizDistancias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Haversine implements MetodoCalculoDistancia {
    private static final Logger log = LogManager.getLogger(Haversine.class);

    @Override
    public void registrarDistancias(AlgoritmoGeneticoResponse algoritmoGeneticoResponse) {
        var enderecos = algoritmoGeneticoResponse.enderecos();
        if (enderecos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum endereço encontrado. Impossível iniciar a roteirização");
        }

        if (enderecos.size() < 6) {
            throw new IllegalArgumentException(String.format("É necessário no mínimo 6 endereços para a roteirização. Encontrado %d", enderecos.size()));
        }

        for (int i = 0; i < enderecos.size(); i++) {
            for (int j = i + 1; j < enderecos.size(); j++) {
                var origem = enderecos.get(i);
                var destino = enderecos.get(j);

                var distancia = calcularDistancia(origem, destino);
                MatrizDistancias.setDistancia(origem.id(), destino.id(), distancia);
                MatrizDistancias.setDistancia(destino.id(), origem.id(), distancia);
            }
        }
    }

    double calcularDistancia(Endereco origem, Endereco destino) {
        double latitude1 = origem.latitude();
        double latitude2 = destino.latitude();

        double longitude1 = origem.longitude();
        double longitude2 = destino.longitude();

        var dLat = Math.toRadians(latitude2 - latitude1);
        var dLon = Math.toRadians(longitude2 - longitude1);

        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        var RAIO_TERRA_KM = 6371.0;

        var distancia = RAIO_TERRA_KM * c;

        log.info("Distância entre {} (ID: {}) e {} (ID: {}) é igual a {}", origem.descricao(), origem.id(), destino.descricao(), destino.id(), distancia);

        return distancia;
    }
}
