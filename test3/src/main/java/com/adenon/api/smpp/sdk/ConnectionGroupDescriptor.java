package com.adenon.api.smpp.sdk;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import com.adenon.api.smpp.common.CommonUtils;

public class ConnectionGroupDescriptor {

    private final String                                  connectionGrouptName;
    private final Hashtable<String, ConnectionDescriptor> connectiontable = new Hashtable<String, ConnectionDescriptor>();

    public ConnectionGroupDescriptor(final String clientName) {
        this.connectionGrouptName = clientName;

    }

    public ConnectionGroupDescriptor addConnection(final ConnectionDescriptor connectionDescriptor) {
        if (connectionDescriptor != null) {
            final String connectionName = connectionDescriptor.getConnectionName();
            if (!CommonUtils.checkStringIsEmpty(connectionName)) {
                this.connectiontable.put(connectionName, connectionDescriptor);
            }
        }
        return this;
    }

    public ConnectionDescriptor generateConnection(final String connectionName) {
        final ConnectionDescriptor connectionDescriptor = new ConnectionDescriptor(this.getConnectionGroupName(), connectionName);
        return connectionDescriptor;

    }

    public String getConnectionGroupName() {
        return this.connectionGrouptName;
    }

    public ArrayList<ConnectionDescriptor> getConnectionList() {
        final ArrayList<ConnectionDescriptor> descriptors = new ArrayList<ConnectionDescriptor>();
        final Enumeration<ConnectionDescriptor> elements = this.connectiontable.elements();
        while (elements.hasMoreElements()) {
            final ConnectionDescriptor connectionDescriptor = elements.nextElement();
            descriptors.add(connectionDescriptor);
        }
        return descriptors;
    }

    public boolean removeConnection(final String connectionName) {
        try {
            this.connectiontable.remove(connectionName);
            return true;
        } catch (final Exception e) {
        }
        return false;
    }
}
