package com.adenon.api.smpp.sdk;


public class WapPushSLMessageDescriptor extends WapServiceDescriptor implements IMessage {


    public WapPushSLMessageDescriptor(final SLActionType slActionType) {
        this.setActionType(slActionType);
    }


    @Override
    public EMessageType getMessageType() {
        return EMessageType.WAPPushSL;
    }

    public final static WapPushSLMessageDescriptor getDefaultWapPushSL(final String url) {
        final WapPushSLMessageDescriptor messageDescriptor = new WapPushSLMessageDescriptor(SLActionType.ExecuteLow);
        messageDescriptor.setHrefUrl(url);
        return messageDescriptor;
    }


    @Override
    public String getMsg() {
        return this.getHrefUrl();
    }


    @Override
    public EDataCoding getDataCoding() {
        return EDataCoding.WAP;
    }


}
