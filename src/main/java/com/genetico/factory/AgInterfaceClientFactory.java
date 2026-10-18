package com.genetico.factory;

import com.genetico.aginterface.AgInterfaceClient;

public class AgInterfaceClientFactory {
    private static AgInterfaceClient agInterfaceClient;

    private AgInterfaceClientFactory() {

    }

    public static AgInterfaceClient getAgInterfaceClient() {
        if (agInterfaceClient == null) {
            agInterfaceClient = new AgInterfaceClient();
        }

        return agInterfaceClient;
    }

}
