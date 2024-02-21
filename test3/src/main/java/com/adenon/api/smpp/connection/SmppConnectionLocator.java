package com.adenon.api.smpp.connection;

import com.adenon.api.smpp.common.IndexCounter;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.api.smpp.core.SmppIOReactor;

public class SmppConnectionLocator {

    private final IndexCounter               index = new IndexCounter();
    private final SmppConnectionGroupManager connectionGroupManager;
    private final Object                     syncObject;

    public SmppConnectionLocator(final SmppConnectionGroupManager smppConnectionGroupManager,
                                 final Object syncObject) {
        this.connectionGroupManager = smppConnectionGroupManager;
        this.syncObject = syncObject;
    }

    public SmppIOReactor findAvaliableConnection(final int messageCount) {
        final SmppConnectionGroup[] clientConnectionsArray = this.connectionGroupManager.getClientConnectionsArray();
        if (clientConnectionsArray != null) {
            int lIndex = this.index.increase(clientConnectionsArray.length);
            for (int j = 0; j < clientConnectionsArray.length; j++) {
                if (lIndex > (clientConnectionsArray.length - 1)) {
                    lIndex = 0;
                }
                if (clientConnectionsArray[lIndex] != null) {
                    final SmppIOReactor ioReactor = clientConnectionsArray[lIndex].getConnectedConnection(messageCount);
                    if (ioReactor != null) {
                        return ioReactor;
                    }
                }
                lIndex++;
            }
        }
        return null;
    }

    public SmppIOReactor findAvaliableClientConnection(final String connectionGroupName,
                                                       final String connectionName,
                                                       final int messageCount) throws SmppApiException {
        SmppIOReactor ioReactor = null;
        if ((connectionGroupName == null) || "".equals(connectionGroupName)) {
            ioReactor = this.findAvaliableConnection(messageCount);
        } else {
            final SmppConnectionGroup connectionGroup = this.connectionGroupManager.get(connectionGroupName);
            if (connectionGroup == null) {
                throw new SmppApiException(SmppApiException.NOT_AVAILABLE, SmppApiException.DOMAIN_SMPP_CONNECTION, "Connection group is not available : "
                                                                                                                    + connectionGroupName);
            }

            if (connectionName == null) {
                ioReactor = connectionGroup.getConnectedConnection(messageCount);
                if (ioReactor == null) {
                    int counter = 0;
                    while ((ioReactor == null) && (counter < 10)) {
                        try {
                            Thread.sleep(8L);
                        } catch (final Exception e) {
                        }
                        ioReactor = connectionGroup.getConnectedConnection(messageCount);
                        counter++;
                    }
                }
            } else {
                ioReactor = connectionGroup.getConnectedConnection(connectionName, messageCount);
                if (ioReactor == null) {
                    int counter = 0;
                    while ((ioReactor == null) && (counter < 10)) {
                        try {
                            Thread.sleep(10L);
                        } catch (final Exception e) {
                        }
                        ioReactor = connectionGroup.getConnectedConnection(connectionName, messageCount);
                        counter++;
                    }
                }
            }
        }
        return ioReactor;
    }

}
