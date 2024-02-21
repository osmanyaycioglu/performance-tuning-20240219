package com.adenon.api.smpp.sdk;

import java.util.ArrayList;
import java.util.List;


public class RawMessages implements IRawMessages {

    private final List<IRawMessage> messages = new ArrayList<IRawMessage>();

    public RawMessages() {
    }

    public void addMessage(final IRawMessage message) {
        if (message != null) {
            this.messages.add(message);
        }
    }

    @Override
    public List<IRawMessage> getMessageList() {
        return this.messages;
    }

}
