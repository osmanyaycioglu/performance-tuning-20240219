package com.adenon.api.smpp.sdk;

public enum LogType {
    LogAllInOneFile(0),
    LogConnectionGroupSeparetly(1),
    LogConnectionsSeparetly(2);

    private int value;

    private LogType(final int val) {
        this.value = val;
    }

    public int getValue() {
        return this.value;
    }

    public static LogType getLogType(final int val) {
        switch (val) {
            case 0:
                return LogAllInOneFile;
            case 1:
                return LogConnectionGroupSeparetly;
            case 2:
                return LogConnectionsSeparetly;
            default:
                return LogAllInOneFile;
        }
    }

}
