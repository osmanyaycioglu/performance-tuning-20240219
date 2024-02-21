package com.adenon.api.smpp.sdk;

import com.adenon.api.smpp.common.CommonUtils;

public class ConnectionInfo {

    private String connectionGroupName;
    private String connectionName;

    public ConnectionInfo() {
    }

    public String getConnectionGroupName() {
        return this.connectionGroupName;
    }

    public ConnectionInfo setConnectionGroupName(final String clientName) {
        this.connectionGroupName = clientName;
        return this;
    }

    public String getConnectionName() {
        return this.connectionName;
    }

    public ConnectionInfo setConnectionName(final String hostName) {
        this.connectionName = hostName;
        return this;
    }

    public boolean checkConnectionInfoIsNull() {
        if (CommonUtils.checkStringIsEmpty(this.connectionGroupName)) {
            if (CommonUtils.checkStringIsEmpty(this.connectionName)) {
                return true;
            }
        }
        return false;
    }
}
