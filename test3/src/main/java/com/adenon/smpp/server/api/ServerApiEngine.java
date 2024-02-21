package com.adenon.smpp.server.api;

import java.util.Hashtable;
import java.util.Map;

import com.adenon.api.smpp.sdk.LogDescriptor;
import com.adenon.smpp.server.core.IServerCallback;
import com.adenon.smpp.server.core.ServerApiProperties;

public class ServerApiEngine {

    public static Map<String, ServerApiEngine> instances = new Hashtable<String, ServerApiEngine>();

    private ServerApi                          smppApi;

    private final String                       serverName;

    private ServerApiEngine(final String serverName) {
        this.serverName = serverName.trim();
        ServerApiEngine.instances.put(this.serverName, this);
    }

    public static synchronized ServerApiEngine getServerApiEngine(final String name) {
        ServerApiEngine apiEngine = ServerApiEngine.instances.get(name);
        if (apiEngine == null) {
            apiEngine = new ServerApiEngine(name);
        }
        return apiEngine;
    }

    public ServerApi getServerApi(final IServerCallback serverCallback,
                                  final LogDescriptor logDescriptor) {
        if (this.smppApi == null) {
            this.smppApi = new ServerApi(this.serverName, serverCallback, logDescriptor, new ServerApiProperties());
        }
        return this.smppApi;
    }

    public ServerApi getServerApi(final IServerCallback serverCallback,
                                  final LogDescriptor logDescriptor,
                                  final ServerApiProperties apiProperties) {
        if (this.smppApi == null) {
            this.smppApi = new ServerApi(this.serverName, serverCallback, logDescriptor, apiProperties);
        }
        return this.smppApi;
    }

    public void dispose() {
        ServerApiEngine.instances.remove(this.serverName);
    }


}
