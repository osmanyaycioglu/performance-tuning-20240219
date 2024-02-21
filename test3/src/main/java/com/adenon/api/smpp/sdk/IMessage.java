package com.adenon.api.smpp.sdk;


public interface IMessage {

    public EMessageType getMessageType();

    public String getMsg();

    public EDataCoding getDataCoding();

}
