package com.adenon.api.smpp.logging;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LogDailyFile extends LogCreatorImpl {

    private String pattern      = "|%-9r|%d [%-20.20t] %-5p - %m%n";
    private String logDirectory = "";
    private String datePattern;
    private Level  level        = Level.INFO;

    @Override
    public LoggerWrapper getloggerImpl(final String logName) {
        try {
            final PatternLayout layout = new PatternLayout(this.getPattern());
            DailyRollingFileAppender appender = null;
            Path path = Paths.get(this.logDirectory, logName + ".log");
            if (this.datePattern != null) {
                appender = new DailyRollingFileAppender(layout, path.toString(), this.datePattern);
            } else {
                appender = new DailyRollingFileAppender(layout, path.toString(), "'.'yyyy-MM-dd");
            }

            appender.setName(logName + "DailyApp");
            appender.setImmediateFlush(true);
            final Logger logger = Logger.getLogger(logName);
            logger.setAdditivity(false);
            logger.addAppender(appender);
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

    public LogDailyFile setPattern(final String pattern) {
        this.pattern = pattern;
        return this;
    }

    public String getLogDirectory() {
        return this.logDirectory;
    }

    public LogDailyFile setLogDirectory(final String logDirectory) {
        this.logDirectory = logDirectory;
        return this;
    }

    public Level getLevel() {
        return this.level;
    }

    public LogDailyFile setLevel(final Level level) {
        this.level = level;
        return this;
    }

    public String getDatePattern() {
        return this.datePattern;
    }

    public void setDatePattern(final String datePattern) {
        this.datePattern = datePattern;
    }

}
