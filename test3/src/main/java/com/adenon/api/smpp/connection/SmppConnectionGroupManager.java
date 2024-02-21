package com.adenon.api.smpp.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.adenon.api.smpp.common.StateHigherAuthority;
import com.adenon.api.smpp.core.IOReactorStorage;
import com.adenon.api.smpp.core.SmppApiDelegator;
import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.logging.LogManager;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.sdk.ConnectionDescriptor;
import com.adenon.api.smpp.sdk.ConnectionGroupDescriptor;
import com.adenon.api.smpp.sdk.ConnectionInformation;

public class SmppConnectionGroupManager {

    private final LoggerWrapper                    logger;

    private final Map<String, SmppConnectionGroup> connectionGroupMap   = new HashMap<String, SmppConnectionGroup>();
    private final Object                           synchronizationObject;
    private SmppConnectionGroup[]                  clientConnectionsArray;
    private final LogManager                       logManager;
    private final IOReactorStorage                 smppIOReactorStorage;
    private final StateHigherAuthority             stateHigherAuthority = new StateHigherAuthority();
    private final SmppApiDelegator                 smppApiDelegator;


    public SmppConnectionGroupManager(final IOReactorStorage smppIOReactorStorage,
                                      final LogManager logManager,
                                      final Object syncObject,
                                      final SmppApiDelegator smppApiDelegator) {
        this.logManager = logManager;
        this.synchronizationObject = syncObject;
        this.smppApiDelegator = smppApiDelegator;
        this.logger = this.logManager.getLogControler().getLogger();
        this.smppIOReactorStorage = smppIOReactorStorage;
    }

    public SmppConnectionGroup get(final String connectionGroupName) {
        return this.getConnectionGroupMap().get(connectionGroupName);
    }

    public void put(final String clientName,
                    final SmppConnectionGroup connectionGroupController) {
        this.getConnectionGroupMap().put(clientName, connectionGroupController);
    }

    public int getConnectionGroupSize() {
        return this.getConnectionGroupMap().size();
    }

    public Iterator<Entry<String, SmppConnectionGroup>> getConnectionGroupControllerIterator() {
        return this.getConnectionGroupMap().entrySet().iterator();
    }

    public SmppConnectionGroup remove(final String clientName) {
        return this.getConnectionGroupMap().remove(clientName);

    }

    public void clear() {
        this.getConnectionGroupMap().clear();

    }


