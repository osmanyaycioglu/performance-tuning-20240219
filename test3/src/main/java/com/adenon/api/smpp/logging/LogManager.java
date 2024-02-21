package com.adenon.api.smpp.logging;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Level;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.connection.SmppConnectionGroup;
import com.adenon.api.smpp.core.IIOReactor;
import com.adenon.api.smpp.core.SmppApiDelegator;
import com.adenon.api.smpp.core.SmppIOReactor;
import com.adenon.api.smpp.sdk.LogDescriptor;
import com.adenon.api.smpp.sdk.LogType;

public class LogManager {

    private final LoggerWrapper    logger;
    private LogType                logType = LogType.LogAllInOneFile;
    private final LogController    logControler;
    private Level                  rootlevel;
    private final String           apiEngineName;
    private final SmppApiDelegator smppApiDelegator;
    private final Object           syncObject;

    public LogManager(final String name,
                      final LogDescriptor descriptor,
                      final SmppApiDelegator smppApiDelegator,
                      final Object pSyncObject) {
        this.syncObject = pSyncObject;
        this.smppApiDelegator = smppApiDelegator;
        this.apiEngineName = name;
        this.logControler = new LogController(this.apiEngineName, descriptor);
        this.getLogControler().initiliaze();
        this.logger = this.getLogControler().getLogger();
        this.logType = descriptor.getLogType();

        if (this.getLogger().isInfoEnabled()) {
            this.getLogger().info("LogManager", "LogManager", 0, null, "LogManager initiated. System name : " + this.apiEngineName);
        }

    }

    public boolean changeLogLevel(final String connectionGroupName,
                                  final String connectionName,
                                  final Level level) {
        if (this.getLogger().isInfoEnabled()) {
            this.getLogger().info("LogManager",
                                  "changeLogLevel",
                                  0,
                                  CommonUtils.getClientHostLabel(connectionGroupName, connectionName),
                                  "Changing logging level for group : "
                                          + connectionGroupName
                                          + " connection : "
                                          + connectionName
                                          + " level : "
                                          + level.toString());
        }
        final SmppConnectionGroup connectionGroupController = this.smppApiDelegator.getConnectionGroupManager().get(connectionGroupName);
        if (connectionGroupController == null) {
            return false;
        }
        IIOReactor ioReactor = null;
        try {
            ioReactor = connectionGroupController.getConnection(connectionName);
            if (ioReactor == null) {
                return false;
            }
        } catch (final Exception e) {
            return false;
        }
        if (ioReactor.getLogger() != null) {
            ioReactor.getLogger().setLevel(level);
            if (this.getLogger().isInfoEnabled()) {
                this.getLogger().info("LogManager",
                                      "changeLogLevel",
                                      0,
                                      CommonUtils.getClientHostLabel(connectionGroupName, connectionName),
                                      "Log level changed for group : "
                                              + connectionGroupName
                                              + " connection : "
                                              + connectionName
                                              + " level : "
                                              + level.toString());
            }
        }
        return true;
    }

    public void changeLogLevel(final String connectionGroupName,
                               final Level level) {
        if (this.getLogger().isInfoEnabled()) {
            this.getLogger().info("LogManager",
                                  "changeLogLevel",
                                  0,
                                  CommonUtils.getClientHostLabel(connectionGroupName, null),
                                  "Changing log level for group : " + connectionGroupName + " LogLevel : " + level.toString());
        }
        synchronized (this.syncObject) {
            final SmppConnectionGroup customerConnect = this.smppApiDelegator.getConnectionGroupManager().get(connectionGroupName);
            if (customerConnect == null) {
                return;
            }
            final Iterator<Map.Entry<String, SmppIOReactor>> iteratorConnection = customerConnect.entries();
            SmppIOReactor ioReactor = null;
            while (iteratorConnection.hasNext()) {
                ioReactor = iteratorConnection.next().getValue();
                if (ioReactor.getLogger() != null) {
                    ioReactor.getLogger().setLevel(level);
                    if (this.getLogger().isInfoEnabled()) {
                        this.getLogger().info("LogManager",
                                              "changeLogLevel",
                                              0,
                                              CommonUtils.getClientHostLabel(connectionGroupName, ioReactor.getConnectionDescriptor().getConnectionName()),
                                              "Changing log level to : " + level.toString());
                    }
                }
            }
        }
    }

