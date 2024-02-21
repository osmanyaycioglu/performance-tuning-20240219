package com.adenon.library.common.sequence;


public class LongSequenceGenerator {

    public static final long    MAXIMUM_LONG_SEQUENCE_NUMBER = Long.MAX_VALUE - 1;
    public static final long    MINIMUM_LONG_SEQUENCE_NUMBER = 100;


    private long                sequenceNumber               = LongSequenceGenerator.MINIMUM_LONG_SEQUENCE_NUMBER;

    private static final Object lock                         = new Object();

    public LongSequenceGenerator() {

    }

    public long getNextLongSequenceNum() {
        synchronized (LongSequenceGenerator.lock) {
            this.sequenceNumber++;
            if (this.sequenceNumber > LongSequenceGenerator.MAXIMUM_LONG_SEQUENCE_NUMBER) {
                this.sequenceNumber = LongSequenceGenerator.MINIMUM_LONG_SEQUENCE_NUMBER;
            }
            return this.sequenceNumber;
        }
    }

}
