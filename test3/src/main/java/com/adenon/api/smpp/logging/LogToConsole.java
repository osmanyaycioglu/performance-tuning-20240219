package com.adenon.api.smpp.logging;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


public class LogToConsole extends LogCreatorImpl {

    private String pattern = "|%-9r|%d [%-20.20t] %-5p - %m%n";
    private Level  level   = Level.INFO;


    public LogToConsole() {
    }

    @Override
    public LoggerWrapper getloggerImpl(final String logName) {
        try {
            final PatternLayout layout = new PatternLayout(this.getPattern());
            final ConsoleAppender consoleAppender = new ConsoleAppender(layout);
            consoleAppender.setName(logName + "FileApp");
            consoleAppender.setImmediateFlush(true);
            final Logger logger = Logger.getLogger(logName);
            logger.setAdditivity(false);
            logger.addAppender(consoleAppender);
            logger.setLevel(this.getLevel());
            final LoggerWrapper retlogger = new LoggerWrapper(logger);
            return retlogger;
        } catch (final Exception e) {
            System.err.println(" : Error : " + e.getMessage());
            System.err.println(e);
        }
        return null;
    }


    public String getPattern() {
        return this.pattern;
    }


    public Level getLevel() {
        return this.level;
    }

    public LogToConsole setPattern(final String pattern) {
        this.pattern = pattern;
        return this;
    }

    public LogToConsole setLevel(final Level level) {
        this.level = level;
        return this;
    }

}
