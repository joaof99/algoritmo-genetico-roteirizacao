package com.genetico.factory;

import com.genetico.client.RotaClient;
import com.genetico.exception.RotaClientException;

public class RotaClientFactory {
    private static RotaClient rotaClient;

    private RotaClientFactory() {

    }

    public static RotaClient getRotaClient() throws RotaClientException {
        if (rotaClient == null) {
            rotaClient = new RotaClient();
        }

        return rotaClient;
    }

}
