package com.adenon.api.smpp.core;

import java.text.MessageFormat;

public class ConnectionController extends Thread {

    private final IIOReactor smppReactor;
    private int              enquireLinkSequenceNumber = -1;
    private long             enquireLinkTime           = 0;
    private boolean          shutdownController        = false;
    private long             bindStart                 = 0;

    public ConnectionController(final IIOReactor ioReactor) {
        super("ConCtrl\\" + ioReactor.getLabel());
        this.smppReactor = ioReactor;
    }

    public void shutdown() {
        this.shutdownController = true;
    }

    @Override
    public void run() {
        while (!this.shutdownController) {
            if (this.smppReactor.getConnectionInformation().getConnectionState().isStopped()) {
                this.bindStart = 0;
                this.smppReactor.getLogger().info("SmppIOReactor", "run", 0, this.smppReactor.getLabel(), "Checking connection sanity.");
                this.shutdownController = this.smppReactor.handleCloseConnection();
            } else {
                if (!this.smppReactor.isBinded()) {
                    if (this.bindStart == 0) {
                        this.bindStart = System.currentTimeMillis();
                    }
                    if ((System.currentTimeMillis() - this.bindStart) > 5000) {
                        this.smppReactor.closeConnection("Bind expired. ");
                    }

                    try {
                        Thread.sleep(100);
                    } catch (final InterruptedException exp) {
                        this.smppReactor.getLogger().error("ConnectionController",
                                                           "run",
                                                           0,
                                                           this.smppReactor.getLabel(),
                                                           " : Connection Control Thread Interrupted : " + exp.getMessage(),
                                                           exp);

                        this.shutdownController = true;
                    } catch (final Exception e) {
                        this.smppReactor.getLogger().error("ConnectionController", "run", 0, this.smppReactor.getLabel(), " : Error : " + e.getMessage(), e);
                    }
                    continue;
                }
                if ((System.currentTimeMillis() - this.smppReactor.getLastWriteTime()) > 60000) {
                    this.smppReactor.closeConnection("There is no activity during 60 seconds. Closing connection.");
                } else {
                    if (this.smppReactor.getLogger().isInfoEnabled()) {
                        this.smppReactor.getLogger().info("SmppIOReactor", "run", 0, this.smppReactor.getLabel(), "Connection is healty");
                    }
                }
                try {
                    if (this.smppReactor.getLogger().isInfoEnabled()) {
                        this.smppReactor.getLogger().info("ConnectionController",
                                                          "run",
                                                          0,
                                                          this.smppReactor.getLabel(),
                                                          "IO Reader Thread Count : "
                                                                  + this.smppReactor.getThreadCount()
                                                                  + " Smpp window information : "
                                                                  + this.smppReactor.getResponseBuffer().toString());
                    }
                    if (this.smppReactor.getLogger().isInfoEnabled()) {
                        this.smppReactor.getLogger().info("ConnectionController",
                                                          "run",
                                                          0,
                                                          this.smppReactor.getLabel(),
                                                          "Total : " + this.smppReactor.getStatisticCollector().toString());
                    }
                    if (this.smppReactor.getLogger().isInfoEnabled()) {
                        this.smppReactor.getLogger().info("ConnectionController",
                                                          "run",
                                                          0,
                                                          this.smppReactor.getLabel(),
                                                          "Last 1000ms : " + this.smppReactor.getStatisticCollector().lastValues());
                    }

                    if (!this.smppReactor.isWriteEnd() && ((System.currentTimeMillis() - this.smppReactor.getStartWriteTime()) > 3000)) {
                        this.smppReactor.getLogger().error("ConnectionController",
                                                           "run",
                                                           0,
                                                           this.smppReactor.getLabel(),
                                                           " : Error : Writing didnt finish for 3000 ms. Connection is blocked.");
                        this.smppReactor.closeConnection("Blocked connection.");
                        continue;
                    }
                    if (this.smppReactor.getResponseBuffer().getExpiredItemsCount(3000) > (this.smppReactor.getResponseBuffer().getBufferSize() - (this.smppReactor.getResponseBuffer()
                                                                                                                                                                   .getBufferSize() / 10))) {
                        this.smppReactor.closeConnection("Window has expired");
                        continue;
                    }
                    if (this.getEnquireLinkSequenceNumber() > 0) {
                        if ((System.currentTimeMillis() - this.getEnquireLinkTime()) > 3000) {
                            this.smppReactor.closeConnection("send alive response late!!!");
                            this.setEnquireLinkSequenceNumber(-1);
                            continue;
                        }
                    }
                    this.smppReactor.getResponseBuffer().checkExpiredItems();
                    if (this.smppReactor.getResponseBuffer().getTimeoutQueue().size() > 0) {
                        if (this.smppReactor.getResponseBuffer().getTimeoutQueue().size() > (this.smppReactor.getResponseBuffer().getBufferSize() / 2)) {
                            this.smppReactor.closeConnection("Responses expired.");
                        }
                        this.smppReactor.handleTimeoutRequests();
                    }
                    if (this.smppReactor.getThrottleEndTime() > 0) {
                        if (System.currentTimeMillis() > this.smppReactor.getThrottleEndTime()) {
                            final int tps = this.smppReactor.getConnectionInformation().getTps();
                            this.smppReactor.adjustTps(tps);
                            this.smppReactor.setThrottleStartTime(0);
                            this.smppReactor.setThrottleEndTime(0);

                            this.smppReactor.getLogger().warn("ConnectionController",
                                                              "run",
                                                              0,
                                                              " : WARN : ",
                                                              MessageFormat.format("Throttle period ended >> Conn: {0}, new-tps: {1}",
                                                                                   this.smppReactor.getConnectionInformation().getConnectionName(),
                                                                                   tps));
                        }
                    }
                    if (this.smppReactor.getConnectionInformation().getConnectionState().isSuspended() && this.smppReactor.isBinded()) {
                        // Check if suspended connection has a end time. This means this connection suspended because of QUEUE FULL MESSAGE
                        if (this.smppReactor.getSuspendEndTime() > 0) {
                            if (System.currentTimeMillis() > this.smppReactor.getSuspendEndTime()) {
                                this.smppReactor.getConnectionInformation().getConnectionState().idle();
                                this.smppReactor.setSuspendEndTime(0);
                            } else { // @changed
                                if ((System.currentTimeMillis() - this.smppReactor.getLastWriteTime()) > 20000) {
                                    if (this.getEnquireLinkSequenceNumber() < 1) {
                                        if (this.smppReactor.getLogger().isDebugEnabled()) {
                                            this.smppReactor.getLogger().debug("ConnectionController",
                                                                               "run",
                                                                               0,
                                                                               this.smppReactor.getLabel(),
                                                                               "Initiating send msg enquire link thread");
                                        }
                                        new EnquireLinkThread(this.smppReactor, this).start();
                                    }
                                }
                            }
                        } else {
                            final int freeItemsCount = this.smppReactor.getResponseBuffer().getFreeItemCount();
                            if (freeItemsCount != this.smppReactor.getResponseBuffer().getBufferSize()) {
                                if (freeItemsCount > (this.smppReactor.getResponseBuffer().getBufferSize() / 2)) {
                                    if (this.smppReactor.getLogger().isInfoEnabled()) {
                                        this.smppReactor.getLogger().info("ConnectionController",
                                                                          "run",
                                                                          0,
                                                                          this.smppReactor.getLabel(),
                                                                          "Buffer is healty . Restarting execution.");
                                    }
                                    this.smppReactor.getConnectionInformation().getConnectionState().idle();
                                } else {
                                    if ((System.currentTimeMillis() - this.smppReactor.getSuspendStartTime()) > 2000) {
                                        this.smppReactor.closeConnection("Suspend too long.");
                                    }
                                }
                            }
                        }
                    } else {
                        if (this.smppReactor.getLogger().isDebugEnabled()) {
                            this.smppReactor.getLogger().debug("ConnectionController",
                                                               "run",
                                                               0,
                                                               this.smppReactor.getLabel(),
                                                               "Last Write made : "
                                                                       + (System.currentTimeMillis() - this.smppReactor.getLastWriteTime())
                                                                       + " Last Read Made : "
                                                                       + (System.currentTimeMillis() - this.smppReactor.getLastReadTime()));
                        }
                        if ((System.currentTimeMillis() - this.smppReactor.getLastWriteTime()) > 20000) {
                            if (this.getEnquireLinkSequenceNumber() < 1) {
                                if (this.smppReactor.getLogger().isDebugEnabled()) {
                                    this.smppReactor.getLogger().debug("ConnectionController",
                                                                       "run",
                                                                       0,
                                                                       this.smppReactor.getLabel(),
                                                                       "Initiating send msg enquire link thread");
                                }
                                new EnquireLinkThread(this.smppReactor, this).start();
                            }
                        }
                    }
                } catch (final Exception exp) {
                    this.smppReactor.getLogger().error("ConnectionController", "run", 0, this.smppReactor.getLabel(), " : Error : " + exp.getMessage(), exp);
                    this.smppReactor.closeConnection(exp.getMessage());
                }
            }
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException exp) {
                this.smppReactor.getLogger().error("ConnectionController",
                                                   "run",
                                                   0,
                                                   this.smppReactor.getLabel(),
                                                   " : Connection Control Thread Interrupted : " + exp.getMessage(),
                                                   exp);

                this.shutdownController = true;
            } catch (final Exception e) {
                this.smppReactor.getLogger().error("ConnectionController", "run", 0, this.smppReactor.getLabel(), " : Error : " + e.getMessage(), e);
            }
        }
        if (this.smppReactor.getLogger().isInfoEnabled()) {
            this.smppReactor.getLogger().info("ConnectionController", "run", 0, this.smppReactor.getLabel(), "Exiting from connection control thread!!!!");
        }
    }

    public void aliveResponse(final int seq) {
        if (this.getEnquireLinkSequenceNumber() == seq) {
            if (this.smppReactor.getLogger().isDebugEnabled()) {
                this.smppReactor.getLogger().debug("ConnectionController", "aliveResponse", 0, this.smppReactor.getLabel(), "Connection is healty..");
            }
            this.setEnquireLinkSequenceNumber(-1);
        }
    }

    public long getEnquireLinkTime() {
        return this.enquireLinkTime;
    }

    public void setEnquireLinkTime(final long enquireLinkTime) {
        this.enquireLinkTime = enquireLinkTime;
    }

    public int getEnquireLinkSequenceNumber() {
        return this.enquireLinkSequenceNumber;
    }

    public void setEnquireLinkSequenceNumber(final int enquireLinkSequenceNumber) {
        this.enquireLinkSequenceNumber = enquireLinkSequenceNumber;
    }
}
