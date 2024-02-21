package com.adenon.api.smpp.messaging.processor;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.common.CommonParameters;
import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.SmppApiException;
import com.adenon.api.smpp.common.encoder.CharacterEncoderASCII;
import com.adenon.api.smpp.common.encoder.CharacterEncoderGSM;
import com.adenon.api.smpp.common.encoder.CharacterEncoderSingleShiftTurkish;
import com.adenon.api.smpp.common.encoder.CharacterEncoderUC8;
import com.adenon.api.smpp.core.ResponseHandler;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.api.smpp.sdk.EDataCoding;
import com.adenon.api.smpp.sdk.EMessageType;
import com.adenon.api.smpp.sdk.IMessage;
import com.adenon.api.smpp.sdk.MessageInformation;
import com.adenon.api.smpp.sdk.TextMessageDescriptor;

public class TextSmsProcessor extends ResponseHandler {

    private String              smsText;
    private LoggerWrapper       logger;
    private String              label;
    private long                transID;
    private ICharacterProcessor smsCharacterProcessor;

    public TextSmsProcessor() {

    }

    public TextSmsProcessor(final String text,
                            final EDataCoding eDataCoding,
                            final LoggerWrapper pLogger,
                            final long pTransId,
                            final String pLabel) throws SmppApiException {
        this.processMessage(text, eDataCoding, pLogger, pTransId, pLabel);
    }

    public void processMessage(final String text,
                               final EDataCoding eDataCoding,
                               final LoggerWrapper pLogger,
                               final long pTransId,
                               final String pLabel) throws SmppApiException {
        this.transID = pTransId;
        this.label = pLabel;
        this.logger = pLogger;
        this.smsText = text;

        if (this.smsText == null) {
            this.smsText = "";
        }

        // public static final int DATA_CODING_SMSC_DEFAULT = 0x00;
        // public static final int DATA_CODING_ASCII = 0x01;
        // public static final int DATA_CODING_ISO_8859_1 = 0x03;
        // public static final int DATA_CODING_BINARY = 0x04;
        // public static final int DATA_CODING_UCS2 = 0x08;

        switch (eDataCoding) {
            case GSM_DEFAULT:
                final CharacterEncoderGSM characterEncoder = new CharacterEncoderGSM(false);
                this.smsCharacterProcessor = new CharacterProcessor(characterEncoder,
                                                                    CommonParameters.BYTE_COUNT_FOR_7BIT_SMS,
                                                                    CommonParameters.BYTE_COUNT_FOR_CONCAT_7BIT_SMS,
                                                                    pLogger,
                                                                    this.transID,
                                                                    this.label);
                break;
            case GSM_DEFAULT_WITH_ESCAPE:
                final CharacterEncoderGSM characterEncoderWithEscapeChars = new CharacterEncoderGSM(true);
                this.smsCharacterProcessor = new CharacterProcessor(characterEncoderWithEscapeChars,
                                                                    CommonParameters.BYTE_COUNT_FOR_7BIT_SMS,
                                                                    CommonParameters.BYTE_COUNT_FOR_CONCAT_7BIT_SMS,
                                                                    pLogger,
                                                                    this.transID,
                                                                    this.label);
                break;
            case ASCII:
            case ISO_8859_1:
                final CharacterEncoderASCII characterEncoderASCII = new CharacterEncoderASCII();
                this.smsCharacterProcessor = new CharacterProcessor(characterEncoderASCII,
                                                                    CommonParameters.BYTE_COUNT_FOR_7BIT_SMS,
                                                                    CommonParameters.BYTE_COUNT_FOR_CONCAT_7BIT_SMS,
                                                                    pLogger,
                                                                    this.transID,
                                                                    this.label);
                break;
            case TURKISH_SINGLE_SHIFT:
                final CharacterEncoderSingleShiftTurkish characterEncoderSingleShiftTurkish = new CharacterEncoderSingleShiftTurkish();
                this.smsCharacterProcessor = new TurkishSingleShiftCharacterprocessor(characterEncoderSingleShiftTurkish,
                                                                                      CommonParameters.BYTE_COUNT_FOR_7BIT_TURKISH_SINGLE_SHIFT_SMS,
                                                                                      CommonParameters.BYTE_COUNT_FOR_7BIT_CONCAT_TURKISH_SINGLE_SHIFT_SMS,
                                                                                      pLogger,
                                                                                      this.transID,
                                                                                      this.label);
                break;
            case UCS2:
                final CharacterEncoderUC8 characterEncoderUC8 = new CharacterEncoderUC8();
                this.smsCharacterProcessor = new CharacterProcessor(characterEncoderUC8,
                                                                    CommonParameters.BYTE_COUNT_FOR_UC8_SMS,
                                                                    CommonParameters.BYTE_COUNT_FOR_CONCAT_UC8_SMS,
                                                                    pLogger,
                                                                    this.transID,
                                                                    this.label);
                break;
            default:
                final CharacterEncoderGSM characterEncoderforDefault = new CharacterEncoderGSM();
                this.smsCharacterProcessor = new CharacterProcessor(characterEncoderforDefault,
                                                                    CommonParameters.BYTE_COUNT_FOR_7BIT_SMS,
                                                                    CommonParameters.BYTE_COUNT_FOR_CONCAT_7BIT_SMS,
                                                                    pLogger,
                                                                    this.transID,
                                                                    this.label);
                break;
        }

        if (this.smsCharacterProcessor != null) {
            this.smsCharacterProcessor.process(this.smsText);
            this.createHandler(this.smsCharacterProcessor.getPartCount());
        } else {
            throw new SmppApiException(SmppApiException.NULL, SmppApiException.DOMAIN_SMPP_CHARATER_PROCESS, "SMS Character Processor is null");
        }

    }

