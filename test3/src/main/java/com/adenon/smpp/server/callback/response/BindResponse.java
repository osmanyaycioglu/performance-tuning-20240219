package com.adenon.smpp.server.callback.response;


public class BindResponse {

    private final EBindResult bindResult;
    private final String      connectionName;

    public BindResponse(final EBindResult bindResult,
                        final String connectionName) {
        this.bindResult = bindResult;
        this.connectionName = connectionName;
    }

    public EBindResult getBindResult() {
        return this.bindResult;
    }


    public String getConnectionName() {
        return this.connectionName;
    }


}
