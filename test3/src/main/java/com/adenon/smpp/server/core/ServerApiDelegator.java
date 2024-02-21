package com.adenon.smpp.server.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.adenon.api.smpp.core.IOReactorStorage;
import com.adenon.api.smpp.core.SmppIOReactorSelectorEx;
import com.adenon.api.smpp.core.threadpool.SmppThreadPool;
import com.adenon.api.smpp.sdk.LogDescriptor;
import com.adenon.smpp.server.managers.ServerConnectionManager;
import com.adenon.smpp.server.managers.ServerLogManager;
import com.adenon.smpp.server.managers.ServerMessagingManager;

public class ServerApiDelegator {

    private final ServerLogManager        logManager;
    private final IOReactorStorage        smppIOReactorStorage;
    private final SmppIOReactorSelectorEx smppIOReactorSelector;
    private final SmppThreadPool          smppThreadPool;
    private final Object                  syncObject = new Object();
    private ServerApiProperties           apiProperties;
    private final ServerConnectionManager connectionManager;
    private final IServerCallback         serverCallback;
    private final String                  serverName;
    private final ServerConnectionStore   serverConnectionStore;
    private final ServerMessagingManager  serverMessagingManager;

    public ServerApiDelegator(final String serverName,
                              final IServerCallback serverCallback,
                              final LogDescriptor descriptor,
                              final ServerApiProperties apiProperties) {

        this.serverName = serverName;
        this.serverCallback = serverCallback;
        this.setApiProperties(apiProperties);

        this.logManager = new ServerLogManager(serverName, descriptor, this, this.syncObject);
        this.smppIOReactorStorage = new IOReactorStorage();


        int threadCount = 50;
        if (apiProperties != null) {
            threadCount = apiProperties.getThreadCount();
        }

        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        this.smppThreadPool = new SmppThreadPool(executor);


        this.smppIOReactorSelector = new SmppIOReactorSelectorEx(this.getSmppIOReactorStorage(), this.smppThreadPool, this.getLogManager().getLogger());
        this.smppIOReactorSelector.setDaemon(true);
        this.smppIOReactorSelector.start();

        try {
            final ServerIOReactorAccept ioReactorAccept = new ServerIOReactorAccept(serverName,
                                                                                    this,
                                                                                    this.getApiProperties(),
                                                                                    this.getLogManager().getLogger(),
                                                                                    this.getSmppIOReactorStorage());
            ioReactorAccept.start();
        } catch (final Exception e) {
            this.getLogManager().getLogger().error("ServerApiDelegator", "ServerApiDelegator", 0, null, " : Error : " + e.getMessage(), e);
            System.exit(0);
        }
        this.connectionManager = new ServerConnectionManager(this);
        this.serverConnectionStore = new ServerConnectionStore();
        this.serverMessagingManager = new ServerMessagingManager(this);

    }

    public ServerApiProperties getApiProperties() {
        return this.apiProperties;
    }

    public void setApiProperties(final ServerApiProperties apiProperties) {
        this.apiProperties = apiProperties;
    }

    public IOReactorStorage getSmppIOReactorStorage() {
        return this.smppIOReactorStorage;
    }

    public ServerConnectionManager getConnectionManager() {
        return this.connectionManager;
    }

    public IServerCallback getServerCallback() {
        return this.serverCallback;
    }

    public String getServerName() {
        return this.serverName;
    }

    public ServerLogManager getLogManager() {
        return this.logManager;
    }

    public ServerConnectionStore getServerConnectionStore() {
        return this.serverConnectionStore;
    }

    public ServerMessagingManager getServerMessagingManager() {
        return this.serverMessagingManager;
    }


}
