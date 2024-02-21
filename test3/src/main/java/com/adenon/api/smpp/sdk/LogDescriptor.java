package com.adenon.api.smpp.sdk;

import org.apache.log4j.Level;

public class LogDescriptor {

    private LogType     logType      = LogType.LogAllInOneFile;
    private Level       level        = Level.INFO;
    private ILogCreator logCreator;
    private boolean     writeConsole = false;

    public LogType getLogType() {
        return this.logType;
    }

    public LogDescriptor setLogType(final LogType logType) {
        this.logType = logType;
        return this;
    }

    public Level getLevel() {
        return this.level;
    }

    public LogDescriptor setLevel(final Level level) {
        this.level = level;
        return this;
    }

    public ILogCreator getLogCreator() {
        return this.logCreator;
    }

    public LogDescriptor setLogCreator(final ILogCreator logCreator) {
        this.logCreator = logCreator;
        return this;

    }

    public static LogDescriptor getDefaultLogDescriptor() {
        return new LogDescriptor();
    }

    public boolean isWriteConsole() {
        return this.writeConsole;
    }

    public LogDescriptor setWriteConsole(final boolean writeConsole) {
        this.writeConsole = writeConsole;
        return this;
    }

}
