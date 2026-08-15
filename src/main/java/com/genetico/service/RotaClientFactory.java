package com.genetico.service;

public class RotaClientFactory {
    private static RotaClient rotaClient;

    private RotaClientFactory() {

    }

    public static RotaClient getRotaClient() {
        if (rotaClient == null) {
            rotaClient = new RotaClient();
        }

        return rotaClient;
    }

}
