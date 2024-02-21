package com.adenon.api.smpp;

import com.adenon.api.smpp.core.SmppApiDelegator;
import com.adenon.api.smpp.sdk.ApiProperties;
import com.adenon.api.smpp.sdk.LogDescriptor;

public class SmppApi {

    private final SmppApiDelegator  apiDelegator;
    private final SmppConnectionApi smppConnectionApi;
    private final SmppMessagingApi  smppMessagingApi;
    private final SmppLoggerApi     smppLoggerApi;

    public SmppApi(final String engineName,
                   final LogDescriptor descriptor,
                   final ApiProperties apiProperties,
                   final int tpsCount) {
        this.apiDelegator = new SmppApiDelegator(engineName, descriptor, apiProperties, tpsCount);
        this.smppConnectionApi = new SmppConnectionApi(this.apiDelegator);
        this.smppMessagingApi = new SmppMessagingApi(this.apiDelegator);
        this.apiDelegator.setApiProperties(apiProperties);
        this.smppLoggerApi = new SmppLoggerApi(this.apiDelegator);

    }

    public SmppConnectionApi getSmppConnectionApi() {
        return this.smppConnectionApi;
    }

    public SmppMessagingApi getSmppMessagingApi() {
        return this.smppMessagingApi;
    }

    public SmppLoggerApi getSmppLoggerApi() {
        return this.smppLoggerApi;
    }

}
