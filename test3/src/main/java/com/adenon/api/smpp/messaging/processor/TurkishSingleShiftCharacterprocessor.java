package com.adenon.api.smpp.messaging.processor;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.SequenceGenerator;
import com.adenon.api.smpp.common.encoder.ICharacterEncoder;
import com.adenon.api.smpp.logging.LoggerWrapper;


public class TurkishSingleShiftCharacterprocessor extends CharacterProcessor {

    private int refNumber = -1;

    public TurkishSingleShiftCharacterprocessor(final ICharacterEncoder characterEncoder,
                                                final int normalMessageLength,
                                                final int concatMessageLength,
                                                final LoggerWrapper pLogger,
                                                final long pTransId,
                                                final String pLabel) {
        super(characterEncoder, normalMessageLength, concatMessageLength, pLogger, pTransId, pLabel);
    }

    @Override
    public void fillMessageBody(final ByteBuffer buffer,
                                final int index,
                                final byte[] concatHeader) throws Exception {
        byte[] conHeader = null;
        int headerLength = 0;
        if (this.getPartCount() > 1) {
            if (concatHeader != null) {
                conHeader = concatHeader;
            } else {
                if (this.refNumber == -1) {
                    this.refNumber = SequenceGenerator.getNextRefNumByte();
                }
                conHeader = this.getConcatHeader(this.getPartCount(), index + 1, this.refNumber);
            }
            headerLength += 5;
        }

        int smsLength = 0;

        if (this.concatMessagePointer.size() == 0) {
            buffer.put((byte) 0);
            return;
        }


        final MessagePartDescriptor messagePartDescriptor = this.concatMessagePointer.get(index);

        headerLength += 3; // For Single Shift Header

        smsLength = messagePartDescriptor.getLength() + headerLength + 1;
        buffer.put((byte) smsLength);
        buffer.put((byte) headerLength);
        if (conHeader != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("CharacterProcessor",
                                  "fillMessageBody",
                                  this.pTransId,
                                  this.pLabel,
                                  "Inserting Concat Header : " + CommonUtils.bytesToHex(concatHeader));
            }
            buffer.put(conHeader);
        }
        buffer.put(this.getSingleShiftHeader());
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("CharacterProcessor",
                              "fillMessageBody",
                              this.pTransId,
                              this.pLabel,
                              "Inserting Message Part : " + CommonUtils.bytesToHex(this.encodedBytes));
        }
        buffer.put(this.encodedBytes, messagePartDescriptor.getStart(), messagePartDescriptor.getLength());

    }

    public byte[] getConcatHeader(final int maxSMS,
                                  final int currentSMS,
                                  final int ref) {
        final byte[] totBytes = new byte[5];
        totBytes[0] = 0;
        totBytes[1] = 3;
        totBytes[2] = (byte) ref;
        totBytes[3] = (byte) maxSMS;
        totBytes[4] = (byte) currentSMS;
        return totBytes;
    }

    public byte[] getSingleShiftHeader() {
        return TurkishSingleShiftCharacterprocessor.singleSTR;
    }

    private static byte[] singleSTR = new byte[] { 0x24, 0x01, 0x01 };
}
