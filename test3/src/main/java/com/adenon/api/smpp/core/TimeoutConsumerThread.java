package com.adenon.api.smpp.core;

import java.util.Queue;

import com.adenon.api.smpp.common.Smpp34Constants;
import com.adenon.api.smpp.core.buffer.TimedoutObject;
import com.adenon.api.smpp.message.SubmitSMMessage;
import com.adenon.api.smpp.sdk.ESendResult;


public class TimeoutConsumerThread extends Thread {

    private final SmppIOReactor         smppIOReactor;
    private final Queue<TimedoutObject> timedoutObjects;

    public TimeoutConsumerThread(final SmppIOReactor smppIOReactor,
                                 final Queue<TimedoutObject> timedoutObjects) {
        this.smppIOReactor = smppIOReactor;
        this.timedoutObjects = timedoutObjects;
    }

    @Override
    public void run() {
        TimedoutObject timedoutObject;
        while ((timedoutObject = this.timedoutObjects.poll()) != null) {
            try {
                if (timedoutObject != null) {
                    if (timedoutObject.getWaitingObject() != null) {
                        if (timedoutObject.getWaitingObject().getMesssageType() == Smpp34Constants.MSG_SUBMIT_SM) {
                            final SubmitSMMessage submitSM = (SubmitSMMessage) timedoutObject.getWaitingObject();
                            submitSM.setSendResult(ESendResult.RETRY);
                            if (submitSM.getWaitObject() == null) {
                                this.smppIOReactor.getSmppCallback().submitResult(this.smppIOReactor.getConnectionInformation(),
                                                                                  submitSM,
                                                                                  submitSM.getAttachedObject());
                            } else {
                                synchronized (submitSM.getWaitObject()) {
                                    submitSM.getWaitObject().notify();
                                }
                            }
                        }
                    }
                }
            } catch (final Exception e) {
                this.smppIOReactor.getLogger().error("TimeoutConsumerThread", "run", 0, null, " : Error : " + e.getMessage(), e);
            }
        }
    }
}