    @Override
    public void fillMessageBody(final ByteBuffer buffer,
                                final int index,
                                final byte[] concatHeader) throws Exception {
    	for (int i = 0; i < 500; i++) {
    		String bytesToHexFormated = CommonUtils.bytesToHexFormated(buffer);
    		if (bytesToHexFormated.contains("00AA00")){
    			System.out.println("hello there");
    		}
		}

        this.smsCharacterProcessor.fillMessageBody(buffer, index, concatHeader);
    }

    @Override
    public MessageInformation getMessageInformation(final IMessage messageDescriptor) {
        if (messageDescriptor == null) {
            return null;
        }
        final EMessageType messageType = messageDescriptor.getMessageType();

        if (messageType == EMessageType.SMSText) {

            try {
                final TextMessageDescriptor textMessageDescriptor = (TextMessageDescriptor) messageDescriptor;
                this.processMessage(textMessageDescriptor.getMessage(), textMessageDescriptor.getDataCoding(), null, -1, "null");

                final MessageInformation messageInformation = new MessageInformation();
                messageInformation.setMessageCount(this.smsCharacterProcessor.getPartCount());
                messageInformation.setByteCount(-1);
                return messageInformation;
            } catch (final Exception e) {

            }
        }
        return null;
    }

    // public TextSmsProcessor(String text,
    // int encodingType,
    // LoggerWrapper pLogger,
    // long pTransId,
    // String pLabel) throws SmppApiException {
    // this.transID = pTransId;
    // this.label = pLabel;
    // this.logger = pLogger;
    // this.smsText = text;
    // this.messageEncodingType = encodingType;
    //
    // if (this.smsText == null) {
    // this.smsText = "";
    // }
    // this.smsTextChars = this.smsText.toCharArray();
    //
    // final int encodingCode = (byte) ((byte) encodingType & (byte) 0x0f);
    //
    //
    // // Calculate msg parts
    //
    // final int msgMaxByteCount = CommonParameters.BYTE_COUNT_FOR_SMS;
    // double multiplier = 0.5;
    // if (encodingCode == CommonParameters.DATA_CODING_UCS2) {
    // multiplier = 0.5;
    // } else if (encodingCode == CommonParameters.DATA_CODING_BINARY) {
    // multiplier = 2;
    // if ((this.smsText.length() % 2) == 1) {
    // throw new SmppApiException(SmppApiException.FATAL_ERROR, SmppApiException.DOMAIN_IOREACTOR, "SMS Byte count is not odd : "
    // + this.smsText.length());
    // }
    // } else {
    // multiplier = (double) 8 / (double) 7;
    // }
    //
    // double remainBytes;
    // int charactersLeft;
    // int messageCounter = 0;
    // int reservedBytes;
    // int previousIndex = 0;
    // int msgRemainLength = this.smsText.length();
    // while (msgRemainLength > 0) {
    // reservedBytes = 0;
    // if (this.isConcatMsg) {
    // reservedBytes += 5;
    // } else {
    // final double actualLength = (msgRemainLength / multiplier);
    // int actualBytes = (int) actualLength;
    // if (actualLength > actualBytes) {
    // actualBytes++;
    // }
    // if ((reservedBytes + actualBytes) > msgMaxByteCount) {
    // this.isConcatMsg = true;
    // reservedBytes += 5;
    // }
    // }
    // if (reservedBytes > 0) {
    // reservedBytes++;
    // }
    // remainBytes = msgMaxByteCount - reservedBytes;
    // charactersLeft = (int) (remainBytes * multiplier);
    // if (encodingCode == CommonParameters.DATA_CODING_BINARY) {
    // if ((charactersLeft % 2) == 1) {
    // charactersLeft--;
    // }
    // }
    // if (charactersLeft >= msgRemainLength) {
    // final MessagePartDescriptor messagePartDescriptor = new MessagePartDescriptor();
    // messagePartDescriptor.setStart(previousIndex);
    // messagePartDescriptor.setEnd(previousIndex + msgRemainLength);
    // messagePartDescriptor.setLength(msgRemainLength);
    // this.concatMessagePointer.add(messagePartDescriptor);
    // msgRemainLength = 0;
    // previousIndex += msgRemainLength;
    // } else {
    // final MessagePartDescriptor messagePartDescriptor = new MessagePartDescriptor();
    // messagePartDescriptor.setStart(previousIndex);
    // messagePartDescriptor.setEnd(previousIndex + charactersLeft);
    // messagePartDescriptor.setLength(charactersLeft);
    // this.concatMessagePointer.add(messagePartDescriptor);
    // msgRemainLength -= charactersLeft;
    // previousIndex += charactersLeft;
    // }
    // messageCounter++;
    // }
    // this.createHandler(messageCounter);
    // }

}
