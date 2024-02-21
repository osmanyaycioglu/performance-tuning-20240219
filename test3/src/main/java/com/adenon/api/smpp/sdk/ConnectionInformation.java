package com.adenon.api.smpp.sdk;

import com.adenon.api.smpp.common.State;
import com.adenon.api.smpp.core.IIOReactor;
import com.adenon.library.common.utils.tps.NonBlockingTpsCounter;

public class ConnectionInformation {

    private final IIOReactor      smppIOReactor;
    private String                connectionName;
    private final String          connectionGroupName;
    private String                ip;
    private int                   port;
    private boolean               connected;
    private State                 state = new State();
    private String                connectionLabel;
    private String                userName;
    private int                   tps;
    private NonBlockingTpsCounter nonBlockingTpsCounter;

    public ConnectionInformation(final IIOReactor smppIOReactor,
                                 final String connectionGroupName,
                                 final String connectionName) {
        this.smppIOReactor = smppIOReactor;
        this.connectionGroupName = connectionGroupName;
        this.setConnectionName(connectionName);
    }

    public String getConnectionName() {
        return this.connectionName;
    }

    public String getConnectionGroupName() {
        return this.connectionGroupName;
    }

    public String getIp() {
        return this.ip;
    }

    public ConnectionInformation setIp(final String ip) {
        this.ip = ip;
        return this;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void setConnected(final boolean connected) {
        this.connected = connected;
    }

    public State getConnectionState() {
        return this.state;
    }

    public void setConnectionState(final State light) {
        this.state = light;
    }

    public String getConnectionLabel() {
        return this.connectionLabel;
    }

    public void setConnectionLabel(final String connectionLabel) {
        this.connectionLabel = connectionLabel;
    }

    public IIOReactor getSmppIOReactor() {
        return this.smppIOReactor;
    }

    @Override
    public String toString() {
        return this.ip + ":" + this.port + " [" + this.connectionGroupName + "@" + this.getConnectionName() + "]";
    }

    public String toLongString() {
        return "<ConnectionInformation> connectionName : "
               + this.connectionName
               + " , connectionGroupName : "
               + this.connectionGroupName
               + " , ip : "
               + this.ip
               + " , port : "
               + this.port
               + " , connected : "
               + this.connected
               + " , state : *"
               + this.state
               + "* , connectionLabel : "
               + this.connectionLabel
               + " , userName : "
               + this.userName
               + " , tps : "
               + this.tps
               + " , nonBlockingTpsCounter : "
               + this.nonBlockingTpsCounter;
    }

    public void setConnectionName(final String connectionName) {
        this.connectionName = connectionName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public int getTps() {
        return this.tps;
    }

    public void setTps(final int tps) {
        this.tps = tps;
    }

    public NonBlockingTpsCounter getNonBlockingTpsCounter() {
        return this.nonBlockingTpsCounter;
    }

    public void setNonBlockingTpsCounter(final NonBlockingTpsCounter nonBlockingTpsCounter) {
        this.nonBlockingTpsCounter = nonBlockingTpsCounter;
    }

    public int getAverageTps() {
        if (this.nonBlockingTpsCounter == null) {
            return 0;
        }
        return this.nonBlockingTpsCounter.getAverageTps();
    }

    public boolean isBinded() {
        return this.smppIOReactor.getBinded().get();
    }


}
