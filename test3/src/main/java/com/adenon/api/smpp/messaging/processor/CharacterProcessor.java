package com.adenon.api.smpp.messaging.processor;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.encoder.ICharacterEncoder;
import com.adenon.api.smpp.logging.LoggerWrapper;

public class CharacterProcessor implements ICharacterProcessor {

    protected byte[]                                 encodedBytes;
    protected int                                    partCount;
    protected final ArrayList<MessagePartDescriptor> concatMessagePointer = new ArrayList<MessagePartDescriptor>(10);
    protected ICharacterEncoder                      characterEncoder;
    protected int                                    normalMessageLength;
    protected int                                    concatMessageLength;
    protected LoggerWrapper                          logger;
    protected long                                   pTransId;
    protected String                                 pLabel;

    public CharacterProcessor(final ICharacterEncoder characterEncoder,
                              final int normalMessageLength,
                              final int concatMessageLength,
                              final LoggerWrapper pLogger,
                              final long pTransId,
                              final String pLabel) {

        this.characterEncoder = characterEncoder;
        this.normalMessageLength = normalMessageLength;
        this.concatMessageLength = concatMessageLength;
        this.logger = pLogger;
        this.pTransId = pTransId;
        this.pLabel = pLabel;
    }

    @Override
    public void process(final String str) {
        this.encodedBytes = this.characterEncoder.encode(str);
        final int length = this.encodedBytes.length;
        if (length > this.normalMessageLength) {
            final int mCounter = length / this.concatMessageLength;
            this.setPartCount(mCounter);
            if ((length - (mCounter * this.concatMessageLength)) > 0) {
                this.setPartCount(this.getPartCount() + 1);
            }
            for (int i = 0; i < this.partCount; i++) {
                final int start = i * this.concatMessageLength;
                int end = 0;
                int partLength = 0;
                if (i == (this.partCount - 1)) {
                    end = length;
                    partLength = end - start;
                } else {
                    end = start + this.concatMessageLength;
                    partLength = this.concatMessageLength;
                }
                final MessagePartDescriptor messagePartDescriptor = new MessagePartDescriptor();
                messagePartDescriptor.setStart(start);
                messagePartDescriptor.setEnd(end);
                messagePartDescriptor.setLength(partLength);
                this.concatMessagePointer.add(messagePartDescriptor);
            }
        } else {
            this.setPartCount(1);
            final MessagePartDescriptor messagePartDescriptor = new MessagePartDescriptor();
            messagePartDescriptor.setStart(0);
            messagePartDescriptor.setEnd(this.encodedBytes.length);
            messagePartDescriptor.setLength(this.encodedBytes.length);
            this.concatMessagePointer.add(messagePartDescriptor);
        }
    }


    @Override
    public void fillMessageBody(final ByteBuffer buffer,
                                final int index,
                                final byte[] concatHeader) throws Exception {
        int headerLength = 0;
        if (concatHeader != null) {
            headerLength += concatHeader.length;
        }
        int smsLength = 0;

        if (this.concatMessagePointer.size() == 0) {
            buffer.put((byte) 0);
            return;
        }

        final MessagePartDescriptor messagePartDescriptor = this.concatMessagePointer.get(index);

        smsLength = messagePartDescriptor.getLength();
        smsLength += headerLength;
        buffer.put((byte) smsLength);
        if (headerLength > 0) {
            buffer.put((byte) headerLength);
            if (concatHeader != null) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("CharacterProcessor",
                                      "fillMessageBody",
                                      this.pTransId,
                                      this.pLabel,
                                      "Inserting Concat Header : " + CommonUtils.bytesToHex(concatHeader));
                }
                buffer.put(concatHeader);
            }
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("CharacterProcessor",
                              "fillMessageBody",
                              this.pTransId,
                              this.pLabel,
                              "Inserting Message Part : " + CommonUtils.bytesToHex(this.encodedBytes));
        }
        buffer.put(this.encodedBytes, messagePartDescriptor.getStart(), messagePartDescriptor.getLength());

    }

    @Override
    public int getPartCount() {
        return this.partCount;
    }

    public void setPartCount(final int partCount) {
        this.partCount = partCount;
    }
}
