package com.adenon.api.smpp;

import java.util.List;

import com.adenon.api.smpp.common.State;
import com.adenon.api.smpp.connection.EConnectionStateChange;
import com.adenon.api.smpp.connection.SmppConnectionGroup;
import com.adenon.api.smpp.core.SmppApiDelegator;
import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.sdk.ConnectionDescriptor;
import com.adenon.api.smpp.sdk.ConnectionGroupDescriptor;
import com.adenon.api.smpp.sdk.ConnectionInformation;

public class SmppConnectionApi {

    private final SmppApiDelegator smppApiDelegator;

    public SmppConnectionApi(final SmppApiDelegator smppApiDelegator) {
        this.smppApiDelegator = smppApiDelegator;
    }

    public void addConnection(final ConnectionDescriptor connectionDescriptor) throws Exception {
        connectionDescriptor.validate();
        this.smppApiDelegator.getConnectionGroupManager().addConnection(connectionDescriptor.getConnectionGroupName(), connectionDescriptor);
    }

    public void createConnectionGroup(final ConnectionGroupDescriptor connectionGroupDescriptor) throws Exception {
        this.smppApiDelegator.getConnectionGroupManager().createConnectionGroup(connectionGroupDescriptor);
    }

    public ConnectionGroupDescriptor generateConnectionGroup(final String connectionGroupName) {
        final ConnectionGroupDescriptor connectionGroupDescriptor = new ConnectionGroupDescriptor(connectionGroupName);
        return connectionGroupDescriptor;
    }

    public State getConnectionGroupState(final String connectionGroupName) {
        final SmppConnectionGroup smppConnectionGroup = this.smppApiDelegator.getConnectionGroupManager().get(connectionGroupName);
        if (smppConnectionGroup != null) {
            return smppConnectionGroup.getStateHigherAuthority();
        }
        return null;
    }

    public State getState() {
        return this.smppApiDelegator.getConnectionGroupManager().getStateHigherAuthority();
    }

    public State getState(final String connectionGroupName,
                          final String connectionName) {
        try {
            final SmppConnectionGroup smppConnectionGroup = this.smppApiDelegator.getConnectionGroupManager().get(connectionGroupName);
            final SmppIOReactor connection = smppConnectionGroup.getConnection(connectionName);
            final ConnectionInformation connectionInformation = connection.getConnectionInformation();
            return connectionInformation.getConnectionState();
        } catch (final Exception e) {
        }
        return null;
    }

    public void setTps(final String connectionGroupName,
                       final String connectionName,
                       final int tpsCount) {
        this.smppApiDelegator.getConnectionGroupManager().setTps(connectionGroupName, connectionName, tpsCount);

    }

    public void removeConnection(final String connectionGroupName,
                                 final String connectionName) {
        this.smppApiDelegator.getConnectionGroupManager().removeConnection(connectionGroupName, connectionName);
    }

    public void removeConnectionGroup(final String connectionGroupName) {
        this.smppApiDelegator.getConnectionGroupManager().doAction(connectionGroupName, EConnectionStateChange.SHUTDOWN);
    }

    public void shutdownAll() {
        this.smppApiDelegator.getConnectionGroupManager().doAction(EConnectionStateChange.SHUTDOWN);
    }

    public void shutdownConnectionGroup(final String connectionGroupName) {
        this.smppApiDelegator.getConnectionGroupManager().doAction(connectionGroupName, EConnectionStateChange.SHUTDOWN);
    }

    public void shutdownConnection(final String connectionGroupName,
                                   final String connectionName) {
        this.smppApiDelegator.getConnectionGroupManager().doAction(connectionGroupName, connectionName, EConnectionStateChange.SHUTDOWN);
    }

    public void restartAll() {
        this.smppApiDelegator.getConnectionGroupManager().doAction(EConnectionStateChange.RESTART);
    }

    public void restartConnectionGroup(final String connectionGroupName) {
        this.smppApiDelegator.getConnectionGroupManager().doAction(connectionGroupName, EConnectionStateChange.RESTART);
    }

