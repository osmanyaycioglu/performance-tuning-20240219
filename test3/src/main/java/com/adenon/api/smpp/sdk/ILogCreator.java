package com.adenon.api.smpp.sdk;

import com.adenon.api.smpp.logging.LoggerWrapper;


public interface ILogCreator {

    public LoggerWrapper getlogger(final String logName);

    public void addLogCreator(ILogCreator logCreator);
}
