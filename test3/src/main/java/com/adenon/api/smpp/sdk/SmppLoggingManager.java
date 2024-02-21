package com.adenon.api.smpp.sdk;

import org.apache.log4j.Level;

import com.adenon.api.smpp.logging.LogManager;


public class SmppLoggingManager {

    private final LogManager logManager;

    public SmppLoggingManager(final LogManager logManager) {
        this.logManager = logManager;
    }

    public void changeLogType(final LogType pLogtype) {
        this.logManager.changeLogType(pLogtype);
    }

    public void changeLogLevel(final Level level) {
        this.logManager.changeLogLevel(level);
    }

    public void changeLogLevel(final String connectionGroupName,
                               final String connectionName,
                               final Level level) {
        this.logManager.changeLogLevel(connectionGroupName, connectionName, level);
    }

    public void changeLogLevel(final String connectionGroupName,
                               final Level level) {
        this.logManager.changeLogLevel(connectionGroupName, level);
    }

    public void changeLogType(final String connectionGroupName,
                              final String connectionName,
                              final LogType pLogType) {
        this.logManager.changeLogType(connectionGroupName, connectionName, pLogType);
    }
}
