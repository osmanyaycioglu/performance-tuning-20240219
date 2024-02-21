package com.adenon.api.smpp.core;

import java.nio.ByteBuffer;

import com.adenon.api.smpp.buffer.SmppBufferManager;
import com.adenon.api.smpp.common.CommonUtils;
import com.adenon.api.smpp.common.PDUParser;
import com.adenon.api.smpp.common.SmppApiException;


public class SmppPackageReader {

    public ByteBuffer readSmppPackage(final ByteBuffer pByteBuffer,
                                      final IIOReactor pIOReactor) throws Exception {
        synchronized (pIOReactor.getReadLock()) {
            int readCount = 0;
            pByteBuffer.clear();
            pByteBuffer.limit(4);
            do {
                if (pIOReactor.getSocketChannel() == null) {
                    return null;
                }
                readCount = pIOReactor.getSocketChannel().read(pByteBuffer);
                pIOReactor.setLastReadTime(System.currentTimeMillis());
                if (pIOReactor.getLogger().isDebugEnabled()) {
                    pIOReactor.getLogger().debug("SmppPackageReader", "readSmppPackage", 0, pIOReactor.getLabel(), "  Start to read socket.");
                }
                if (readCount == -1) {
                    pIOReactor.closeConnection("Connection closed.");
                    pIOReactor.getStatisticCollector().increaseTotalHostConnectionClose();
                    throw new SmppApiException(SmppApiException.FATAL_ERROR, "Connection Dead!");
                }
            } while (pByteBuffer.position() != 4);
            pByteBuffer.position(0);
            final int pduSize = pByteBuffer.getInt();
            if (pduSize > SmppBufferManager.MAX_PDU_SIZE) {
                pIOReactor.getLogger().error("SmppPackageReader",
                                             "readSmppPackage",
                                             0,
                                             pIOReactor.getLabel(),
                                             "  Error : pdu size is bigger than " + SmppBufferManager.MAX_PDU_SIZE + "!!! size : " + pduSize);
                throw new SmppApiException(SmppApiException.FATAL_ERROR, "PDU Size bigger than expected!");
            }
            pByteBuffer.limit(pduSize);
            do {
                readCount = pIOReactor.getSocketChannel().read(pByteBuffer);
                if (readCount == -1) {
                    pIOReactor.closeConnection("Connection closed.");
                    throw new SmppApiException(SmppApiException.FATAL_ERROR, "Connection Dead!");
                }
            } while (pByteBuffer.position() != (pduSize));
        }

        if (pIOReactor.isTraceON()) {
            if (pIOReactor.getLogger().isDebugEnabled()) {
                pIOReactor.getLogger().debug("SmppPackageReader",
                                             "readSmppPackage",
                                             0,
                                             null,
                                             "  [RECEIVED PDU] :" + CommonUtils.bytesToHexFormated(pByteBuffer) + "\n" + PDUParser.parsePDU(pByteBuffer));
            }
        }
        pByteBuffer.position(0);
        return pByteBuffer;
    }

}
