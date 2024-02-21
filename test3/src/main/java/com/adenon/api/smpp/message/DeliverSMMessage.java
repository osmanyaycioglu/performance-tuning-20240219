package com.adenon.api.smpp.message;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.Smpp34Constants;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.messaging.processor.DeliveryProcessor;
import com.adenon.api.smpp.messaging.processor.IMessageProcessor;
import com.adenon.api.smpp.messaging.processor.TextSmsProcessor;
import com.adenon.api.smpp.sdk.ESendResult;
import com.adenon.api.smpp.sdk.IMessage;
import com.adenon.api.smpp.sdk.TextMessageDescriptor;


public class DeliverSMMessage extends OptionalParameters implements MessageObject {

    private final LoggerWrapper logger;

    private long                transactionId;

    private final String        label;

    private IMessageProcessor   messageProcessor;

    private Object              attachedObject;

    private Object              waitObject;

    private IMessage            message;

    private ESendResult         sendResult;

    private boolean             delivery;


    public DeliverSMMessage(final LoggerWrapper logger,
                            final long transactionId,
                            final String pLabel) {
        super(logger, Smpp34Constants.MSG_DELIVER_SM);
        this.transactionId = transactionId;
        this.logger = logger;
        this.label = pLabel;
    }

    public void parseMessage(final ByteBuffer byteBuffer) throws Exception {
        super.parseMandatoryParameters(byteBuffer, 0, this.label);

        super.parseOpitionalParameters(byteBuffer, 0, this.label);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("DeliverSMMessage", "parseMessage", 0, this.label, "Deliver SM Received : " + this.toString());
        }

    }

    public void fillBuffer(final ByteBuffer byteBuffer,
                           final int sequenceNumber,
                           final int messageIndex) throws Exception {
        super.fillMandatoryParameters(byteBuffer, sequenceNumber);
        this.messageProcessor.fillMessageBody(byteBuffer, messageIndex, null);
        this.fillOptionalParameters(byteBuffer);
        CommonUtils.setLength(byteBuffer);
    }

    public void init(final Object attachedObject) throws Exception {
        this.setAttachedObject(attachedObject);
        if (this.delivery == true) {
            this.messageProcessor = new DeliveryProcessor();
        } else {
            final TextMessageDescriptor messageDescriptor = (TextMessageDescriptor) this.message;
            this.setParamDataCoding(messageDescriptor.getDataCoding().getValue());
            this.setParamShortMessage(messageDescriptor.getMessage());
            this.messageProcessor = new TextSmsProcessor(this.getParamShortMessage(),
                                                         messageDescriptor.getDataCoding(),
                                                         this.logger,
                                                         this.transactionId,
                                                         this.label);
        }

    }

    @Override
    public String getDescription() {
        return "DELIVER_SM";
    }

    @Override
    public int getMesssageType() {
        return Smpp34Constants.MSG_DELIVER_SM;
    }

    @Override
    public String toString() {
        return " *DELIVER SM* " + super.toString();
    }

    public String getMessageIdentifier() {
        return this.getOpParamMessageId();
    }


    public IMessageProcessor getMessageProcessor() {
        return this.messageProcessor;
    }


    public void setMessageProcessor(final IMessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }


    public Object getAttachedObject() {
        return this.attachedObject;
    }


    public void setAttachedObject(final Object attachedObject) {
        this.attachedObject = attachedObject;
    }


    public Object getWaitObject() {
        return this.waitObject;
    }


    public void setWaitObject(final Object waitObject) {
        this.waitObject = waitObject;
    }


    public IMessage getMessage() {
        return this.message;
    }


    public void setMessage(final IMessage message) {
        this.message = message;
    }


    public ESendResult getSendResult() {
        return this.sendResult;
    }


    public void setSendResult(final ESendResult sendResult) {
        this.sendResult = sendResult;
    }


    public LoggerWrapper getLogger() {
        return this.logger;
    }


    public String getLabel() {
        return this.label;
    }

    public long getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(final long transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isDelivery() {
        return this.delivery;
    }

    public void setDelivery(final boolean delivery) {
        this.delivery = delivery;
    }


}
