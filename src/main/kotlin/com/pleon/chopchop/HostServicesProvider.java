package com.pleon.chopchop;

import javafx.application.HostServices;

// TODO: Make this a singleton
public class HostServicesProvider {

    private static HostServices hostServices;

    public static HostServices getHostServices() {
        return hostServices;
    }

    public static void setHostServices(HostServices hostServices) {
        HostServicesProvider.hostServices = hostServices;
    }
}
