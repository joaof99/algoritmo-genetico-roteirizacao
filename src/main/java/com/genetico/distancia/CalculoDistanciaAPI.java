package com.genetico.distancia;

import com.genetico.model.AlgoritmoGeneticoRequest;
import com.genetico.model.MatrizDistancias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CalculoDistanciaAPI implements MetodoCalculoDistancia {
    private static final Logger log = LogManager.getLogger(CalculoDistanciaAPI.class);

    @Override
    public void registrarDistancias(AlgoritmoGeneticoRequest algoritmoGeneticoRequest) {
        log.info("Inicializando distâncias via API...");
        for (var distancia : algoritmoGeneticoRequest.distancias()) {
            MatrizDistancias.setDistancia(distancia.idOrigem(), distancia.idDestino(), distancia.distancia());
        }
    }
}
