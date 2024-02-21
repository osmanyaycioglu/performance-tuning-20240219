package com.adenon.api.smpp.core.handler;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.Smpp34Constants;
import com.adenon.api.smpp.core.ISmppMessageHandler;
import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.message.MessageHeader;


public class SmppMessageHandler implements ISmppMessageHandler {

    private final SmppIOReactor                    smppIOReactor;
    private final SmppHeaderHandler                smppHeaderHandler              = new SmppHeaderHandler();
    private final SmppBindHandler                  smppBindHandler                = new SmppBindHandler();
    private final SmppDeliverSMHandler             smppDeliverSMHandler           = new SmppDeliverSMHandler();
    private final SmppNackHandler                  smppNackHandler                = new SmppNackHandler();
    private final SmppSubmitSMResponseHandler      submitSMResponseHandler        = new SmppSubmitSMResponseHandler();
    private final SmppEnquireLinkHandler           smppEnquireLinkHandler         = new SmppEnquireLinkHandler();
    private final SmppEnquireLinkResponseHandler   smppEnquireLinkResponseHandler = new SmppEnquireLinkResponseHandler();
    private final SmppSubmitSMHandler              submitSMHandler                = new SmppSubmitSMHandler();
    private final SmppSubmitMultiSMResponseHandler submitMultiSMResponseHandler   = new SmppSubmitMultiSMResponseHandler();
    private final SmppCancelSMResponseHandler      smppCancelSMResponseHandler    = new SmppCancelSMResponseHandler();
    private final SmppQuerySMResponseHandler       smppQuerySMResponseHandler     = new SmppQuerySMResponseHandler();
    private final SmppUnbindResponseHandler        smppUnbindResponseHandler      = new SmppUnbindResponseHandler();
    private final SmppUnbindHandler                smppUnbindHandler              = new SmppUnbindHandler();

    public SmppMessageHandler(final SmppIOReactor ioReactor) {
        this.smppIOReactor = ioReactor;
    }

    @Override
    public void handleMsg(final ByteBuffer byteBuffer) throws Exception {
        this.smppIOReactor.getStatisticCollector().increaseTotalReceivedPackageCount();
        final MessageHeader header = this.smppHeaderHandler.handle(byteBuffer);
        if (this.smppIOReactor.getLogger().isDebugEnabled()) {
            this.smppIOReactor.getLogger()
                              .debug("SmppMessageHandler", "handleMsg", 0, this.smppIOReactor.getLabel(), "Received Message : " + header.toString());
        }
        switch (header.getCommandID()) {
            case Smpp34Constants.MSG_BIND_TRANSCVR_RESP:
            case Smpp34Constants.MSG_BIND_TRANSMITTER_RESP:
            case Smpp34Constants.MSG_BIND_RECEIVER_RESP:
                this.smppBindHandler.handle(header, this.smppIOReactor);
                return;

            case Smpp34Constants.MSG_SUBMIT_SM_RESP:
                this.submitSMResponseHandler.handle(header, this.smppIOReactor, byteBuffer);
                break;

            case Smpp34Constants.MSG_GEN_NACK:
                this.smppNackHandler.handle(header, this.smppIOReactor);
                break;

            case Smpp34Constants.MSG_CANCEL_SM_RESP:
                this.smppCancelSMResponseHandler.handle(header, this.smppIOReactor, byteBuffer);
                break;

            case Smpp34Constants.MSG_QUERY_SM_RESP:
                this.smppQuerySMResponseHandler.handle(header, this.smppIOReactor, byteBuffer);
                break;

            case Smpp34Constants.MSG_ENQUIRE_LINK_RESP:
                this.smppEnquireLinkResponseHandler.handle(header, this.smppIOReactor);
                break;

            case Smpp34Constants.MSG_DELIVER_SM:
                this.smppDeliverSMHandler.handle(header, this.smppIOReactor, byteBuffer);
                break;

            case Smpp34Constants.MSG_SUBMIT_SM:
                this.submitSMHandler.handle(header, this.smppIOReactor, byteBuffer);
                break;

            case Smpp34Constants.MSG_SUBMIT_MULTI_SM:
                this.submitSMHandler.handle(header, this.smppIOReactor, byteBuffer);
                break;

            case Smpp34Constants.MSG_SUBMIT_MULTI_SM_RESP:
                this.submitMultiSMResponseHandler.handle(header, this.smppIOReactor, byteBuffer);
                break;

            case Smpp34Constants.MSG_ENQUIRE_LINK:
                this.smppEnquireLinkHandler.handle(header, this.smppIOReactor);
                break;

            case Smpp34Constants.MSG_UNBIND_RESP:
                this.smppUnbindResponseHandler.handle(header, this.smppIOReactor);

            case Smpp34Constants.MSG_UNBIND:
                this.smppUnbindHandler.handle(header, this.smppIOReactor);

            default:
                break;
        }
    }
}
