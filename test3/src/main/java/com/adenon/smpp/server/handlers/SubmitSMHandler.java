package com.adenon.smpp.server.handlers;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import com.adenon.api.smpp.message.MessageHeader;
import com.adenon.api.smpp.message.SubmitSMMessage;
import com.adenon.smpp.server.callback.response.ESubmitResult;
import com.adenon.smpp.server.callback.response.SubmitResponse;
import com.adenon.smpp.server.core.ServerIOReactor;

public class SubmitSMHandler {

	public static class SubmitHolder {
		private SubmitSMMessage submitSMMessage;
		private SubmitResponse submitResponse;

		public SubmitSMMessage getSubmitSMMessage() {
			return submitSMMessage;
		}

		public void setSubmitSMMessage(SubmitSMMessage submitSMMessage) {
			this.submitSMMessage = submitSMMessage;
		}

		public SubmitResponse getSubmitResponse() {
			return submitResponse;
		}

		public void setSubmitResponse(SubmitResponse submitResponse) {
			this.submitResponse = submitResponse;
		}

	}

	public static Map<String, SubmitSMMessage> map = new HashMap<>();

	public SubmitHolder handle(final MessageHeader header, final ByteBuffer byteBuffer, final ServerIOReactor ioReactor)
			throws Exception {
		if (ioReactor.getLogger().isDebugEnabled()) {
			ioReactor.getLogger().debug("SubmitSMHandler", "handle", header.getSequenceNo(), ioReactor.getLabel(),
					" SubmitSM recieved sequence : " + header.getSequenceNo() + " . Handling SubmitSM.");
		}

		final SubmitSMMessage submitSMMessage = new SubmitSMMessage(ioReactor.getLogger(), -1, ioReactor.getLabel());

		submitSMMessage.parseMessage(byteBuffer);
		final int size = byteBuffer.limit();
		final byte[] messageBytes = new byte[size];
		byteBuffer.position(0);
		byteBuffer.get(messageBytes, 0, size);
		submitSMMessage.setMessageBytes(messageBytes);

		final SubmitResponse submitResponse = ioReactor.getServerCallback()
				.submitSMReceived(ioReactor.getConnectionInformation(), submitSMMessage);
		SubmitHolder holder = new SubmitHolder();
		holder.setSubmitResponse(submitResponse);
		holder.setSubmitSMMessage(submitSMMessage);
		if (submitResponse.getSubmitResult() == ESubmitResult.DoNothing) {
			ioReactor.getLogger().debug("SubmitSMHandler", "handle", header.getSequenceNo(), ioReactor.getLabel(),
					" [DOING NOTHING] to : " + header.getSequenceNo() + " msgId : " + submitResponse.getMessageId()
							+ " Status : " + submitResponse.getSubmitResult().getValue() + "-"
							+ submitResponse.getSubmitResult().getDescription());
			check();
			return holder;
		}
		map.put("" + header.getSequenceNo(), submitSMMessage);

		ioReactor.sendSubmitSMResponse(header.getSequenceNo(), byteBuffer, submitResponse);
		if (ioReactor.getLogger().isDebugEnabled()) {
			ioReactor.getLogger().debug("SubmitSMHandler", "handle", header.getSequenceNo(), ioReactor.getLabel(),
					" SubmitSM Response sent sequence : " + header.getSequenceNo() + " msgId : "
							+ submitResponse.getMessageId() + " Status : " + submitResponse.getSubmitResult().getValue()
							+ "-" + submitResponse.getSubmitResult().getDescription());
		}
		check();
		return holder;
	}
	
	public void check() {
		process();
	}
	
	public void process() {
		getDelegation();
	}
	public void getDelegation() {
		try {
			Thread.sleep(10);
		} catch (Exception e) {
		}
		execute();
	}
	
	public void execute() {
		for (int i = 1; i < 400; i++) {
			SecureRandom random = new SecureRandom();
			random.nextInt(i);
		}
	}
	
}