    public void restartConnection(final String connectionGroupName,
                                  final String connectionName) {
        this.smppApiDelegator.getConnectionGroupManager().doAction(connectionGroupName, connectionName, EConnectionStateChange.RESTART);
    }

    public void suspendAll() {
        this.smppApiDelegator.getConnectionGroupManager().doAction(EConnectionStateChange.SUSPEND);
    }

    public void suspendConnectionGroup(final String connectionGroupName) {
        this.smppApiDelegator.getConnectionGroupManager().doAction(connectionGroupName, EConnectionStateChange.SUSPEND);
    }

    public void suspendConnection(final String connectionGroupName,
                                  final String connectionName) {
        this.smppApiDelegator.getConnectionGroupManager().doAction(connectionGroupName, connectionName, EConnectionStateChange.SUSPEND);
    }

    public void unsuspendAll() {
        this.smppApiDelegator.getConnectionGroupManager().doAction(EConnectionStateChange.UNSUSPEND);
    }

    public void unsuspendConnectionGroup(final String connectionGroupName) {
        this.smppApiDelegator.getConnectionGroupManager().doAction(connectionGroupName, EConnectionStateChange.UNSUSPEND);
    }

    public void unsuspendConnection(final String connectionGroupName,
                                    final String connectionName) {
        this.smppApiDelegator.getConnectionGroupManager().doAction(connectionGroupName, connectionName, EConnectionStateChange.UNSUSPEND);
    }

    public int getCapacity(final String connectionGroupName) {
        return this.smppApiDelegator.getConnectionGroupManager().getCapacity(connectionGroupName);
    }

    public ConnectionInformation getConnectionInformation(final String connectionGroupName,
                                                          final String connectionName) {
        return this.smppApiDelegator.getConnectionGroupManager().getConnectionInformation(connectionGroupName, connectionName);
    }

    public List<ConnectionInformation> getConnectionInformations(final String connectionGroupName) {
        return this.smppApiDelegator.getConnectionGroupManager().getConnectionInformations(connectionGroupName);
    }

    /**
     * @param connectionGroupName
     * @param connectionName
     * 
     *            if connection is not shutdown unbind the connection from host
     * 
     */
    public void unbindConnection(final String connectionGroupName,
                                 final String connectionName) {

        final SmppIOReactor conn = this.getSmppApiDelegator().getConnectionGroupManager().get(connectionGroupName).getConnection(connectionName);
        if (conn != null) {
            if (!conn.isShutdown()) {
                conn.sendUnbind();
            }
        }
    }

    /**
     * @param connectionGroupName
     * 
     *            unbind all connections of a connection group from host
     */
    public void unbindConnectionGroup(final String connectionGroupName) {
        final SmppConnectionGroup smppConnectionGroup = this.getSmppApiDelegator().getConnectionGroupManager().get(connectionGroupName);
        if (smppConnectionGroup != null) {
            final SmppIOReactor[] connections = smppConnectionGroup.getConnections();
            if (connections != null) {
                for (final SmppIOReactor conn : connections) {
                    this.smppApiDelegator.getLogManager()
                                         .getLogger()
                                         .info("SmppConnectionApi", "unbind", 0, null, "CONNECTION : " + conn.getHostName() + " is being unbound.");
                    this.unbindConnection(connectionGroupName, conn.getHostName());
                    this.smppApiDelegator.getLogManager()
                                         .getLogger()
                                         .info("SmppConnectionApi", "unbind", 0, null, "CONNECTION : " + conn.getHostName() + " has been unbound.");
                }
            }
        }
    }

    /**
     * unbind all connections of system from host
     * 
     */
    public void unbindAllConnectionGroup() {
        final SmppConnectionGroup[] clientConnectionsArray = this.getSmppApiDelegator().getConnectionGroupManager().getClientConnectionsArray();
        for (final SmppConnectionGroup smppConnectionGroup : clientConnectionsArray) {
            this.unbindConnectionGroup(smppConnectionGroup.getConnectionGroupName());
        }
    }

    public SmppApiDelegator getSmppApiDelegator() {
        return this.smppApiDelegator;
    }
}