    public void changeLogType(final LogType pLogtype) {
        synchronized (this.syncObject) {
            try {
                final Iterator<Map.Entry<String, SmppConnectionGroup>> iteratorConnectionGroup = this.smppApiDelegator.getConnectionGroupManager()
                                                                                                                      .getConnectionGroupControllerIterator();
                SmppConnectionGroup customerConnect = null;
                while (iteratorConnectionGroup.hasNext()) {
                    customerConnect = iteratorConnectionGroup.next().getValue();
                    final Iterator<Map.Entry<String, SmppIOReactor>> iteratorConnection = customerConnect.entries();
                    SmppIOReactor ioReactor = null;
                    while (iteratorConnection.hasNext()) {
                        ioReactor = iteratorConnection.next().getValue();
                        switch (pLogtype) {
                            case LogAllInOneFile:
                                ioReactor.setLogger(this.logControler.getLogger());
                                break;
                            case LogConnectionGroupSeparetly:
                                ioReactor.setLogger(this.logControler.getLogger(customerConnect.getConnectionGroupName()));
                                break;
                            case LogConnectionsSeparetly:
                                ioReactor.setLogger(this.logControler.getLogger(customerConnect.getConnectionGroupName() + "_" + ioReactor.getConnectionName()));
                                break;
                            default:
                                break;
                        }
                        if (this.getLogger().isInfoEnabled()) {
                            this.getLogger().info("LogManager",
                                                  "changeLogType",
                                                  0,
                                                  CommonUtils.getClientHostLabel(customerConnect.getConnectionGroupName(), ioReactor.getConnectionDescriptor()
                                                                                                                                    .getConnectionName()),
                                                  "Log style changed to : " + pLogtype);
                        }

                    }
                }
            } catch (final Exception e) {
                this.getLogger().error("LogManager", "changeLogType", 0, null, " : Error : " + e.getMessage(), e);
            }
            this.logType = pLogtype;
        }
    }

    public void changeLogLevel(final Level level) {
        synchronized (this.syncObject) {
            try {
                final Iterator<Map.Entry<String, SmppConnectionGroup>> iteratorConnectionPool = this.smppApiDelegator.getConnectionGroupManager()
                                                                                                                     .getConnectionGroupControllerIterator();
                SmppConnectionGroup customerConnect = null;
                while (iteratorConnectionPool.hasNext()) {
                    customerConnect = iteratorConnectionPool.next().getValue();
                    final Iterator<Map.Entry<String, SmppIOReactor>> iteratorConnection = customerConnect.entries();
                    IIOReactor ioReactor = null;
                    while (iteratorConnection.hasNext()) {
                        ioReactor = iteratorConnection.next().getValue();
                        if (ioReactor.getLogger() != null) {
                            ioReactor.getLogger().setLevel(level);
                        }
                    }
                }
            } catch (final Exception e) {
                this.getLogger().error("LogManager", "changeLogLevel", 0, null, " : Error : " + e.getMessage(), e);
            }
        }
    }

    public boolean changeLogType(final String connectionGroupName,
                                 final String connectionName,
                                 final LogType pLogType) {
        if (this.getLogger().isInfoEnabled()) {
            this.getLogger().info("LogManager",
                                  "changeLogType",
                                  0,
                                  CommonUtils.getClientHostLabel(connectionGroupName, connectionName),
                                  "Changing log style changed to : " + pLogType);
        }
        final SmppConnectionGroup customerConnect = this.smppApiDelegator.getConnectionGroupManager().get(connectionGroupName);
        if (customerConnect == null) {
            return false;
        }
        SmppIOReactor ioReactor = null;
        try {
            ioReactor = customerConnect.getConnection(connectionName);
            if (ioReactor == null) {
                return false;
            }
        } catch (final Exception e) {
            return false;
        }
        switch (pLogType) {
            case LogAllInOneFile:
                ioReactor.setLogger(this.getLogControler().getLogger());
                break;
            case LogConnectionGroupSeparetly:
                ioReactor.setLogger(this.getLogControler().getLogger(customerConnect.getConnectionGroupName()));
                break;
            case LogConnectionsSeparetly:
                ioReactor.setLogger(this.getLogControler().getLogger(ioReactor.getConnectionName()));
                break;
            default:
                return false;
        }
        if (this.getLogger().isInfoEnabled()) {
            this.getLogger().info("LogManager",
                                  "changeLogType",
                                  0,
                                  CommonUtils.getClientHostLabel(connectionGroupName, connectionName),
                                  "Log style changed to : " + pLogType);
        }
        return true;
    }

    public LogType getLogType() {
        return this.logType;
    }

    public void setLogType(final LogType pLogType) {
        this.logType = pLogType;
    }

    public LogController getLogControler() {
        return this.logControler;
    }

    public LoggerWrapper getLogger() {
        return this.logger;
    }

    public Level getRootlevel() {
        return this.rootlevel;
    }

    public void setRootlevel(final Level rootlevel) {
        this.rootlevel = rootlevel;
    }

}
