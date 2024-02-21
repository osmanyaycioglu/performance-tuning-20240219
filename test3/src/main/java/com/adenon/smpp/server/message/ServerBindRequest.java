package com.adenon.smpp.server.message;

import com.adenon.api.smpp.message.BindRequestMessage;
import com.adenon.smpp.server.core.ServerIOReactor;


public class ServerBindRequest extends BindRequestMessage {

    private ServerIOReactor serverIOReactor;

    public ServerBindRequest() {
    }

    public ServerIOReactor getServerIOReactor() {
        return this.serverIOReactor;
    }

    public void setServerIOReactor(final ServerIOReactor serverIOReactor) {
        this.serverIOReactor = serverIOReactor;
    }
}
