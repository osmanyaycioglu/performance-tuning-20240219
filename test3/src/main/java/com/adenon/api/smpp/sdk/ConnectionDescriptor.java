package com.adenon.api.smpp.sdk;

import java.util.ArrayList;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.library.common.utils.ETime;

public class ConnectionDescriptor implements Comparable<ConnectionDescriptor> {

    private ISmppCallback           callbackInterface;
    private String                  connectionGroupName = null;
    private String                  connectionName      = null;
    private SmppConnectionType      connectionType      = SmppConnectionType.BOTH;
    private final ArrayList<String> ipList              = new ArrayList<String>();
    private int                     maxThreadCount      = 5;
    private String                  password            = null;
    private int                     port                = 0;
    private int                     smppWindowSize      = 100;
    private boolean                 trace               = false;
    private String                  username            = null;
    private int                     tps                 = -1;

    // reduce TPS by %20 in case of THROTTLING for 60 minutes.
    private long                    throttlePeriod      = ETime.MINUTE.getMiliseconds() * 60;
    private int                     reduceTpsBy         = 20;

    private long                    suspendPeriod       = ETime.MINUTE.getMiliseconds() * 2;
    private int                     connectionTimeout   = 15000;

    public ConnectionDescriptor(final String connectionGroupName,
                                final String connectionName) {
        this.connectionGroupName = connectionGroupName;
        this.connectionName = connectionName;
    }

    public ConnectionDescriptor addIp(final String ip) {
        this.ipList.add(ip);
        return this;
    }

    @Override
    public int compareTo(final ConnectionDescriptor o) {
        if (!CommonUtils.checkStringEquality(this.getConnectionName(), o.getConnectionName())) {
            return -1;
        }
        if (!CommonUtils.checkStringEquality(this.getUsername(), o.getUsername())) {
            return -1;
        }
        if (!CommonUtils.checkStringEquality(this.getPassword(), o.getPassword())) {
            return -1;
        }
        if (this.getPort() != o.getPort()) {
            return -1;
        }
        if (this.getSmppWindowSize() != o.getSmppWindowSize()) {
            return -1;
        }
        if (this.getConnectionType() != o.getConnectionType()) {
            return -1;
        }
        if (this.ipList.size() != o.ipList.size()) {
            return -1;
        } else {
            try {
                for (int i = 0; i < this.ipList.size(); i++) {
                    if (!this.ipList.get(i).equals(o.ipList.get(i))) {
                        return -1;
                    }
                }
            } catch (final Exception e) {
                return -1;
            }
        }
        return 0;
    }

    public final void validate() throws Exception {
        if (CommonUtils.checkStringIsEmpty(this.getConnectionGroupName())) {
            throw new SmppApiException(SmppApiException.MISSING_PARAMETER, SmppApiException.DOMAIN_SMPP_CONNECTION, "Connection group name can not be empty!");
        }
        if (CommonUtils.checkStringIsEmpty(this.getConnectionName())) {
            throw new SmppApiException(SmppApiException.MISSING_PARAMETER, SmppApiException.DOMAIN_SMPP_CONNECTION, "Connection name can not be empty!");
        }
        if (CommonUtils.checkStringIsEmpty(this.getUsername())) {
            throw new SmppApiException(SmppApiException.MISSING_PARAMETER, SmppApiException.DOMAIN_SMPP_CONNECTION, "Username can not be empty!");
        }
        if (CommonUtils.checkStringIsEmpty(this.getPassword())) {
            throw new SmppApiException(SmppApiException.MISSING_PARAMETER, SmppApiException.DOMAIN_SMPP_CONNECTION, "password can not be empty!");
        }
        if ((this.getPort() < 0) || (this.getPort() > 65535)) {
            throw new SmppApiException(SmppApiException.MISSING_PARAMETER, SmppApiException.DOMAIN_SMPP_CONNECTION, "port should be between 0-65535");
        }

        if ((this.getSmppWindowSize() < 0) || (this.getSmppWindowSize() > 5000)) {
            throw new SmppApiException(SmppApiException.MISSING_PARAMETER, SmppApiException.DOMAIN_SMPP_CONNECTION, "Window size should be between 0-5000");
        }
        if (this.callbackInterface == null) {
            throw new SmppApiException(SmppApiException.MISSING_PARAMETER, SmppApiException.DOMAIN_SMPP_CONNECTION, "Callback interface can not be null!");
        }

        if (this.reduceTpsBy > 95) {
            throw new SmppApiException(SmppApiException.INVALID_PARAMETER,
                                       SmppApiException.DOMAIN_SMPP_CONNECTION,
                                       "TPS can be reduced by %95 max. Your value : " + this.reduceTpsBy);
        }

    }

