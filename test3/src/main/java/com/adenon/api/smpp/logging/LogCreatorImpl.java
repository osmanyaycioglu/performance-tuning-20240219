package com.adenon.api.smpp.logging;

import java.util.ArrayList;

import com.adenon.api.smpp.sdk.ILogCreator;


public abstract class LogCreatorImpl implements ILogCreator {

    private ArrayList<ILogCreator> creators;

    public LogCreatorImpl() {
    }

    @Override
    public LoggerWrapper getlogger(final String logName) {
        if (this.creators != null) {
            for (final ILogCreator logCreator : this.creators) {
                logCreator.getlogger(logName);
            }
        }
        return this.getloggerImpl(logName);
    }

    @Override
    public synchronized void addLogCreator(final ILogCreator logCreator) {
        if (logCreator == null) {
            return;
        }
        if (this.creators == null) {
            this.creators = new ArrayList<ILogCreator>();
        }
        this.creators.add(logCreator);
    }

    public abstract LoggerWrapper getloggerImpl(String logName);

}
