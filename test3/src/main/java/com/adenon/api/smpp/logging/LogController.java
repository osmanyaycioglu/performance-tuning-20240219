package com.adenon.api.smpp.logging;

import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.adenon.api.smpp.sdk.ILogCreator;
import com.adenon.api.smpp.sdk.LogDescriptor;

public class LogController {

    private static HashMap<String, LoggerWrapper> logWrappers = new HashMap<String, LoggerWrapper>();
    private LoggerWrapper                         loggerWrapper;
    private final String                          engineName;
    private final LogDescriptor                   logDescriptor;

    public LogController(final String instanceName,
                         final LogDescriptor logDescriptor) {
        this.engineName = instanceName;
        this.logDescriptor = logDescriptor;
    }

    public void initiliaze() {
        final Logger existLogger = LogManager.exists(this.engineName);
        if (existLogger != null) {
            final LoggerWrapper retlogger = new LoggerWrapper(existLogger);
            LogController.logWrappers.put(this.engineName, retlogger);
            this.setLogger(retlogger);
        } else {
            this.setLogger(this.getLogger(this.engineName));

        }

    }

    public LoggerWrapper getLogger(final String loggerName) {
        LoggerWrapper retlogger = LogController.logWrappers.get(loggerName);
        if (retlogger != null) {
            return retlogger;
        }
        ILogCreator logCreator = this.logDescriptor.getLogCreator();
        if (logCreator == null) {
            logCreator = new LogSizedFile().setLevel(this.logDescriptor.getLevel());
        }
        retlogger = logCreator.getlogger(loggerName);
        if (this.logDescriptor.isWriteConsole()) {
            retlogger = new LogToConsole().setLevel(this.logDescriptor.getLevel()).getlogger(loggerName);
        }
        LogController.logWrappers.put(loggerName, retlogger);
        return retlogger;
    }

    public void setLogLevel(final String loggerName,
                            final Level level) {
        final Logger retlogger = LogManager.exists(loggerName);
        if (retlogger != null) {
            retlogger.setLevel(level);
        }
    }

    public LoggerWrapper getLogger() {
        return this.loggerWrapper;
    }

    private void setLogger(final LoggerWrapper logger) {
        this.loggerWrapper = logger;
    }
}
