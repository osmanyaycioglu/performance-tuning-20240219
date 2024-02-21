package com.adenon.api.smpp.messaging.processor;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.adenon.api.smpp.buffer.SendBufferObject;
import com.adenon.api.smpp.buffer.SmppBufferManager;
import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.SequenceGenerator;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.api.smpp.core.ResponseHandler;
import com.adenon.api.smpp.sdk.EMessageType;
import com.adenon.api.smpp.sdk.IMessage;
import com.adenon.api.smpp.sdk.MessageInformation;
import com.adenon.api.smpp.sdk.SIActionType;
import com.adenon.api.smpp.sdk.WapPushBookmarkMessageDescriptor;
import com.adenon.api.smpp.sdk.WapPushSIMessageDescriptor;
import com.adenon.api.smpp.sdk.WapPushSLMessageDescriptor;
import com.adenon.api.smpp.wappush.BookmarkMessage;
import com.adenon.api.smpp.wappush.IWapMessage;
import com.adenon.api.smpp.wappush.ServiceIndicationMessage;
import com.adenon.api.smpp.wappush.ServiceLoadingMessage;
import com.adenon.api.smpp.wappush.WDPPart;


public class WapPushProcessor extends ResponseHandler {


    private IWapMessage                            wapPushMessage;
    private final ArrayList<MessagePartDescriptor> concatMessagePointer = new ArrayList<MessagePartDescriptor>(10);

    public WapPushProcessor() {
    }

    public WapPushProcessor(final IMessage messageDescriptor) throws Exception {
        this.processMessage(messageDescriptor);
    }

    public void processMessage(final IMessage messageDescriptor) throws Exception {
        final SendBufferObject nextBufferObject = SmppBufferManager.getNextBufferObject();
        if (nextBufferObject == null) {
            throw new SmppApiException(SmppApiException.FATAL_ERROR, "Buffer object is null");
        }
        try {
            switch (messageDescriptor.getMessageType()) {
                case WAPPushSI:
                    final WapPushSIMessageDescriptor indicatorDescriptor = (WapPushSIMessageDescriptor) messageDescriptor;
                    this.wapPushMessage = new ServiceIndicationMessage(indicatorDescriptor);
                    break;
                case WAPPushSL:
                    final WapPushSLMessageDescriptor slMessageDescriptor = (WapPushSLMessageDescriptor) messageDescriptor;
                    this.wapPushMessage = new ServiceLoadingMessage(slMessageDescriptor);
                    break;
                case WAPBookmark:
                    final WapPushBookmarkMessageDescriptor bookmarkMessageDescriptor = (WapPushBookmarkMessageDescriptor) messageDescriptor;
                    this.wapPushMessage = new BookmarkMessage(bookmarkMessageDescriptor);
                    break;
                default:
                    break;
            }
            int messageCount = 0;
            final ByteBuffer byteBuffer = nextBufferObject.getByteBuffer();
            this.wapPushMessage.encode(byteBuffer);
            final int wspBytesLength = this.wapPushMessage.getWSPBytesLength();
            final int messageLength = byteBuffer.position();
            final int concatCapacityForFirstPart = WDPPart.getTotalBytesLength() - WDPPart.getUdhConcatBytesLength() - wspBytesLength;
            final int concatCapacityForOtherParts = WDPPart.getTotalBytesLength() - WDPPart.getUdhConcatBytesLength();
            final int capacityForOneMessage = WDPPart.getTotalBytesLength() - WDPPart.getUdhBytesLength() - wspBytesLength;
            if (messageLength > capacityForOneMessage) {
                messageCount = 1;
                final int afterFirstPart = messageLength - concatCapacityForFirstPart;
                int otherMessageCounts = (afterFirstPart / concatCapacityForOtherParts);
                final int leftOver = afterFirstPart % concatCapacityForOtherParts;
                if (leftOver > 0) {
                    otherMessageCounts++;
                }
                messageCount += otherMessageCounts;
            } else {
                messageCount = 1;
            }
            int startIndex = 0;
            int amount = 0;
            if (messageCount > 1) {
                amount = concatCapacityForFirstPart;
            } else {
                amount = capacityForOneMessage;
            }
            int endIndex;
            for (int i = 0; i < messageCount; i++) {
                if (i > 0) {
                    amount = concatCapacityForOtherParts;
                }
                if ((startIndex + amount) < messageLength) {
                    endIndex = startIndex + amount;
                } else {
                    endIndex = messageLength;
                }
                final MessagePartDescriptor messagePartDescriptor = new MessagePartDescriptor();
                messagePartDescriptor.setStart(startIndex);
                messagePartDescriptor.setEnd(endIndex);
                messagePartDescriptor.setLength((endIndex - startIndex));
                final byte[] myBytes = new byte[messagePartDescriptor.getLength()];
                byteBuffer.position(startIndex);
                byteBuffer.get(myBytes, 0, messagePartDescriptor.getLength());
                messagePartDescriptor.setByteArray(myBytes);
                this.concatMessagePointer.add(messagePartDescriptor);
                startIndex += amount;
            }
            this.createHandler(messageCount);
        } finally {
            SmppBufferManager.releaseBufferObject(nextBufferObject);
        }

    }

