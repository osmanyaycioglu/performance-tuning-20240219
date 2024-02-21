package com.adenon.api.smpp.core;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.adenon.api.smpp.buffer.SendBufferObject;
import com.adenon.api.smpp.buffer.SmppBufferManager;

public class SmmpIOReader implements Runnable {


    private static final Logger logger = Logger.getLogger(SmmpIOReader.class);

    private final IIOReactor    ioReactor;

    public SmmpIOReader(final IIOReactor ioReactor) {
        this.ioReactor = ioReactor;
    }

    @Override
    public void run() {
        try {
            if (this.ioReactor.getLogger().isDebugEnabled()) {
                this.ioReactor.getLogger().debug("SmmpIOReader",
                                                 "run",
                                                 0,
                                                 null,
                                                 " -> Connection status : "
                                                         + this.ioReactor.getConnectionInformation().getConnectionState().toString()
                                                         + " connected : "
                                                         + this.ioReactor.getConnectionInformation().isConnected());
            }
            if (!this.ioReactor.getConnectionInformation().getConnectionState().isStopped() && this.ioReactor.getConnectionInformation().isConnected()) { // @changed
                final SendBufferObject nextBufferObject = SmppBufferManager.getNextBufferObject();
                if (nextBufferObject != null) {
                    try {
                        final ByteBuffer readSmppPackage = this.ioReactor.getSmppPackageReader().readSmppPackage(nextBufferObject.getByteBuffer(),
                                                                                                                 this.ioReactor);
                        if (readSmppPackage == null) {
                            this.ioReactor.getLogger().warn("SmmpIOReader",
                                                            "run",
                                                            0,
                                                            null,
                                                            "Buffer returned empty after reading socket. This may be due to a disconnection attempt.");
                            return;
                        }
                        this.ioReactor.getMessageHandler().handleMsg(readSmppPackage);
                    } catch (final Exception e) {
                        if (!this.ioReactor.getConnectionInformation().getConnectionState().isStopped()) {
                            this.ioReactor.getLogger().error("SmmpIOReader", "run", 0, this.ioReactor.getLabel(), " : Error : " + e.getMessage(), e);
                            this.ioReactor.closeConnection("Error : " + e.getMessage());
                        }
                    } finally {
                        SmppBufferManager.releaseBufferObject(nextBufferObject);
                    }
                } else {
                    this.ioReactor.getLogger().error("SmmpIOReader",
                                                     "run",
                                                     0,
                                                     this.ioReactor.getLabel(),
                                                     " : Error : Couldnt get a valid byte buffer for reading process.");
                }
            }
        } catch (final Exception e) {
            this.ioReactor.getLogger().error("SmmpIOReader", "run", 0, null, " : Error : " + e.getMessage(), e);
        } finally {
            this.ioReactor.decreaseThreadCount();
        }
    }
}
