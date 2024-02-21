package com.adenon.api.smpp;

import java.util.Hashtable;
import java.util.Map;

import com.adenon.api.smpp.sdk.ApiProperties;
import com.adenon.api.smpp.sdk.LogDescriptor;

public class SmppApiEngine {

    public static Map<String, SmppApiEngine> instances = new Hashtable<String, SmppApiEngine>();

    private SmppApi                          smppApi;

    private final String                     engineName;

    private int                              tpsCount;

    private SmppApiEngine(final String engineName) {
        this.engineName = engineName.trim();
        SmppApiEngine.instances.put(this.engineName, this);
    }

    public static synchronized SmppApiEngine getSmppApiEngine(final String name,
                                                              final int tpsCount) {
        SmppApiEngine apiEngine = SmppApiEngine.instances.get(name);
        if (apiEngine == null) {
            apiEngine = new SmppApiEngine(name);
            apiEngine.tpsCount = tpsCount;
        }
        return apiEngine;
    }

    public SmppApi getSmppApi(final LogDescriptor logDescriptor) {
        if (this.smppApi == null) {
            this.smppApi = new SmppApi(this.engineName, logDescriptor, null, this.tpsCount);
        }
        return this.smppApi;
    }

    public SmppApi getSmppApi(final LogDescriptor logDescriptor,
                              final ApiProperties apiProperties) {
        if (this.smppApi == null) {
            this.smppApi = new SmppApi(this.engineName, logDescriptor, apiProperties, this.tpsCount);
        }
        return this.smppApi;
    }

    public void dispose() {
        SmppApiEngine.instances.remove(this.engineName);
    }

    public int getTpsCount() {
        return this.tpsCount;
    }


}
