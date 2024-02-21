package com.adenon.api.smpp.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.adenon.api.smpp.connection.SmppConnectionGroupManager;
import com.adenon.api.smpp.connection.SmppConnectionLocator;
import com.adenon.api.smpp.core.threadpool.SmppThreadPool;
import com.adenon.api.smpp.logging.LogManager;
import com.adenon.api.smpp.sdk.ApiProperties;
import com.adenon.api.smpp.sdk.LogDescriptor;
import com.adenon.api.smpp.sdk.SmppLoggingManager;
import com.adenon.api.smpp.sdk.SmppMessagingManager;
import com.adenon.library.common.utils.tps.BlockingTpsCounter;

public class SmppApiDelegator {

    private final SmppConnectionGroupManager smppClientManager;
    private final LogManager                 logManager;
    private final SmppMessagingManager       messagingManager;
    private final SmppConnectionLocator      smppConnectionLocator;
    private final IOReactorStorage           smppIOReactorStorage;
    private final SmppIOReactorSelectorEx    smppIOReactorSelector;
    private final SmppThreadPool             smppThreadPool;
    private final SmppLoggingManager         smppLoggingManager;
    private final Object                     syncObject = new Object();
    private ApiProperties                    apiProperties;
    private final BlockingTpsCounter         blockingTpsCounter;

    public SmppApiDelegator(final String instanceName,
                            final LogDescriptor descriptor,
                            final ApiProperties apiProperties,
                            final int tpsCount) {
        this.apiProperties = apiProperties;
        this.logManager = new LogManager(instanceName, descriptor, this, this.syncObject);
        this.smppLoggingManager = new SmppLoggingManager(this.logManager);
        this.smppIOReactorStorage = new IOReactorStorage();
        this.smppClientManager = new SmppConnectionGroupManager(this.smppIOReactorStorage, this.getLogManager(), this.syncObject, this);
        this.smppConnectionLocator = new SmppConnectionLocator(this.smppClientManager, this.syncObject);
        this.messagingManager = new SmppMessagingManager(this.getConnectionGroupManager(), this.getLogManager(), this.getSmppConnectionLocator());
        int threadCount = 50;
        if (apiProperties != null) {
            threadCount = apiProperties.getThreadCount();
        }
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        this.smppThreadPool = new SmppThreadPool(executor);
        this.smppIOReactorSelector = new SmppIOReactorSelectorEx(this.smppIOReactorStorage, this.smppThreadPool, this.logManager.getLogger());
        this.smppIOReactorSelector.setDaemon(true);
        this.smppIOReactorSelector.start();
        this.blockingTpsCounter = new BlockingTpsCounter(tpsCount);

    }

    public SmppConnectionGroupManager getConnectionGroupManager() {
        return this.smppClientManager;
    }

    public SmppMessagingManager getMessagingManager() {
        return this.messagingManager;
    }

    public LogManager getLogManager() {
        return this.logManager;
    }

    public SmppConnectionLocator getSmppConnectionLocator() {
        return this.smppConnectionLocator;
    }


    public ApiProperties getApiProperties() {
        return this.apiProperties;
    }

    public void setApiProperties(final ApiProperties apiProperties) {
        this.apiProperties = apiProperties;
    }

    public BlockingTpsCounter getBlockingTpsCounter() {
        return this.blockingTpsCounter;
    }

    public SmppLoggingManager getSmppLoggingManager() {
        return this.smppLoggingManager;
    }

}
