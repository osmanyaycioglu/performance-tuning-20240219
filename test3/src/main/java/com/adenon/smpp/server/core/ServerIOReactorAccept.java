package com.adenon.smpp.server.core;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.adenon.api.smpp.core.IOReactorStorage;
import com.adenon.api.smpp.logging.LoggerWrapper;
import com.adenon.smpp.server.managers.ServerLogManager;

public class ServerIOReactorAccept extends Thread {

    private final LoggerWrapper       logger;
    private ServerSocket              smppServerSocket;
    private int                       index = 0;
    private final ServerApiProperties apiProperties;
    private final IOReactorStorage    smppIOReactorStorage;
    private final String              serverName;
    private final ServerApiDelegator  serverApiDelegator;

    public ServerIOReactorAccept(final String serverName,
                                 final ServerApiDelegator serverApiDelegator,
                                 final ServerApiProperties apiProperties,
                                 final LoggerWrapper logger,
                                 final IOReactorStorage smppIOReactorStorage) throws Exception {
        super("ServerIOReactorAccept");
        this.serverName = serverName;
        this.serverApiDelegator = serverApiDelegator;
        this.apiProperties = apiProperties;
        this.logger = logger;
        this.smppIOReactorStorage = smppIOReactorStorage;
    }

    @Override
    public void run() {
        ServerSocketChannel ssChannel = null;
        try {
            ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(true);
            ssChannel.socket().bind(new InetSocketAddress(this.getApiProperties().getPort()));
        } catch (final Exception e) {
            this.logger.error("ServerIOReactorAccept", "run", 0, null, " : Error : " + e.getMessage(), e);
            System.exit(0);
        }
        if (ssChannel == null) {
            System.exit(0);
        }
        while (true) {
            try {
                final SocketChannel socketChannel = ssChannel.accept();
                this.index++;
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("ServerIOReactorAccept", "run", 0, null, "New connection : "
                                                                              + this.index
                                                                              + " from : "
                                                                              + socketChannel.socket().getInetAddress());
                }
                socketChannel.socket().setKeepAlive(true);
                Socket clientSocket = socketChannel.socket();
                try {
                    final ServerLogManager logManager = this.serverApiDelegator.getLogManager();
                    LoggerWrapper clogger;
                    switch (logManager.getLogType()) {
                        case LogAllInOneFile:
                            clogger = logManager.getLogger();
                            break;
                        case LogConnectionGroupSeparetly:
                            clogger = logManager.getLogControler().getLogger(this.serverApiDelegator.getServerName());
                            break;
                        case LogConnectionsSeparetly:
                            clogger = logManager.getLogControler().getLogger(this.serverApiDelegator.getServerName());
                            break;
                        default:
                            clogger = this.logger;
                            break;
                    }

                    final ServerIOReactor serverIOReactor = new ServerIOReactor(clogger,
                                                                                this.serverName,
                                                                                this.serverApiDelegator,
                                                                                socketChannel,
                                                                                socketChannel.socket().getInetAddress().toString(),
                                                                                this.getApiProperties().getPort());
                    serverIOReactor.initialize();
                    this.smppIOReactorStorage.addSmppIOReactor(serverIOReactor);
                } catch (final OutOfMemoryError error) {
                    this.logger.error("ServerIOReactorAccept", "run", 0, null, " : Error : " + error.getMessage(), error);
                    if (clientSocket != null) {
                        clientSocket.close();
                        clientSocket = null;
                    }
                }
            } catch (final Exception e) {
                this.logger.error("ServerIOReactorAccept", "run", 0, null, " : Error : " + e.getMessage(), e);
            }
        }
    }

    public void shutdown() {
        try {
            this.smppServerSocket.close();
        } catch (final Exception exc) {
            exc.printStackTrace();
        }
    }

    public ServerApiProperties getApiProperties() {
        return this.apiProperties;
    }
}