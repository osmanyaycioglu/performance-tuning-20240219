package com.adenon.api.smpp.sdk;

import com.adenon.api.smpp.message.SubmitSMMessage;


public interface ISMSSendResult extends ISendResult {

    public SubmitSMMessage getMessage();

}
