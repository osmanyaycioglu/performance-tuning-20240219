package com.adenon.api.smpp;

import org.apache.log4j.Level;

import com.adenon.api.smpp.core.SmppApiDelegator;
import com.adenon.api.smpp.sdk.LogType;


public class SmppLoggerApi {

    private final SmppApiDelegator smppApiDelegator;

    public SmppLoggerApi(final SmppApiDelegator smppApiDelegator) {
        this.smppApiDelegator = smppApiDelegator;
    }

    public void changeLogType(final LogType pLogtype) {
        this.smppApiDelegator.getSmppLoggingManager().changeLogType(pLogtype);
    }

    public void changeLogLevel(final Level level) {
        this.smppApiDelegator.getSmppLoggingManager().changeLogLevel(level);
    }

    public void changeLogLevel(final String connectionGroupName,
                               final String connectionName,
                               final Level level) {
        this.smppApiDelegator.getSmppLoggingManager().changeLogLevel(connectionGroupName, connectionName, level);
    }

    public void changeLogLevel(final String connectionGroupName,
                               final Level level) {
        this.smppApiDelegator.getSmppLoggingManager().changeLogLevel(connectionGroupName, level);
    }

    public void changeLogType(final String connectionGroupName,
                              final String connectionName,
                              final LogType pLogType) {
        this.smppApiDelegator.getSmppLoggingManager().changeLogType(connectionGroupName, connectionName, pLogType);
    }


}
