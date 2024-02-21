package com.adenon.api.smpp.core;

import com.adenon.api.smpp.common.SmppApiException;


public class EnquireLinkThread extends Thread {

    private final IIOReactor           smppIOReactor;
    private final ConnectionController aliveThread;

    public EnquireLinkThread(final IIOReactor smppIOReactor,
                             final ConnectionController _aliveThread) {
        this.smppIOReactor = smppIOReactor;
        this.aliveThread = _aliveThread;
    }

    @Override
    public void run() {
        try {
            if (this.smppIOReactor.getLogger().isDebugEnabled()) {
                this.smppIOReactor.getLogger().debug("SendAliveThreadWriter", "run", 0, this.smppIOReactor.getLabel(), "Sending enquire link msg.");
            }
            this.aliveThread.setEnquireLinkTime(System.currentTimeMillis());
            this.aliveThread.setEnquireLinkSequenceNumber(this.smppIOReactor.getSequenceNumber());
            this.smppIOReactor.sendAlive(this.aliveThread.getEnquireLinkSequenceNumber());
        } catch (final SmppApiException e) {
            this.smppIOReactor.getLogger().error("SendAliveThreadWriter", "run", 0, this.smppIOReactor.getLabel(), "Error on sending enquire link message.", e);
        } catch (final Exception exp) {
            this.smppIOReactor.closeConnection(exp.getMessage());
        }
    }
}
