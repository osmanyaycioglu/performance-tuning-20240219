package com.adenon.api.smpp.message;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.Smpp34Constants;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.api.smpp.sdk.SmppConnectionType;

public class BindRequestMessage {


    private String             systemIdentifier = null;
    private String             systemType       = null;
    private String             password         = null;
    private byte               interfaceVersion = Smpp34Constants.INTERFACE_VERSION;
    private byte               addressTon       = -1;
    private byte               addressNpi       = -1;
    private String             addressRange     = null;
    private SmppConnectionType connectionType   = SmppConnectionType.BOTH;
    private String             ip;

    public BindRequestMessage() {
    }

    public void parseMessage(final ByteBuffer byteBuffer) throws Exception {
        byte[] temp = new byte[50];
        int charCount = CommonUtils.getCOctetString(temp, byteBuffer);
        this.systemIdentifier = new String(temp, 0, charCount);
        charCount = CommonUtils.getCOctetString(temp, byteBuffer);
        this.password = new String(temp, 0, charCount);
        charCount = CommonUtils.getCOctetString(temp, byteBuffer);
        this.systemType = new String(temp, 0, charCount);
        this.setInterfaceVersion(byteBuffer.get());
        this.setAddressTon(byteBuffer.get());
        this.setAddressNpi(byteBuffer.get());
        CommonUtils.getCOctetString(temp, byteBuffer);
        this.setAddressRange(new String(temp, 0, charCount));
        temp = null;
    }

    public void fillBody(final ByteBuffer byteBuffer,
                         final int sequence) throws Exception {
        byteBuffer.position(4);
        switch (this.getConnectionType()) {
            case BOTH:
                byteBuffer.putInt(Smpp34Constants.MSG_BIND_TRANSCVR);
                break;
            case READ:
                byteBuffer.putInt(Smpp34Constants.MSG_BIND_RECEIVER);
                break;
            case WRITE:
                byteBuffer.putInt(Smpp34Constants.MSG_BIND_TRANSMITTER);
                break;
        }
        byteBuffer.putInt(0);
        byteBuffer.putInt(sequence);
        if ((this.getSystemIdentifier() == null) || (this.getPassword() == null)) {
            throw new SmppApiException(SmppApiException.FATAL_ERROR, "username or password is missing.");
        }
        byteBuffer.put(this.getSystemIdentifier().getBytes("ISO8859-1"));
        byteBuffer.put((byte) 0);
        byteBuffer.put(this.getPassword().getBytes("ISO8859-1"));
        byteBuffer.put((byte) 0);
        if (this.systemType != null) {
            byteBuffer.put(this.systemType.getBytes("ISO8859-1"));
        }
        byteBuffer.put((byte) 0);
        byteBuffer.put(this.interfaceVersion);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 1);
        if (this.getAddressRange() != null) {
            byteBuffer.put(this.getAddressRange().getBytes("ISO8859-1"));
        }
        byteBuffer.put((byte) 0);
        byteBuffer.putInt(0, byteBuffer.position());
    }

    public String getSystemIdentifier() {
        return this.systemIdentifier;
    }

    public void setSystemIdentifier(final String systemIdentifier) {
        this.systemIdentifier = systemIdentifier;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.getSystemIdentifier();
    }

    public byte getInterfaceVersion() {
        return this.interfaceVersion;
    }

    public void setInterfaceVersion(final byte interfaceVersion) {
        this.interfaceVersion = interfaceVersion;
    }

    public byte getAddressTon() {
        return this.addressTon;
    }

    public void setAddressTon(final byte addressTon) {
        this.addressTon = addressTon;
    }

    public byte getAddressNpi() {
        return this.addressNpi;
    }

    public void setAddressNpi(final byte addressNpi) {
        this.addressNpi = addressNpi;
    }

    public String getAddressRange() {
        return this.addressRange;
    }

    public void setAddressRange(final String addressRange) {
        this.addressRange = addressRange;
    }

    public SmppConnectionType getConnectionType() {
        return this.connectionType;
    }

    public void setConnectionType(final SmppConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public String toString() {
        return "BindRequestMessage [systemIdentifier="
               + this.systemIdentifier
               + ", password="
               + this.password
               + ", systemType="
               + this.systemType
               + ", interfaceVersion="
               + this.interfaceVersion
               + ", addressTon="
               + this.addressTon
               + ", addressNpi="
               + this.addressNpi
               + ", addressRange="
               + this.addressRange
               + ", connectionType="
               + this.connectionType
               + "]";
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(final String ip) {
        this.ip = ip;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }


}
