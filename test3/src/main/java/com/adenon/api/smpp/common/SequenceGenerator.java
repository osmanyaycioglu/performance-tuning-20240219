package com.adenon.api.smpp.common;

public class SequenceGenerator {

    public static final int     MAX_SEQUENCE_NO     = 2147483640;
    public static final int     MIN_SEQUENCE_NO     = 100;

    public static final int     MAX_MSG_REF         = 254;
    public static final int     MIN_MSG_REF         = 1;

    public static final int     MAX_MSG_REF_BYTE    = 0xFF;
    public static final int     MIN_MSG_REF_BYTE    = 0x01;

    private static int          sequenceNumber      = SequenceGenerator.MIN_SEQUENCE_NO;
    private static int          referenceNumber     = SequenceGenerator.MIN_MSG_REF;
    private static int          referenceNumberByte = SequenceGenerator.MIN_MSG_REF_BYTE;

    private static final Object lockSequence        = new Object();
    private static final Object lockRefNum          = new Object();
    private static final Object lockRefNumByte      = new Object();

    private SequenceGenerator() {

    }

    public static int getNextSequenceNum() {
        synchronized (SequenceGenerator.lockSequence) {
            SequenceGenerator.sequenceNumber++;
            if (SequenceGenerator.sequenceNumber > SequenceGenerator.MAX_SEQUENCE_NO) {
                SequenceGenerator.sequenceNumber = SequenceGenerator.MIN_SEQUENCE_NO;
            }
            return SequenceGenerator.sequenceNumber;
        }
    }

    public static short getNextRefNum() {
        synchronized (SequenceGenerator.lockRefNum) {
            SequenceGenerator.referenceNumber++;
            if (SequenceGenerator.referenceNumber > SequenceGenerator.MAX_MSG_REF) {
                SequenceGenerator.referenceNumber = SequenceGenerator.MIN_MSG_REF;
            }
            return (byte) SequenceGenerator.referenceNumber;
        }
    }

    public static int getNextRefNumByte() {
        synchronized (SequenceGenerator.lockRefNumByte) {
            SequenceGenerator.referenceNumberByte++;
            if (SequenceGenerator.referenceNumberByte > SequenceGenerator.MAX_MSG_REF_BYTE) {
                SequenceGenerator.referenceNumberByte = SequenceGenerator.MIN_MSG_REF_BYTE;
            }
            return SequenceGenerator.referenceNumberByte;
        }
    }

}
