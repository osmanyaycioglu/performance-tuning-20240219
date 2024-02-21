package com.adenon.api.smpp.message;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.CommonUtils;

public class MessageHeader {

    private int commandLength = -1;
    private int commandID     = -1;
    private int commandStatus = -1;
    private int sequenceNo    = -1;

    public MessageHeader() {
    }

    public ByteBuffer parse(final ByteBuffer byteBuffer) {
        this.setCommandLength(byteBuffer.getInt());
        this.setCommandID(byteBuffer.getInt());
        this.setCommandStatus(byteBuffer.getInt());
        this.setSequenceNo(byteBuffer.getInt());
        return byteBuffer;
    }

    public ByteBuffer fill(final ByteBuffer byteBuffer) {
        byteBuffer.putInt(this.getCommandLength());
        byteBuffer.putInt(this.getCommandID());
        byteBuffer.putInt(this.getCommandStatus());
        byteBuffer.putInt(this.getSequenceNo());
        return byteBuffer;
    }

    public int getCommandLength() {
        return this.commandLength;
    }

    public void setCommandLength(final int commandLength) {
        this.commandLength = commandLength;
    }

    public int getCommandID() {
        return this.commandID;
    }

    public void setCommandID(final int commandID) {
        this.commandID = commandID;
    }

    public int getCommandStatus() {
        return this.commandStatus;
    }

    public void setCommandStatus(final int commandStatus) {
        this.commandStatus = commandStatus;
    }

    public int getSequenceNo() {
        return this.sequenceNo;
    }

    public void setSequenceNo(final int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    @Override
    public String toString() {
        return "Smpp34Header [commandLength="
               + this.commandLength
               + ", commandID=("
               + this.commandID
               + ")"
               + CommonUtils.getSmppCommandDescription(this.commandID)
               + ", commandStatus="
               + this.commandStatus
               + ", sequenceNo="
               + this.sequenceNo
               + "]";
    }

}