    public void createConnectionGroup(final ConnectionGroupDescriptor connectionGroupDescriptor) throws Exception {
        synchronized (this.synchronizationObject) {
            final ArrayList<ConnectionDescriptor> connectionList = connectionGroupDescriptor.getConnectionList();
            final String connectionGroupName = connectionGroupDescriptor.getConnectionGroupName();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("SmppConnectionGroupManager", "createConnectionGroup", 0, null, "Client Intializing in progress. Client : "
                                                                                                  + connectionGroupName);
            }
            if (connectionGroupName == null) {
                this.logger.error("SmppConnectionGroupManager",
                                  "createConnectionGroup",
                                  0,
                                  null,
                                  " : Error : You must enter connection group name for proper initialization.");
                throw new NullPointerException("Connection Group Name is NULL !!!");
            }
            if ((connectionList == null) || (connectionList.size() == 0)) {
                this.logger.error("SmppConnectionGroupManager",
                                  "initSmppClient",
                                  0,
                                  null,
                                  " : Error : You must enter at least one host to initialize client : "
                                          + connectionGroupName
                                          + " . Given list was empty or null.");
                throw new NullPointerException("Connection List is NULL !!!");
            }
            SmppConnectionGroup connectionGroup = null;
            connectionGroup = this.get(connectionGroupName);
            if (connectionGroup != null) {
                this.checkConnections(connectionGroup, connectionList);
            } else {
                connectionGroup = new SmppConnectionGroup(connectionGroupName, this.logger, this.stateHigherAuthority);
                final Iterator<ConnectionDescriptor> connectionIterator = connectionList.iterator();
                ConnectionDescriptor connectionDescriptor;
                LoggerWrapper clogger;
                while (connectionIterator.hasNext()) {
                    try {
                        connectionDescriptor = connectionIterator.next();
                        switch (this.logManager.getLogType()) {
                            case LogAllInOneFile:
                                clogger = this.logger;
                                break;
                            case LogConnectionGroupSeparetly:
                                clogger = this.logManager.getLogControler().getLogger(connectionGroupName);
                                break;
                            case LogConnectionsSeparetly:
                                clogger = this.logManager.getLogControler().getLogger(connectionGroupName + "_" + connectionDescriptor.getConnectionName());
                                break;
                            default:
                                clogger = this.logger;
                                break;
                        }
                        final SmppIOReactor smppIOReactor = new SmppIOReactor(clogger,
                                                                              connectionGroupName,
                                                                              connectionDescriptor,
                                                                              connectionGroup.getStateHigherAuthority(),
                                                                              this.smppApiDelegator);
                        smppIOReactor.initialize();
                        this.addIOReactor(connectionGroup, smppIOReactor);

                    } catch (final Exception e) {
                        this.logger.error("SmppConnectionGroupManager", "createConnectionGroup", 0, null, " : Error : " + e.getMessage(), e);
                        throw e;
                    }
                }
                this.put(connectionGroupName, connectionGroup);
                this.fillArray();
            }
        }
    }


    private void addIOReactor(final SmppConnectionGroup connectionGroup,
                              final SmppIOReactor ioReactor) {
        connectionGroup.addConnection(ioReactor);
        this.smppIOReactorStorage.addSmppIOReactor(ioReactor);
    }

    private void fillArray() {
        synchronized (this.synchronizationObject) {
            try {
                if (this.getConnectionGroupSize() > 0) {
                    this.setClientConnectionsArray(new SmppConnectionGroup[this.getConnectionGroupSize()]);
                    Map.Entry<String, SmppConnectionGroup> cc = null;
                    final Iterator<Map.Entry<String, SmppConnectionGroup>> iterConnection = this.getConnectionGroupControllerIterator();
                    int c_index = 0;
                    while (iterConnection.hasNext()) {
                        cc = iterConnection.next();
                        this.getClientConnectionsArray()[c_index] = cc.getValue();
                        c_index++;
                    }
                } else {
                    this.setClientConnectionsArray(null);
                }
            } catch (final Exception e) {
                this.logger.error("SmppConnectionGroupManager", "fillArray", 0, null, " : Error : " + e.getMessage(), e);
            }
        }
    }

    private void checkConnections(final SmppConnectionGroup connectionPool,
                                  final ArrayList<ConnectionDescriptor> connectionDescriptorList) throws Exception {
        synchronized (this.synchronizationObject) {
            Hashtable<String, ConnectionDescriptor> tempConnectionDescriptorMap = new Hashtable<String, ConnectionDescriptor>();
            final Iterator<ConnectionDescriptor> iterator = connectionDescriptorList.iterator();
            ConnectionDescriptor connectionDescriptor = null;
            SmppIOReactor customerConnection = null;
            while (iterator.hasNext()) {
                connectionDescriptor = iterator.next();
                tempConnectionDescriptorMap.put(connectionDescriptor.getConnectionName(), connectionDescriptor);
                customerConnection = connectionPool.getConnection(connectionDescriptor.getConnectionName());
                if (customerConnection == null) {
                    this.addConnection(connectionPool, connectionDescriptor);
                } else {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("SmppConnectionGroupManager", "checkConnections", 0, null, "Starting to Check connection : "
                                                                                                     + customerConnection.getConnectionDescriptor()
                                                                                                                         .getConnectionName());
                    }
                    if (customerConnection.getConnectionDescriptor().compareTo(connectionDescriptor) != 0) {
                        connectionPool.removeConnection(customerConnection.getConnectionDescriptor().getConnectionName());
                        this.addConnection(connectionPool, connectionDescriptor);
                    } else {
                        customerConnection.updateConnectionDescriptor(connectionDescriptor);
                    }
                }
            }

            final Iterator<Map.Entry<String, SmppIOReactor>> iterConnections = connectionPool.entries();
            final ArrayList<String> hostToBeRemoved = new ArrayList<String>();
            while (iterConnections.hasNext()) {
                final Map.Entry<String, SmppIOReactor> entry = iterConnections.next();
                final SmppIOReactor ioReactor = entry.getValue();
                connectionDescriptor = tempConnectionDescriptorMap.get(ioReactor.getConnectionDescriptor().getConnectionName());
                if (connectionDescriptor == null) {
                    hostToBeRemoved.add(ioReactor.getConnectionDescriptor().getConnectionName());
                    try {
                        ioReactor.shutdown();
                    } catch (final Exception e) {
                        this.logger.error("SmppConnectionGroupManager", "checkConnections", 0, null, " : Error : " + e.getMessage(), e);
                    }
                }
            }
            for (int i = 0; i < hostToBeRemoved.size(); i++) {
                connectionPool.removeConnection(hostToBeRemoved.get(i));
            }
            tempConnectionDescriptorMap.clear();
            tempConnectionDescriptorMap = null;
        }
    }

    public void addConnection(final String connectionGroupName,
                              final ConnectionDescriptor connectionDescriptor) throws Exception {
        synchronized (this.synchronizationObject) {
            if ((connectionGroupName == null) || (connectionDescriptor == null)) {
                this.logger.error("SmppConnectionGroupManager", "addHostToClient", 0, null, " You should provide a valid host for client : "
                                                                                            + connectionGroupName);
                throw new NullPointerException("Connection Descriptor or Connection Group is NULL !!");
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("SmppConnectionGroupManager", "addHostToClient", 0, null, "Populating the client : "
                                                                                            + connectionGroupName
                                                                                            + " host information : "
                                                                                            + connectionDescriptor);
            }
            SmppIOReactor ioReactor = null;
            SmppConnectionGroup connectionGroup = null;
            connectionGroup = this.get(connectionGroupName);
            if (connectionGroup == null) {
                connectionGroup = this.generateConnectionGroupForOneConnection(connectionDescriptor);
            }
            ioReactor = connectionGroup.getConnection(connectionDescriptor.getConnectionName());
            if (ioReactor == null) {
                this.addConnection(connectionGroup, connectionDescriptor);
            } else {
                if (ioReactor.getConnectionDescriptor().compareTo(connectionDescriptor) != 0) {
                    connectionGroup.removeConnection(ioReactor.getConnectionDescriptor().getConnectionName());
                    this.addConnection(connectionGroup, connectionDescriptor);
                } else {
                    ioReactor.updateConnectionDescriptor(connectionDescriptor);
                }
            }
        }
    }


    private SmppConnectionGroup generateConnectionGroupForOneConnection(final ConnectionDescriptor connectionDescriptor) throws Exception {
        final String connectionGroupName = connectionDescriptor.getConnectionGroupName();
        final SmppConnectionGroup connectionGroup = new SmppConnectionGroup(connectionGroupName, this.logger, this.stateHigherAuthority);
        this.addConnection(connectionGroup, connectionDescriptor);
        this.put(connectionGroupName, connectionGroup);
        this.fillArray();
        return connectionGroup;

    }

    private void addConnection(final SmppConnectionGroup connectionGroup,
                               final ConnectionDescriptor connectionDescriptor) throws Exception {
        if (connectionGroup == null) {
            this.logger.error("SmppConnectionGroupManager", "addConnection", 0, null, "Connection Group was empty !");
            throw new NullPointerException("Connection Group Object is Null!!");
        }
        try {
            LoggerWrapper clogger;
            switch (this.logManager.getLogType()) {
                case LogAllInOneFile:
                    clogger = this.logger;
                    break;
                case LogConnectionGroupSeparetly:
                    clogger = this.logManager.getLogControler().getLogger(connectionGroup.getConnectionGroupName());
                    break;
                case LogConnectionsSeparetly:
                    clogger = this.logManager.getLogControler().getLogger(connectionGroup.getConnectionGroupName()
                                                                          + "_"
                                                                          + connectionDescriptor.getConnectionName());
                    break;
                default:
                    clogger = this.logger;
                    break;
            }
            final SmppIOReactor smppIOReactor = new SmppIOReactor(clogger,
                                                                  connectionGroup.getConnectionGroupName(),
                                                                  connectionDescriptor,
                                                                  connectionGroup.getStateHigherAuthority(),
                                                                  this.smppApiDelegator);
            smppIOReactor.initialize();
            this.addIOReactor(connectionGroup, smppIOReactor);
        } catch (final Exception e) {
            this.logger.error("SmppConnectionGroupManager", "addHostToClient", 0, null, " : Error : " + e.getMessage(), e);
            throw e;
        }
    }

    public void removeConnection(final String connectionGroupName,
                                 final String connectionName) {
        synchronized (this.synchronizationObject) {
            final SmppConnectionGroup connectionPool = this.get(connectionGroupName);
            if (connectionPool != null) {
                this.removeConnectionFromConnectionGroup(connectionPool, connectionName);
                if (connectionPool != null) {
                    connectionPool.removeConnection(connectionName);
                }
            }
        }
    }

    private void removeConnectionFromConnectionGroup(final SmppConnectionGroup connectionGroup,
                                                     final String connectionName) {
        if (connectionGroup != null) {
            connectionGroup.removeConnection(connectionName);
        }
    }

    public void doAction(final EConnectionStateChange stateChange) {
        synchronized (this.synchronizationObject) {
            final Iterator<Map.Entry<String, SmppConnectionGroup>> iterConnection = this.getConnectionGroupControllerIterator();
            while (iterConnection.hasNext()) {
                final Map.Entry<String, SmppConnectionGroup> enumConnection = iterConnection.next();
                final SmppConnectionGroup connectionGroup = enumConnection.getValue();
                connectionGroup.doAction(stateChange);
            }
            this.clear();
        }
    }

    public void doAction(final String connectionGroupName,
                         final EConnectionStateChange stateChange) {
        synchronized (this.synchronizationObject) {
            SmppConnectionGroup smppConnectionGroup = this.get(connectionGroupName);
            if (smppConnectionGroup == null) {
                return;
            }
            smppConnectionGroup.doAction(stateChange);
            if (stateChange == EConnectionStateChange.SHUTDOWN) {
                this.remove(connectionGroupName);
                this.fillArray();
            }
            smppConnectionGroup = null;
        }
    }

    public void doAction(final String connectionGroupName,
                         final String connectionName,
                         final EConnectionStateChange stateChange) {
        synchronized (this.synchronizationObject) {
            final SmppConnectionGroup smppConnectionGroup = this.get(connectionGroupName);
            if (smppConnectionGroup == null) {
                return;
            }
            smppConnectionGroup.doAction(connectionName, stateChange);
        }
    }

    public int getCapacity(final String connectionGroupName) {
        synchronized (this.synchronizationObject) {
            final SmppConnectionGroup smppConnectionGroup = this.get(connectionGroupName);
            if (smppConnectionGroup == null) {
                return 0;
            }
            return smppConnectionGroup.getCapacity();
        }
    }

    public SmppConnectionGroup[] getClientConnectionsArray() {
        return this.clientConnectionsArray;
    }

    public void setClientConnectionsArray(final SmppConnectionGroup[] clientConnectionsArray) {
        this.clientConnectionsArray = clientConnectionsArray;
    }

    public Map<String, SmppConnectionGroup> getConnectionGroupMap() {
        return this.connectionGroupMap;
    }

    public StateHigherAuthority getStateHigherAuthority() {
        return this.stateHigherAuthority;
    }

    public void setTps(final String connectionGroupName,
                       final String connectionName,
                       final int tpsCount) {

        synchronized (this.synchronizationObject) {
            final SmppConnectionGroup connectionPool = this.get(connectionGroupName);
            if (connectionPool != null) {
                connectionPool.setTps(connectionName, tpsCount);
            }
        }

    }

    public ConnectionInformation getConnectionInformation(final String connectionGroupName,
                                                          final String connectionName) {
        synchronized (this.synchronizationObject) {
            final SmppConnectionGroup smppConnectionGroup = this.get(connectionGroupName);
            if (smppConnectionGroup == null) {
                return null;
            }
            return smppConnectionGroup.getConnectionInformation(connectionName);
        }
    }

    public List<ConnectionInformation> getConnectionInformations(final String connectionGroupName) {
        synchronized (this.synchronizationObject) {
            final SmppConnectionGroup smppConnectionGroup = this.get(connectionGroupName);
            if (smppConnectionGroup == null) {
                return null;
            }
            return smppConnectionGroup.getConnectionInformations();
        }
    }

}
