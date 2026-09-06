package com.genetico.factory;

import com.genetico.client.AlgoritmoGeneticoClient;
import com.genetico.exception.AlgoritmoGeneticoClientException;

public class AlgoritmoGeneticoClientFactory {
    private static AlgoritmoGeneticoClient algoritmoGeneticoClient;

    private AlgoritmoGeneticoClientFactory() {

    }

    public static AlgoritmoGeneticoClient getAlgoritmoGeneticoClient() throws AlgoritmoGeneticoClientException {
        if (algoritmoGeneticoClient == null) {
            algoritmoGeneticoClient = new AlgoritmoGeneticoClient();
        }

        return algoritmoGeneticoClient;
    }

}
