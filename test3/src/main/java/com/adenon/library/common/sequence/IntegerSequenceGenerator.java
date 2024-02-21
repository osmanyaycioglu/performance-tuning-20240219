package com.adenon.library.common.sequence;


public class IntegerSequenceGenerator {

    public static final int     MAXIMUM_SEQUENCE_NUMBER = Integer.MAX_VALUE - 1;
    public static final int     MINIMUM_SEQUENCE_NUMBER = 100;


    private int                 sequenceNumber          = IntegerSequenceGenerator.MINIMUM_SEQUENCE_NUMBER;

    private static final Object lock                    = new Object();

    public IntegerSequenceGenerator() {

    }

    public int getNextIntegerSequenceNum() {
        synchronized (IntegerSequenceGenerator.lock) {
            this.sequenceNumber++;
            if (this.sequenceNumber > IntegerSequenceGenerator.MAXIMUM_SEQUENCE_NUMBER) {
                this.sequenceNumber = IntegerSequenceGenerator.MINIMUM_SEQUENCE_NUMBER;
            }
            return this.sequenceNumber;
        }
    }

}