    public ConnectionDescriptor getACopy() {
        final ConnectionDescriptor connectionDescriptor = new ConnectionDescriptor(this.getConnectionGroupName(), this.getConnectionName());
        connectionDescriptor.setUsername(this.getUsername());
        connectionDescriptor.setPassword(this.getPassword());
        connectionDescriptor.setPort(this.getPort());
        connectionDescriptor.setWindowSize(this.getSmppWindowSize());
        connectionDescriptor.setConnectionType(this.getConnectionType());
        connectionDescriptor.setCallbackInterface(this.getCallbackInterface());
        connectionDescriptor.setTraceON(this.isTraceON());
        connectionDescriptor.setMaxThreadCount(this.getMaxThreadCount());
        connectionDescriptor.setTps(this.getTps());
        connectionDescriptor.setThrottlePeriod(this.getThrottlePeriod());
        connectionDescriptor.setReduceTpsBy(this.getReduceTpsBy());
        connectionDescriptor.setSuspendPeriod(this.getSuspendPeriod());
        if (this.ipList.size() > 0) {
            for (final String ip : this.ipList) {
                connectionDescriptor.addIp(ip);
            }
        }
        return connectionDescriptor;

    }

    public ISmppCallback getCallbackInterface() {
        return this.callbackInterface;
    }

    public String getConnectionGroupName() {
        return this.connectionGroupName;
    }

    public String getConnectionName() {
        return this.connectionName;
    }

    public SmppConnectionType getConnectionType() {
        return this.connectionType;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public ArrayList<String> getIpList() {
        return this.ipList;
    }

    public int getMaxThreadCount() {
        return this.maxThreadCount;
    }

    public String getPassword() {
        return this.password;
    }

    public int getPort() {
        return this.port;
    }

    public int getSmppWindowSize() {
        return this.smppWindowSize;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isTraceON() {
        return this.trace;
    }

    public ConnectionDescriptor setCallbackInterface(final ISmppCallback smppCallback) {
        this.callbackInterface = smppCallback;
        return this;

    }

    public void setConnectionGroupName(final String connectionGroupName) {
        this.connectionGroupName = connectionGroupName;
    }

    public ConnectionDescriptor setConnectionName(final String connectionName) {
        this.connectionName = connectionName;
        return this;
    }

    public ConnectionDescriptor setConnectionType(final SmppConnectionType connectionType) {
        this.connectionType = connectionType;
        return this;
    }

    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public ConnectionDescriptor setMaxThreadCount(final int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
        return this;
    }

    public ConnectionDescriptor setPassword(final String password) {
        this.password = password;
        return this;
    }

    public ConnectionDescriptor setPort(final int port) {
        this.port = port;
        return this;
    }

    public ConnectionDescriptor setTraceON(final boolean trace) {
        this.trace = trace;
        return this;
    }

    public ConnectionDescriptor setUsername(final String username) {
        this.username = username;
        return this;
    }

    public ConnectionDescriptor setWindowSize(final int windowSize) {
        this.smppWindowSize = windowSize;
        return this;
    }

    @Override
    public String toString() {
        return "SmppConnection [connectionGroupName="
               + this.connectionGroupName
               + ", connectionName="
               + this.connectionName
               + ", username="
               + this.username
               + ", password="
               + this.password
               + ", port="
               + this.port
               + ", ipList="
               + this.ipList
               + ", smppWindowSize="
               + this.smppWindowSize
               + ", connectionType="
               + this.connectionType
               + ", maxThreadCount="
               + this.maxThreadCount
               + ", trace="
               + this.trace
               + ", callbackInterface="
               + this.callbackInterface
               + "]";
    }

    public int getTps() {
        return this.tps;
    }

    public ConnectionDescriptor setTps(final int tps) {
        this.tps = tps;
        return this;
    }

    public long getThrottlePeriod() {
        return this.throttlePeriod;
    }

    public ConnectionDescriptor setThrottlePeriod(final long throttlePeriod) {
        this.throttlePeriod = throttlePeriod;
        return this;
    }

    public int getReduceTpsBy() {
        return this.reduceTpsBy;
    }

    public ConnectionDescriptor setReduceTpsBy(final int reduceTpsBy) {
        this.reduceTpsBy = reduceTpsBy;
        return this;
    }

    public long getSuspendPeriod() {
        return this.suspendPeriod;
    }

    public ConnectionDescriptor setSuspendPeriod(final long suspendPeriod) {
        this.suspendPeriod = suspendPeriod;
        return this;
    }
}
