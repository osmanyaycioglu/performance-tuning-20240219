package com.adenon.api.smpp.core;

import com.adenon.api.smpp.core.threadpool.SmppThreadPool;
import com.adenon.api.smpp.logging.LoggerWrapper;


public class SmppIOReactorSelector extends Thread {

    private final LoggerWrapper    logger;
    private final IOReactorStorage smppIOReactorManager;
    private boolean                shutdown = false;
    private final SmppThreadPool   smppThreadPool;

    public SmppIOReactorSelector(final IOReactorStorage reactorManager,
                                 final SmppThreadPool threadPool,
                                 final LoggerWrapper loggerWrapper) {
        super("SmppIOReactorSelector");
        this.smppIOReactorManager = reactorManager;
        this.smppThreadPool = threadPool;
        this.logger = loggerWrapper;
    }

    @Override
    public void run() {
        // while (!this.shutdown) {
        // try {
        // for (int i = 0; i < this.smppIOReactorManager.size(); i++) {
        // try {
        // final SmppIOReactor smppIOReactor = this.smppIOReactorManager.get(i);
        // final int status = smppIOReactor.getIoReactorStatus().get();
        // if (status == SmppIOReactor.STATUS_READY_FOR_READING) {
        // if (smppIOReactor.getIoReactorStatus().compareAndSet(SmppIOReactor.STATUS_READY_FOR_READING,
        // SmppIOReactor.STATUS_READING_IN_PROGRES)) {
        // if (this.logger.isDebugEnabled()) {
        // this.logger.debug("SmppIOReactorSelector", "run", 0, smppIOReactor.getLabel(), "Ready for another thread.");
        // }
        // final SmmpIOReader ioReader = new SmmpIOReader(smppIOReactor);
        // this.smppThreadPool.execute(ioReader);
        // smppIOReactor.increaseThreadCount();
        // }
        // }
        // } catch (final Exception e) {
        // this.logger.error("SmppIOReactorSelector", "run", 0, null, " : Error : " + e.getMessage(), e);
        // }
        // }
        // try {
        // Thread.sleep(1);
        // } catch (final Exception e) {
        // }
        // } catch (final Exception e) {
        // this.logger.error("SmppIOReactorSelector", "run", 0, null, " : Error : " + e.getMessage(), e);
        // }
        // }
    }

    public boolean isShutdown() {
        return this.shutdown;
    }

    public void shutdown() {
        this.shutdown = true;
    }
}
