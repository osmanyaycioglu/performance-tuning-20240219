package com.adenon.api.smpp.sdk;


public class TextMessageDescriptor implements IMessage {

    private String      message;
    private EDataCoding dataCoding = EDataCoding.GSM_DEFAULT;


    public TextMessageDescriptor() {
    }

    public String getMessage() {
        return this.message;
    }

    public TextMessageDescriptor setMessage(final String message) {
        this.message = message;
        return this;
    }

    @Override
    public EDataCoding getDataCoding() {
        return this.dataCoding;
    }

    public TextMessageDescriptor setDataCoding(final EDataCoding dataCoding) {
        this.dataCoding = dataCoding;
        return this;
    }

    public static TextMessageDescriptor getASCIIMessageDescriptor(final String message) {
        return new TextMessageDescriptor().setDataCoding(EDataCoding.ASCII).setMessage(message);
    }

    public static TextMessageDescriptor getUnicodeMessageDescriptor(final String message) {
        return new TextMessageDescriptor().setDataCoding(EDataCoding.UCS2).setMessage(message);
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.SMSText;
    }

    @Override
    public String getMsg() {
        return this.message;
    }
}