    @Override
    public void fillMessageBody(final ByteBuffer byteBuffer,
                                final int index,
                                final byte[] concatHeader) throws Exception {
        int messageIndex = 0;
        if (this.getMessagePartCount() > 1) {
            messageIndex = SequenceGenerator.getNextRefNumByte();
        }

        final MessagePartDescriptor messagePartDescriptor = this.concatMessagePointer.get(index);
        final int lengthpos = byteBuffer.position();
        byteBuffer.put((byte) (0xff));
        if (this.getMessagePartCount() == 1) {
            this.wapPushMessage.encodeUDHBytes(byteBuffer);
        } else {
            this.wapPushMessage.encodeUDHBytes(byteBuffer, this.getMessagePartCount(), index, messageIndex);
        }
        if (index == 0) {
            this.wapPushMessage.encodeWSPBytes(byteBuffer);
        }

        final byte[] byteArray = messagePartDescriptor.getByteArray();
        byteBuffer.put(byteArray);
        final int lastPosition = byteBuffer.position();
        byteBuffer.position(lengthpos);
        byteBuffer.put((byte) ((lastPosition - lengthpos - 1) & 0xff));
        byteBuffer.position(lastPosition);

    }

    @Override
    public MessageInformation getMessageInformation(final IMessage messageDescriptor) {
        if (messageDescriptor == null) {
            return null;
        }
        final EMessageType messageType = messageDescriptor.getMessageType();
        if ((messageType == EMessageType.WAPBookmark) || (messageType == EMessageType.WAPPushSL) || (messageType == EMessageType.WAPPushSI)) {
            try {
                this.processMessage(messageDescriptor);
                int totalBytes = (this.concatMessagePointer.size() - 1) * 140;
                if (this.concatMessagePointer.size() > 1) {
                    final MessagePartDescriptor messagePartDescriptor = this.concatMessagePointer.get(this.concatMessagePointer.size() - 1);
                    totalBytes += messagePartDescriptor.getLength() + WDPPart.getUdhConcatBytesLength();
                } else {
                    final MessagePartDescriptor messagePartDescriptor = this.concatMessagePointer.get(this.concatMessagePointer.size() - 1);
                    totalBytes += messagePartDescriptor.getLength() + WDPPart.getUdhBytesLength() + this.wapPushMessage.getWSPBytesLength();
                }

                final MessageInformation messageInformation = new MessageInformation();
                messageInformation.setMessageCount(this.concatMessagePointer.size());
                messageInformation.setByteCount(totalBytes);
                return messageInformation;
            } catch (final Exception e) {

            }
        }
        return null;
    }

    public static void main(final String[] args) {
        try {
            final WapPushBookmarkMessageDescriptor bookmarkMessageDescriptor = new WapPushBookmarkMessageDescriptor();
            bookmarkMessageDescriptor.setName("01234567890");
            String a = "0123456789";
            for (int i = 0; i < 1000; i++) {
                a += (i % 10);
                bookmarkMessageDescriptor.setUrl(a);
                final WapPushProcessor pushProcessor1 = new WapPushProcessor();
                final MessageInformation messageInformation = pushProcessor1.getMessageInformation(bookmarkMessageDescriptor);
                System.out.println(" MessageInformation : " + messageInformation.toString() + " i : " + i);
            }


            final WapPushSIMessageDescriptor indicatorDescriptor = new WapPushSIMessageDescriptor(SIActionType.SignalLow);
            indicatorDescriptor.setCreationDate(0);
            indicatorDescriptor.setHrefUrl("01234567890");
            indicatorDescriptor.setServiceIndicatorId("1120");
            indicatorDescriptor.setActionType(SIActionType.SignalLow);
            indicatorDescriptor.setSiExpiryDate(0);
            String b = "0123456789";
            for (int i = 0; i < 1000; i++) {
                b += (i % 10);
                indicatorDescriptor.setText(b);
                final WapPushProcessor pushProcessor1 = new WapPushProcessor();
                final MessageInformation messageInformation = pushProcessor1.getMessageInformation(indicatorDescriptor);
                System.out.println(" MessageInformation : " + messageInformation.toString() + " i : " + i);
            }


            final WapPushProcessor pushProcessor = new WapPushProcessor(indicatorDescriptor);
            for (int i = 0; i < pushProcessor.getMessagePartCount(); i++) {
                final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                pushProcessor.fillMessageBody(byteBuffer, i, null);
                System.out.println(byteBuffer);
                final byte[] arrayOfMsg = new byte[byteBuffer.position()];
                byteBuffer.position(0);
                byteBuffer.get(arrayOfMsg, 0, arrayOfMsg.length);
                final String convertByteStringToHex = CommonUtils.convertByteStringToHex(arrayOfMsg, 0, arrayOfMsg.length);
                System.out.println(convertByteStringToHex);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


}