package com.adenon.smpp.server.managers;

import java.util.ArrayList;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.core.IIOReactor;
import com.adenon.api.smpp.core.IOReactorStorage;
import com.adenon.smpp.server.core.ServerApiDelegator;
import com.adenon.smpp.server.core.ServerIOReactor;


public class ServerConnectionManager {


    private final ServerApiDelegator serverApiDelegator;

    public ServerConnectionManager(final ServerApiDelegator serverApiDelegator) {
        this.serverApiDelegator = serverApiDelegator;
    }

    public boolean closeConnection(final String reason,
                                   final String connectionName) {
        try {
            final IOReactorStorage smppIOReactorStorage = this.serverApiDelegator.getSmppIOReactorStorage();
            final ArrayList<IIOReactor> ioReactors = smppIOReactorStorage.getIoReactors();
            for (final IIOReactor iioReactor : ioReactors) {
                final ServerIOReactor ioReactor = (ServerIOReactor) iioReactor;
                try {
                    if (CommonUtils.checkStringEquality(connectionName, ioReactor.getConnectionInformation().getConnectionName())) {
                        ioReactor.closeConnection(reason);
                        return true;
                    }
                } catch (final Exception e) {
                    this.serverApiDelegator.getLogManager()
                                           .getLogger()
                                           .error("ServerConnectionManager", "closeConnection", 0, null, " : Error : " + e.getMessage(), e);
                }
            }
        } catch (final Exception e) {
            this.serverApiDelegator.getLogManager().getLogger().error("ServerConnectionManager", "closeConnection", 0, null, " : Error : " + e.getMessage(), e);
        }
        return false;
    }

    public ArrayList<String> getConnectedConnectionNames() {
        try {
            final ArrayList<String> connections = new ArrayList<String>();
            final IOReactorStorage smppIOReactorStorage = this.serverApiDelegator.getSmppIOReactorStorage();
            final ArrayList<IIOReactor> ioReactors = smppIOReactorStorage.getIoReactors();
            for (final IIOReactor iioReactor : ioReactors) {
                final ServerIOReactor ioReactor = (ServerIOReactor) iioReactor;
                connections.add(ioReactor.getConnectionInformation().getConnectionName());
            }
            return connections;
        } catch (final Exception e) {
            this.serverApiDelegator.getLogManager().getLogger().error("ServerConnectionManager", "closeConnection", 0, null, " : Error : " + e.getMessage(), e);
        }
        return null;
    }
}
