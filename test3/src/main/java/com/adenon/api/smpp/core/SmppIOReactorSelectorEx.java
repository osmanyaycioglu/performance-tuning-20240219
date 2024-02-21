package com.adenon.api.smpp.core;

import com.adenon.api.smpp.core.threadpool.SmppThreadPool;
import com.adenon.api.smpp.logging.LoggerWrapper;


public class SmppIOReactorSelectorEx extends Thread {

    private final LoggerWrapper    logger;
    private final IOReactorStorage smppIOReactorManager;
    private boolean                shutdown = false;
    private final SmppThreadPool   smppThreadPool;

    public SmppIOReactorSelectorEx(final IOReactorStorage reactorManager,
                                   final SmppThreadPool threadPool,
                                   final LoggerWrapper loggerWrapper) {
        super("IOReactorSelector");
        this.smppIOReactorManager = reactorManager;
        this.smppThreadPool = threadPool;
        this.logger = loggerWrapper;
    }

    @Override
    public void run() {
        boolean found = false;
        while (!this.shutdown) {
            try {
                if (this.smppIOReactorManager.size() > 0) {
                    for (int i = 0; i < this.smppIOReactorManager.size(); i++) {
                        try {
                            final IIOReactor smppIOReactor = this.smppIOReactorManager.get(i);
                            if (!smppIOReactor.getConnectionInformation().getConnectionState().isStopped()) {
                                final int threadCount = smppIOReactor.getThreadCount();
                                if (threadCount < smppIOReactor.getMaxThreadCount()) {
                                    if (smppIOReactor.getLogger().isDebugEnabled()) {
                                        smppIOReactor.getLogger().debug("SmppIOReactorSelectorEx",
                                                                        "run",
                                                                        0,
                                                                        smppIOReactor.getLabel(),
                                                                        "Firing new rocket for : "
                                                                                + smppIOReactor.getLabel()
                                                                                + " thread count : "
                                                                                + threadCount
                                                                                + " max : "
                                                                                + smppIOReactor.getMaxThreadCount());
                                    }
                                    for (int j = 0; j < (smppIOReactor.getMaxThreadCount() - threadCount); j++) {
                                        final SmmpIOReader ioReader = new SmmpIOReader(smppIOReactor);
                                        this.smppThreadPool.execute(ioReader);
                                        smppIOReactor.increaseThreadCount();
                                        found = true;
                                    }
                                }
                            }
                        } catch (final Exception e) {
                            this.logger.error("SmppIOReactorSelectorEx", "run", 0, null, " : Error : " + e.getMessage(), e);
                        }
                    }
                }
                if (found) {
                    try {
                        Thread.yield();
                    } catch (final Exception e) {
                    }
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (final Exception e) {
                    }
                }
                found = false;
            } catch (final Exception e) {
                this.logger.error("SmppIOReactorSelectorEx", "run", 0, null, " : Error : " + e.getMessage(), e);
            }
        }
    }

    public boolean isShutdown() {
        return this.shutdown;
    }

    public void shutdown() {
        this.shutdown = true;
    }
}
