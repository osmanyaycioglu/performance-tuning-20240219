package com.adenon.api.smpp.sdk;


public class WapPushSIMessageDescriptor extends WapServiceDescriptor implements IMessage {

    private String serviceIndicatorId;
    private long   creationDate;
    private long   siExpiryDate;
    private String text;

    public WapPushSIMessageDescriptor(final SIActionType siActionType) {
        this.setActionType(siActionType);
    }

    public String getServiceIndicatorId() {
        return this.serviceIndicatorId;
    }

    public WapPushSIMessageDescriptor setServiceIndicatorId(final String serviceIndicatorId) {
        this.serviceIndicatorId = serviceIndicatorId;
        return this;
    }

    public long getCreationDate() {
        return this.creationDate;
    }

    public WapPushSIMessageDescriptor setCreationDate(final long creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public long getSiExpiryDate() {
        return this.siExpiryDate;
    }

    public WapPushSIMessageDescriptor setSiExpiryDate(final long siExpiryDate) {
        this.siExpiryDate = siExpiryDate;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public WapPushSIMessageDescriptor setText(final String text) {
        this.text = text;
        return this;
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.WAPPushSI;
    }

    public final static WapPushSIMessageDescriptor getDefaultWapPushSI(final String text,
                                                                       final String url) {
        final WapPushSIMessageDescriptor messageDescriptor = new WapPushSIMessageDescriptor(SIActionType.SignalMedium);
        messageDescriptor.setServiceIndicatorId("0001").setText(text).setHrefUrl(url);
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
