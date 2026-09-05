package com.genetico.distancia;

import com.genetico.model.AlgoritmoGeneticoResponse;
import com.genetico.model.MatrizDistancias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CalculoDistanciaAPI implements MetodoCalculoDistancia {
    private static final Logger log = LogManager.getLogger(CalculoDistanciaAPI.class);

    @Override
    public void registrarDistancias(AlgoritmoGeneticoResponse algoritmoGeneticoResponse) {
        log.info("Inicializando distâncias via API...");
        for (var distancia : algoritmoGeneticoResponse.distancias()) {
            MatrizDistancias.setDistancia(distancia.idOrigem(), distancia.idDestino(), distancia.distancia());
        }
    }
}
