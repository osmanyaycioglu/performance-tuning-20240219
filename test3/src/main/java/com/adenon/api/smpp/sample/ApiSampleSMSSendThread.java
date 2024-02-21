package com.adenon.api.smpp.sample;

import com.adenon.api.smpp.SmppMessagingApi;
import com.adenon.api.smpp.sdk.AdditionalParamatersDescriptor;
import com.adenon.api.smpp.sdk.AddressDescriptor;
import com.adenon.api.smpp.sdk.TextMessageDescriptor;

public class ApiSampleSMSSendThread extends Thread {

	private final SmppMessagingApi smppMessagingApi;
	private final int count;
	private BlockingTpsCounter tpsCounter;
	private com.adenon.api.smpp.common.State state;

	private static class NumberGenerator {
		private static long numberStart = 905435420000L;

		public static synchronized String getNextNumber() {
			numberStart++;
			return "" + numberStart;
		}
	}

	public ApiSampleSMSSendThread(final SmppMessagingApi smppMessagingApi, final int count,
			BlockingTpsCounter tpsCounter, com.adenon.api.smpp.common.State state) {
		this.smppMessagingApi = smppMessagingApi;
		this.count = count;
		this.tpsCounter = tpsCounter;
		this.state = state;
	}

	@Override
	public void run() {
		for (int i = 0; i < this.count; i++) {
			try {
				this.tpsCounter.increase();
				if (state.waitIdle()) {
					this.smppMessagingApi.sendSms(null, TextMessageDescriptor.getASCIIMessageDescriptor("hello"), -1,
							AddressDescriptor.getDefaultInternationalAdressDescriptor(NumberGenerator.getNextNumber()),
							AddressDescriptor.getDefaultInternationalAdressDescriptor("905422082419"),
							new AdditionalParamatersDescriptor().setRequestDelivery(true), null);
				}
				System.out.println("%%%%%%%%%%% Sent : " + i + " TH : " + Thread.currentThread().getName());
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
}
