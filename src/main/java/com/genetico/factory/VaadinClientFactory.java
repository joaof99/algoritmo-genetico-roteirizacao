package com.genetico.factory;

import com.genetico.vaadin.VaadinClient;

public class VaadinClientFactory {
    private static VaadinClient vaadinClient;

    private VaadinClientFactory() {

    }

    public static VaadinClient getVaadinClient() {
        if (vaadinClient == null) {
            vaadinClient = new VaadinClient();
        }

        return vaadinClient;
    }

}
