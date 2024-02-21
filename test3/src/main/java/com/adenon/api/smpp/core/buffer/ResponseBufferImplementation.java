package com.adenon.api.smpp.core.buffer;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.adenon.api.smpp.logging.LoggerWrapper;


public class ResponseBufferImplementation {

    private final LoggerWrapper         logger;
    private final CounterWithLimit      bufferCounter;
    private int                         bufferSize         = 50;
    private int                         internalBufferSize = 0x3FF;
    private BufferBean[]                bufferBeans;
    private long                        itemExpireTimeout  = 2000;
    private final Queue<TimedoutObject> timeoutQueue;
    private final String                label;

    public ResponseBufferImplementation(final int bufferSize,
                                        final int arraySizeMultiplier,
                                        final LoggerWrapper logger,
                                        final long itemExpireTimeout,
                                        final String label) {
        this.logger = logger;
        this.setBufferSize(bufferSize);
        this.itemExpireTimeout = itemExpireTimeout;
        this.label = label;
        this.bufferCounter = new CounterWithLimit(this.getBufferSize());
        if (arraySizeMultiplier > 0) {
            this.setInternalBufferSize(this.calculateArraySize(arraySizeMultiplier));
        }
        this.setBufferBeans(new BufferBean[this.getInternalBufferSize() + 1]);
        int s;
        for (s = 0; s < (this.getInternalBufferSize() + 1); s++) {
            this.getBufferBeans()[s] = new BufferBean();
        }
        this.timeoutQueue = new ArrayBlockingQueue<TimedoutObject>(this.getBufferSize() * 2);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("ResponseBufferImplementation", "ResponseBufferImplementation", 0, this.label, " Buffer size : " + this.getInternalBufferSize());
        }
    }

    public BufferBean getFreeItem(final int _sequence) {
        final int index = _sequence & this.getInternalBufferSize();
        final int windowCounter = this.bufferCounter.increase();
        if (windowCounter < 0) {
            this.logger.warn("ResponseBufferImplementation",
                             "getFreeItem",
                             0,
                             this.label,
                             " : Buffer Full !!!! Sequence : " + _sequence + " Used : " + this.getUsedItemCount() + " Total : " + this.getBufferSize());
            return null;
        }
        final boolean isFree = this.getBufferBeans()[index].getStatus().compareAndSet(BufferBean.OBJECT_STATUS_WRITABLE, BufferBean.OBJECT_STATUS_IN_USE);
        if (isFree) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("ResponseBufferImplementation", "getFreeItem", 0, this.label, " Buffer item request has been taken . Seq : "
                                                                                                + _sequence
                                                                                                + " Position : "
                                                                                                + index
                                                                                                + " limit : "
                                                                                                + windowCounter);
            }
            return this.getBufferBeans()[index];
        } else {
            try {
                if ((System.currentTimeMillis() - this.getBufferBeans()[index].getUseDate()) > this.itemExpireTimeout) {
                    this.logger.error("ResponseBufferImplementation",
                                      "getFreeItem",
                                      0,
                                      this.label,
                                      " This object shouldnt be on the queue. Freeing the slot. Position : " + index + this.getBufferBeans()[index].toString());
                    if (this.getBufferBeans()[index].getWaitingObject() != null) {
                        final TimedoutObject timedoutObject = new TimedoutObject();
                        timedoutObject.setWaitingObject(this.getBufferBeans()[index].getWaitingObject());
                        timedoutObject.setSequence(this.getBufferBeans()[index].getSequenceNumber());
                        this.getTimeoutQueue().add(timedoutObject);
                    }
                    this.getBufferBeans()[index].release();
                    return this.getBufferBeans()[index];
                } else {
                    this.logger.error("ResponseBufferImplementation",
                                      "getFreeItem",
                                      0,
                                      this.label,
                                      " This object still in use. . Position : " + index + this.getBufferBeans()[index].toString());
                    return null;
                }
            } catch (final Exception e) {
                this.logger.error("ResponseBufferImplementation", "getFreeItem", 0, this.label, " : Error : " + e.getMessage(), e);
                return null;
            } finally {
                this.bufferCounter.decrease();
            }
        }
    }

    public BufferBean[] getFreeItems(final int[] sequence_no) {
        final BufferBean[] smpp34WaitObjects = new BufferBean[sequence_no.length];
        for (int i = 0; i < sequence_no.length; i++) {
            smpp34WaitObjects[i] = this.getFreeItem(sequence_no[i]);
            if (smpp34WaitObjects[i] == null) {
                for (int j = 0; j < smpp34WaitObjects.length; j++) {
                    if (smpp34WaitObjects[j] != null) {
                        smpp34WaitObjects[j].release();
                        this.bufferCounter.decrease();
                    }
                }
                return null;
            }
        }
        return smpp34WaitObjects;
    }

    public void releaseItems(final BufferBean[] smpp34WaitObjects) {
        for (int i = 0; i < smpp34WaitObjects.length; i++) {
            if (smpp34WaitObjects[i] != null) {
                smpp34WaitObjects[i].release();
                this.bufferCounter.decrease();
            }
        }
    }

    public BufferBean findItem(final int sequenceNumber) {
        try {
            final int index = sequenceNumber & this.getInternalBufferSize();
            final BufferBean retObj = this.getBufferBeans()[index];
            if (retObj.getSequenceNumber() == sequenceNumber) {
                final int windowCounter = this.bufferCounter.decrease();
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("ResponseBufferImplementation", "findItem", 0, this.label, " Sequence : "
                                                                                                 + sequenceNumber
                                                                                                 + " Position : "
                                                                                                 + index
                                                                                                 + " Limit : "
                                                                                                 + windowCounter
                                                                                                 + " Delta : "
                                                                                                 + (System.currentTimeMillis() - retObj.getUseDate()));
                }
                return retObj;
            } else {
                this.logger.error("ResponseBufferImplementation", "findItem", 0, this.label, " : Error : Requested sequence is not in the buffer. Sequence : "
                                                                                             + sequenceNumber);
                return null;
            }
        } catch (final Exception ex) {
            this.logger.error("ResponseBufferImplementation", "findItem", 0, null, " : Error : " + ex.getMessage(), ex);
            return null;
        }
    }

    public int getFreeItemCount() {
        return this.getBufferSize() - this.bufferCounter.getCounter();
    }

    public int getUsedItemCount() {
        return this.bufferCounter.getCounter();
    }

    public void resetBuffer() {
        int s;
        for (s = 0; s < (this.getInternalBufferSize() + 1); s++) {
            try {
                this.getBufferBeans()[s].getStatus().set(BufferBean.OBJECT_STATUS_WRITABLE);
                this.getBufferBeans()[s].setUseDate(0);
                this.getBufferBeans()[s].setWaitingObject(null);
                this.getBufferBeans()[s].setSequenceNumber(0);
            } catch (final Exception e) {
                this.logger.error("ResponseBufferImplementation", "resetBuffer", 0, null, " : Error : " + e.getMessage(), e);
            }
        }
        this.bufferCounter.reset();
    }

    public int checkExpiredItems() {
        int expCount = 0;
        final long _time_now = System.currentTimeMillis();
        int s;
        for (s = 0; s < (this.getInternalBufferSize() + 1); s++) {
            if ((this.getBufferBeans()[s].getStatus().get() == BufferBean.OBJECT_STATUS_READABLE)
                && ((_time_now - this.getBufferBeans()[s].getUseDate()) > this.itemExpireTimeout)) {
                expCount++;
                if (this.getBufferBeans()[s].getWaitingObject() != null) {
                    final TimedoutObject timedoutObject = new TimedoutObject();
                    timedoutObject.setWaitingObject(this.getBufferBeans()[s].getWaitingObject());
                    timedoutObject.setSequence(this.getBufferBeans()[s].getSequenceNumber());
                    this.logger.warn("ResponseBufferImplementation",
                                     "checkExpiredItems",
                                     0,
                                     this.label,
                                     " Expired Object. Sequence : "
                                             + timedoutObject.getSequence()
                                             + " Object Info : "
                                             + timedoutObject.getWaitingObject().toString());
                    this.getTimeoutQueue().add(timedoutObject);
                }
                this.getBufferBeans()[s].release();
            }
        }
        return expCount;
    }

    public int getExpiredItemsCount(final long timeout) {
        int expCount = 0;
        final long _time_now = System.currentTimeMillis();
        int s;
        for (s = 0; s < (this.getInternalBufferSize() + 1); s++) {
            if ((this.getBufferBeans()[s].getStatus().get() == BufferBean.OBJECT_STATUS_READABLE)
                && ((_time_now - this.getBufferBeans()[s].getUseDate()) > timeout)) {
                expCount++;
            }
        }
        return expCount;
    }

    public int getUsedItemsCountFromBuffer() {
        int expCount = 0;
        int s;
        for (s = 0; s < (this.getInternalBufferSize() + 1); s++) {
            if (this.getBufferBeans()[s].getStatus().get() == BufferBean.OBJECT_STATUS_READABLE) {
                expCount++;
            }
        }
        return expCount;
    }

    @Override
    public String toString() {
        return " Size : "
               + this.getBufferBeans().length
               + " Window Size "
               + this.getBufferSize()
               + " Used : "
               + this.bufferCounter.getCounter()
               + " Free : "
               + (this.getBufferSize() - this.bufferCounter.getCounter());
    }

    public int calculateArraySize(final int size) {
        final int rounder = 0x0F;
        int val = 0x3FF;
        for (int i = 0; i < size; i++) {
            val = val << 1;
            val |= rounder;
        }
        return val;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void setBufferSize(final int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getInternalBufferSize() {
        return this.internalBufferSize;
    }

    public void setInternalBufferSize(final int internalBufferSize) {
        this.internalBufferSize = internalBufferSize;
    }

    public BufferBean[] getBufferBeans() {
        return this.bufferBeans;
    }

    public void setBufferBeans(final BufferBean[] bufferEntities) {
        this.bufferBeans = bufferEntities;
    }

    public Queue<TimedoutObject> getTimeoutQueue() {
        return this.timeoutQueue;
    }

}
