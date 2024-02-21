package com.adenon.api.smpp.connection;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.adenon.api.smpp.common.IndexCounter;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.api.smpp.common.State;
import com.adenon.api.smpp.common.StateHigherAuthority;
import com.adenon.api.smpp.core.IIOReactor;
import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.sdk.ConnectionDescriptor;
import com.adenon.api.smpp.sdk.ConnectionInformation;
import com.adenon.api.smpp.sdk.SmppConnectionType;

public class SmppConnectionGroup {

    private final LoggerWrapper        logger;

    private final IndexCounter         index                = new IndexCounter();

    private Map<String, SmppIOReactor> ioStorageMap         = null;
    private String                     connectionGroupName;
    private final State                operationState       = new State();
    private SmppIOReactor[]            connections;
    private Object                     lockObject           = new Object();
    private StateHigherAuthority       stateHigherAuthority = new StateHigherAuthority();

    public SmppConnectionGroup(final String connectionGroupName,
                               final LoggerWrapper logger,
                               final StateHigherAuthority higherAuthorityParent) {
        higherAuthorityParent.addState(this.stateHigherAuthority);
        this.logger = logger;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("SmppConnectionGroup", "SmppConnectionGroup", 0, null, "Connection group created " + connectionGroupName);
        }

        this.setConnectionGroupName(connectionGroupName);
        this.ioStorageMap = new HashMap<String, SmppIOReactor>();
        this.operationState.idle();
    }

    public Iterator<Map.Entry<String, SmppIOReactor>> entries() {
        return this.ioStorageMap.entrySet().iterator();
    }

    public void addConnection(final SmppIOReactor ioReactor) {
        synchronized (this.getLockObject()) {
            try {
                this.operationState.stopped();
                this.setConnections(null);
                this.ioStorageMap.put(ioReactor.getConnectionDescriptor().getConnectionName(), ioReactor);
                this.fillArray();
            } catch (final Exception e) {
                this.logger.error("SmppConnectionGroup", "addConnection", 0, null, " : Error : " + e.getMessage(), e);
            } finally {
                this.operationState.idle();
            }
        }
    }

    public void removeConnection(final String connectionName) {
        synchronized (this.getLockObject()) {
            if (connectionName == null) {
                return;
            }
            IIOReactor ioReactor = null;
            try {
                // this.operationState.stopped();
                ioReactor = this.ioStorageMap.get(connectionName);
                if (ioReactor != null) {
                    this.ioStorageMap.remove(connectionName);
                    this.fillArray();
                }
            } catch (final Exception e) {
                this.logger.error("SmppConnectionGroup", "removeConnection", 0, null, " : Error : " + e.getMessage(), e);
            } finally {
                try {
                    if (ioReactor != null) {
                        ioReactor.shutdown();
                    }
                } catch (final Exception e2) {
                }
                // this.operationState.idle();
            }
        }
    }

    public int getCapacity() {
        try {
            if (this.ioStorageMap.size() > 0) {
                Map.Entry<String, SmppIOReactor> mapEntryConnection = null;
                final Iterator<Map.Entry<String, SmppIOReactor>> iterConnection = this.ioStorageMap.entrySet().iterator();
                int allCapacity = 0;
                while (iterConnection.hasNext()) {
                    mapEntryConnection = iterConnection.next();
                    final SmppIOReactor ioReactor = mapEntryConnection.getValue();
                    if (ioReactor.checkConnectionSanity()) {
                        allCapacity += ioReactor.getConnectionInformation().getNonBlockingTpsCounter().getMaxTps();
                    }
                }
                return allCapacity;
            } else {
                return 0;
            }
        } catch (final Exception e) {
            this.logger.error("SmppConnectionGroup", "getCapacity", 0, null, " : Error : " + e.getMessage(), e);
            return 0;
        }
    }

    private void fillArray() {
        synchronized (this.getLockObject()) {
            try {
                if (this.ioStorageMap.size() > 0) {
                    this.setConnections(new SmppIOReactor[this.ioStorageMap.size()]);
                    Map.Entry<String, SmppIOReactor> cc = null;
                    final Iterator<Map.Entry<String, SmppIOReactor>> iterConnection = this.ioStorageMap.entrySet().iterator();
                    int currentIndex = 0;
                    while (iterConnection.hasNext()) {
                        cc = iterConnection.next();
                        this.getConnections()[currentIndex] = cc.getValue();
                        currentIndex++;
                    }
                } else {
                    this.setConnections(null);
                }
            } catch (final Exception e) {
                this.logger.error("SmppConnectionGroup", "fillArray", 0, null, " : Error : " + e.getMessage(), e);
            }
        }
    }

    // Deneme
    private void AdaptiveFillArray(final Map<String, SmppIOReactor> ioReactorMap) {
        synchronized (this.getLockObject()) {
            try {
                if (ioReactorMap.size() > 0) {
                    final int[] tmpTpsArray = new int[ioReactorMap.size()];
                    final int[] avrgTps = new int[ioReactorMap.size()];
                    final SmppIOReactor[] tmpSmppIOReactorArray = new SmppIOReactor[ioReactorMap.size()];
                    Map.Entry<String, SmppIOReactor> cc = null;
                    final Iterator<Map.Entry<String, SmppIOReactor>> iterConnection = ioReactorMap.entrySet().iterator();
                    int currentIndex = 0;
                    int maxTps = 0;
                    while (iterConnection.hasNext()) {
                        cc = iterConnection.next();
                        final SmppIOReactor ioReactor = cc.getValue();
                        if (ioReactor != null) {
                            tmpSmppIOReactorArray[currentIndex] = ioReactor;
                            int tps = ioReactor.getConnectionDescriptor().getTps();
                            if (tps < 1) {
                                tps = 10;
                            }
                            maxTps += tps;
                            tmpTpsArray[currentIndex] = tps;
                        }
                        currentIndex++;
                    }
                    int totalAraySize = 0;
                    for (int i = 0; i < tmpTpsArray.length; i++) {
                        final float avrgNumber = (float) tmpTpsArray[i] / (float) maxTps;
                        avrgTps[i] = (int) (avrgNumber * 10);
                        if (avrgTps[i] < 1) {
                            avrgTps[i] = 1;
                        }
                        totalAraySize += avrgTps[i];
                    }
                    //
                    for (int i = 0; i < avrgTps.length; i++) {
                        for (int j = i + 1; j < i; j++) {
                            if (avrgTps[j] > avrgTps[i]) {
                                final int temp = avrgTps[i];
                                avrgTps[i] = avrgTps[j];
                                avrgTps[j] = temp;
                                final SmppIOReactor tempSmppIOReactor = tmpSmppIOReactorArray[i];
                                tmpSmppIOReactorArray[i] = tmpSmppIOReactorArray[j];
                                tmpSmppIOReactorArray[j] = tempSmppIOReactor;
                            }
                        }
                    }
                    final ArrayList<SmppIOReactor> arrayOfSmppIOReactors = new ArrayList<SmppIOReactor>();
                    for (int i = 0; i < totalAraySize; i++) {
                        if (avrgTps.length > 1) {
                            for (int j = 0; j < (avrgTps.length - 1); j++) {
                                if ((avrgTps[j] > 0) && (avrgTps[j] >= avrgTps[j + 1])) {
                                    arrayOfSmppIOReactors.add(tmpSmppIOReactorArray[j]);
                                    avrgTps[j]--;
                                    break;
                                }
                            }
                        } else {
                            arrayOfSmppIOReactors.add(tmpSmppIOReactorArray[0]);
                        }
                    }

                    final SmppIOReactor[] smppIOReactorArray = new SmppIOReactor[arrayOfSmppIOReactors.size()];
                    final SmppIOReactor[] smppIOReactors = arrayOfSmppIOReactors.toArray(smppIOReactorArray);
                    this.connections = smppIOReactors;
                } else {
                    this.setConnections(null);
                }
            } catch (final Exception e) {
                this.logger.error("SmppConnectionGroup", "fillArray", 0, null, " : Error : " + e.getMessage(), e);
            }
        }
    }

    public static void main(final String[] args) throws Exception {
        final SmppConnectionGroup connectionGroup = new SmppConnectionGroup("Adenon", null, new StateHigherAuthority());
        final Map<String, SmppIOReactor> ioReactorMap = new HashMap<String, SmppIOReactor>();
        SmppIOReactor ioReactor = new SmppIOReactor(null, "kaan", new ConnectionDescriptor("kaan", "1").setTps(10), new StateHigherAuthority(), null);
        ioReactorMap.put("kaan1", ioReactor);
        ioReactor = new SmppIOReactor(null, "kaan", new ConnectionDescriptor("kaan", "2").setTps(10), new StateHigherAuthority(), null);
        ioReactorMap.put("kaan2", ioReactor);
        connectionGroup.AdaptiveFillArray(ioReactorMap);
    }

    public SmppIOReactor getConnectedConnection(final int messageCount) {
        if (this.getConnections() == null) {
            return null;
        }
        this.operationState.waitIdle();
        for (int j = 0; j < this.getConnections().length; j++) {

            int connectionIndex = this.index.increase(this.getConnections().length);

            if (this.getConnections()[connectionIndex] != null) {
                if (this.getConnections()[connectionIndex].checkConnectionSanity()) {
                    if (this.getConnections()[connectionIndex].getUsedBufferCount() < (this.getConnections()[connectionIndex].getWindowSize() - 2)) {
                        if (this.getConnections()[connectionIndex].getConnectionType() != SmppConnectionType.READ) {
                            if (this.getConnections()[connectionIndex].increaseTps(messageCount)) {
                                return this.getConnections()[connectionIndex];
                            } else {
                                final SmppIOReactor ioReactor = this.getConnections()[connectionIndex];
                                if (this.logger.isDebugEnabled()) {
                                    this.logger.debug("SmppConnectionGroup",
                                                      "getConnectedConnection",
                                                      0,
                                                      " : Warn : ",
                                                      MessageFormat.format("TPS increase returns false. Conn: {0}, Tps Counter = {1}",
                                                                           ioReactor.getConnectionName(),
                                                                           ioReactor.getConnectionInformation().getNonBlockingTpsCounter().getCounter()));
                                }
                            }
                        } else {
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("SmppConnectionGroup",
                                                  "getConnectedConnection",
                                                  0,
                                                  " : Warn : ",
                                                  MessageFormat.format("Connection type is READ. Conn: {0}",
                                                                       this.getConnections()[connectionIndex].getConnectionName()));
                            }
                        }
                    } else {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("SmppConnectionGroup",
                                              "getConnectedConnection",
                                              0,
                                              " : Warn : ",
                                              MessageFormat.format("Used_Buffer_Count >= Window_Size-2 ({0}>={1}), Conn: {3}",
                                                                   this.getConnections()[connectionIndex].getUsedBufferCount(),
                                                                   (this.getConnections()[connectionIndex].getWindowSize() - 2),
                                                                   this.getConnections()[connectionIndex].getConnectionName()));
                        }
                    }
                } else {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("SmppConnectionGroup",
                                          "getConnectedConnection",
                                          0,
                                          " : Warn : ",
                                          MessageFormat.format("Connection Sanity Check Fails: {0}, Conn: {1}",
                                                               this.getConnections()[connectionIndex].getConnectionInformation().getConnectionState(),
                                                               this.getConnections()[connectionIndex].getConnectionName()));
                    }
                }
            } else {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("SmppConnectionGroup", "getConnectedConnection", 0, " : Warn : ", "No connection found with at index " + connectionIndex);
                }
            }
            connectionIndex++;
        }

        try {
            Thread.sleep(1);
        } catch (final Exception e) {
        }

        for (int j = 0; j < this.getConnections().length; j++) {
            final int connectionIndex = this.index.increase(this.getConnections().length);
            if (this.getConnections()[connectionIndex] != null) {
                if (this.getConnections()[connectionIndex].checkConnectionSanity()) {
                    if (this.getConnections()[connectionIndex].getConnectionType() != SmppConnectionType.READ) {
                        if (this.getConnections()[connectionIndex].increaseTps(messageCount)) {
                            return this.getConnections()[connectionIndex];
                        } else {
                            final SmppIOReactor ioReactor = this.getConnections()[connectionIndex];
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("SmppConnectionGroup",
                                                  "getConnectedConnection",
                                                  0,
                                                  " : Warn : ",
                                                  MessageFormat.format("TPS increase returns false. Conn: {0}, Tps Counter = {1}",
                                                                       ioReactor.getConnectionName(),
                                                                       ioReactor.getConnectionInformation().getNonBlockingTpsCounter().getCounter()));
                            }
                        }
                    } else {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("SmppConnectionGroup",
                                              "getConnectedConnection",
                                              0,
                                              " : Warn : ",
                                              MessageFormat.format("Connection type is READ. Conn: {0}",
                                                                   this.getConnections()[connectionIndex].getConnectionName()));
                        }
                    }
                } else {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("SmppConnectionGroup",
                                          "getConnectedConnection",
                                          0,
                                          " : Warn : ",
                                          MessageFormat.format("Connection Sanity Check Fails: {0}, Conn: {1}",
                                                               this.getConnections()[connectionIndex].getConnectionInformation().getConnectionState(),
                                                               this.getConnections()[connectionIndex].getConnectionName()));
                    }
                }
            } else {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("SmppConnectionGroup", "getConnectedConnection", 0, " : Warn2 : ", "No connection found with at index " + connectionIndex);
                }
            }
        }

        return null;
    }

    public SmppIOReactor getConnectedConnection(final String connectionName,
                                                final int messageCount) throws SmppApiException {
        final SmppIOReactor smppIOReactor = this.ioStorageMap.get(connectionName);
        if (smppIOReactor == null) {
            throw new SmppApiException(SmppApiException.NOT_AVAILABLE, SmppApiException.DOMAIN_SMPP_CONNECTION, "host : "
                                                                                                                + connectionName
                                                                                                                + " not on the client list.");
        }
        if (smppIOReactor.checkConnectionSanity()) {
            if (smppIOReactor.increaseTps(messageCount)) {
                return smppIOReactor;
            }
        }
        return null;
    }

    public SmppIOReactor getConnection(final String connectionName) {
        return this.ioStorageMap.get(connectionName);
    }

    public boolean isThereAnyAliveConnection() {
        synchronized (this.getLockObject()) {
            SmppIOReactor ioReactor = null;
            Map.Entry<String, SmppIOReactor> mapCc = null;
            final Iterator<Map.Entry<String, SmppIOReactor>> iterConnection = this.ioStorageMap.entrySet().iterator();
            while (iterConnection.hasNext()) {
                mapCc = iterConnection.next();
                ioReactor = mapCc.getValue();
                if (ioReactor.checkConnectionSanity()) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getSize() {
        return this.ioStorageMap.size();
    }

    public void doAction(final EConnectionStateChange stateChange) {
        synchronized (this.getLockObject()) {
            IIOReactor ioReactor = null;
            Map.Entry<String, SmppIOReactor> mapCc = null;
            final Iterator<Map.Entry<String, SmppIOReactor>> iterConnection = this.ioStorageMap.entrySet().iterator();
            while (iterConnection.hasNext()) {
                mapCc = iterConnection.next();
                ioReactor = mapCc.getValue();
                switch (stateChange) {
                    case RESTART:
                        try {
                            ioReactor.restart();
                        } catch (final Exception e) {
                            this.logger.error("SmppConnectionGroup", "doAction", 0, null, " : Error : " + e.getMessage(), e);
                        }
                        break;
                    case SUSPEND:
                        try {
                            ioReactor.suspend();
                        } catch (final Exception e) {
                            this.logger.error("SmppConnectionGroup", "doAction", 0, null, " : Error : " + e.getMessage(), e);
                        }
                        break;
                    case UNSUSPEND:
                        try {
                            ioReactor.unSuspend();
                        } catch (final Exception e) {
                            this.logger.error("SmppConnectionGroup", "doAction", 0, null, " : Error : " + e.getMessage(), e);
                        }
                        break;
                    case SHUTDOWN:
                        try {
                            ioReactor.shutdown();
                        } catch (final Exception e) {
                            this.logger.error("SmppConnectionGroup", "doAction", 0, null, " : Error : " + e.getMessage(), e);
                        }
                        break;
                    case BIND:
                        break;
                    case UNBIND:
                        break;
                }
            }
            if (stateChange == EConnectionStateChange.SHUTDOWN) {
                this.ioStorageMap.clear();
                this.ioStorageMap = null;
                this.setConnections(null);
            }
        }
    }

    public void doAction(final String connectionName,
                         final EConnectionStateChange stateChange) {
        synchronized (this.getLockObject()) {
            final IIOReactor ioReactor = this.ioStorageMap.get(connectionName);
            if (ioReactor != null) {
                switch (stateChange) {
                    case RESTART:
                        try {
                            ioReactor.restart();
                        } catch (final Exception e) {
                            this.logger.error("SmppConnectionGroup", "doAction", 0, null, " : Error : " + e.getMessage(), e);
                        }
                        break;
                    case SUSPEND:
                        try {
                            ioReactor.suspend();
                        } catch (final Exception e) {
                            this.logger.error("SmppConnectionGroup", "doAction", 0, null, " : Error : " + e.getMessage(), e);
                        }
                        break;
                    case UNSUSPEND:
                        try {
                            ioReactor.unSuspend();
                        } catch (final Exception e) {
                            this.logger.error("SmppConnectionGroup", "doAction", 0, null, " : Error : " + e.getMessage(), e);
                        }
                        break;
                    case SHUTDOWN:
                        this.removeConnection(connectionName);
                        break;
                    case BIND:
                        break;
                    case UNBIND:
                        break;
                }
            }
        }
    }

    @Override
    public String toString() {
        return this.getConnectionGroupName();
    }

    public String getConnectionGroupName() {
        return this.connectionGroupName;
    }

    public void setConnectionGroupName(final String client) {
        this.connectionGroupName = client;
    }

    public SmppIOReactor[] getConnections() {
        return this.connections;
    }

    public void setConnections(final SmppIOReactor[] connections) {
        this.connections = connections;
    }

    public Object getLockObject() {
        return this.lockObject;
    }

    public void setLockObject(final Object lockObject) {
        this.lockObject = lockObject;
    }

    public StateHigherAuthority getStateHigherAuthority() {
        return this.stateHigherAuthority;
    }

    public void setStateHigherAuthority(final StateHigherAuthority stateHigherAuthority) {
        this.stateHigherAuthority = stateHigherAuthority;
    }

    public void setTps(final String connectionName,
                       final int tpsCount) {
        synchronized (this.getLockObject()) {
            final IIOReactor ioReactor = this.ioStorageMap.get(connectionName);
            if (ioReactor != null) {
                try {
                    ioReactor.adjustTps(tpsCount);
                } catch (final Exception e) {
                    this.logger.error("SmppConnectionGroup", "shutdown", 0, null, " : Error : " + e.getMessage(), e);
                }
            }
        }

    }

    public ConnectionInformation getConnectionInformation(final String connectionName) {
        synchronized (this.getLockObject()) {
            final IIOReactor ioReactor = this.ioStorageMap.get(connectionName);
            if (ioReactor == null) {
                return null;
            }
            return ioReactor.getConnectionInformation();
        }
    }

    public List<ConnectionInformation> getConnectionInformations() {
        final ArrayList<ConnectionInformation> connectionInformations = new ArrayList<ConnectionInformation>();

        synchronized (this.getLockObject()) {
            IIOReactor ioReactor = null;
            Map.Entry<String, SmppIOReactor> mapCc = null;
            final Iterator<Map.Entry<String, SmppIOReactor>> iterConnection = this.ioStorageMap.entrySet().iterator();
            while (iterConnection.hasNext()) {
                mapCc = iterConnection.next();
                ioReactor = mapCc.getValue();
                connectionInformations.add(ioReactor.getConnectionInformation());
            }

            return connectionInformations;
        }
    }
}
