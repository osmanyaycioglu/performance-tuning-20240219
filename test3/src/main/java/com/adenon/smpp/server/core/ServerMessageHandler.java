package com.adenon.smpp.server.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.adenon.api.smpp.common.Smpp34Constants;
import com.adenon.api.smpp.core.ISmppMessageHandler;
import com.adenon.api.smpp.core.handler.SmppEnquireLinkHandler;
import com.adenon.api.smpp.core.handler.SmppEnquireLinkResponseHandler;
import com.adenon.api.smpp.core.handler.SmppHeaderHandler;
import com.adenon.api.smpp.core.handler.SmppNackHandler;
import com.adenon.api.smpp.message.MessageHeader;
import com.adenon.smpp.server.handlers.BindHandler;
import com.adenon.smpp.server.handlers.DeliverResponseHandler;
import com.adenon.smpp.server.handlers.SubmitSMHandler;
import com.adenon.smpp.server.handlers.SubmitSMHandler.SubmitHolder;


public class ServerMessageHandler implements ISmppMessageHandler {


    private final ServerIOReactor                ioReactor;
    private final SmppHeaderHandler              smppHeaderHandler              = new SmppHeaderHandler();
    private final SmppEnquireLinkHandler         smppEnquireLinkHandler         = new SmppEnquireLinkHandler();
    private final SmppEnquireLinkResponseHandler smppEnquireLinkResponseHandler = new SmppEnquireLinkResponseHandler();
    private final BindHandler                    bindHandler                    = new BindHandler();
    private final DeliverResponseHandler         deliverResponseHandler         = new DeliverResponseHandler();
    private final SubmitSMHandler                submitSMHandler                = new SubmitSMHandler();
    private final SmppNackHandler                smppNackHandler                = new SmppNackHandler();

    public ServerMessageHandler(final ServerIOReactor ioReactor) {
        this.ioReactor = ioReactor;
    }
    
    private List<SubmitHolder>  list = new ArrayList<>();

    @Override
    public void handleMsg(final ByteBuffer byteBuffer) throws Exception {
        this.ioReactor.getStatisticCollector().increaseTotalReceivedPackageCount();
        final MessageHeader header = this.smppHeaderHandler.handle(byteBuffer);
        if (this.ioReactor.getLogger().isDebugEnabled()) {
            this.ioReactor.getLogger().debug("ServerMessageHandler", "handleMsg", 0, this.ioReactor.getLabel(), "Received Message : " + header.toString());
        }
        switch (header.getCommandID()) {
            case Smpp34Constants.MSG_BIND_TRANSCVR:
            case Smpp34Constants.MSG_BIND_TRANSMITTER:
            case Smpp34Constants.MSG_BIND_RECEIVER:
                this.bindHandler.handle(header, byteBuffer, this.ioReactor);
                break;

            case Smpp34Constants.MSG_SUBMIT_SM:
                SubmitHolder handle = this.submitSMHandler.handle(header, byteBuffer, this.ioReactor);
                list.add(handle);
                break;

            case Smpp34Constants.MSG_SUBMIT_SM_RESP:
                break;

            case Smpp34Constants.MSG_GEN_NACK:
                this.smppNackHandler.handle(header, this.ioReactor);
                break;

            case Smpp34Constants.MSG_DELIVER_SM:
                break;

            case Smpp34Constants.MSG_DELIVER_SM_RESP:
                this.deliverResponseHandler.handle(header, byteBuffer, this.ioReactor);
                break;

            case Smpp34Constants.MSG_ENQUIRE_LINK_RESP:
                this.smppEnquireLinkResponseHandler.handle(header, this.ioReactor);
                break;

            case Smpp34Constants.MSG_ENQUIRE_LINK:
                this.smppEnquireLinkHandler.handle(header, this.ioReactor);
                break;

            default:
                break;
        }

    }
}
