package com.adenon.api.smpp.sdk;


public class WapPushBookmarkMessageDescriptor implements IMessage {

    private String name;
    private String url;

    public String getName() {
        return this.name;
    }


    public WapPushBookmarkMessageDescriptor setName(final String name) {
        this.name = name;
        return this;
    }


    public String getUrl() {
        return this.url;
    }


    public WapPushBookmarkMessageDescriptor setUrl(final String url) {
        this.url = url;
        return this;
    }


    @Override
    public EMessageType getMessageType() {
        return EMessageType.WAPBookmark;
    }

    public static WapPushBookmarkMessageDescriptor getDefaultWapPushBookmarkMessageDescriptor(final String name,
                                                                                              final String url) {
        return new WapPushBookmarkMessageDescriptor().setName(name).setUrl(url);
    }


    @Override
    public String getMsg() {
        return this.name + " : " + this.url;
    }


    @Override
    public EDataCoding getDataCoding() {
        return EDataCoding.WAP;
    }

}
