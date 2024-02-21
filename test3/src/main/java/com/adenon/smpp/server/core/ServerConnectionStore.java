package com.adenon.smpp.server.core;

import java.util.HashMap;


public class ServerConnectionStore {

    private final HashMap<String, ServerIOReactor> ioReactorMap = new HashMap<String, ServerIOReactor>();

    public ServerConnectionStore() {
    }

    public void add(final String connectionName,
                    final ServerIOReactor serverIOReactor) {
        this.ioReactorMap.put(connectionName, serverIOReactor);
    }

    public ServerIOReactor get(final String connectionName) {
        return this.ioReactorMap.get(connectionName);
    }

    public ServerIOReactor remove(final String connectionName) {
        return this.ioReactorMap.remove(connectionName);
    }

}
