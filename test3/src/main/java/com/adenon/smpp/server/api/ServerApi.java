package com.adenon.smpp.server.api;

import com.adenon.api.smpp.sdk.LogDescriptor;
import com.adenon.smpp.server.core.IServerCallback;
import com.adenon.smpp.server.core.ServerApiDelegator;
import com.adenon.smpp.server.core.ServerApiProperties;

public class ServerApi {

    private final ServerApiDelegator  apiDelegator;
    private final ServerConnectionApi serverConnectionApi;
    private final ServerMessagingApi  serverMessagingApi;
    private final ServerLoggerApi     serverLoggerApi;

    public ServerApi(final String serverName,
                     final IServerCallback serverCallback,
                     final LogDescriptor descriptor,
                     final ServerApiProperties apiProperties) {
        this.apiDelegator = new ServerApiDelegator(serverName, serverCallback, descriptor, apiProperties);
        this.serverConnectionApi = new ServerConnectionApi(this.apiDelegator);
        this.serverMessagingApi = new ServerMessagingApi(this.apiDelegator);
        this.serverLoggerApi = new ServerLoggerApi(this.apiDelegator);
    }

    public ServerConnectionApi getServerConnectionApi() {
        return this.serverConnectionApi;
    }

    public ServerMessagingApi getServerMessagingApi() {
        return this.serverMessagingApi;
    }

    public ServerLoggerApi getServerLoggerApi() {
        return this.serverLoggerApi;
    }

}
