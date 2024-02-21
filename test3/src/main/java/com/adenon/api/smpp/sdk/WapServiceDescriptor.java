package com.adenon.api.smpp.sdk;


public class WapServiceDescriptor {

    private String      hrefUrl;
    private IActionType actionType;


    public WapServiceDescriptor() {
    }

    public String getHrefUrl() {
        return this.hrefUrl;
    }

    public WapServiceDescriptor setHrefUrl(final String hrefUrl) {
        this.hrefUrl = hrefUrl;
        return this;
    }

    public IActionType getActionType() {
        return this.actionType;
    }

    public WapServiceDescriptor setActionType(final IActionType actionType) {
        this.actionType = actionType;
        return this;
    }


}
