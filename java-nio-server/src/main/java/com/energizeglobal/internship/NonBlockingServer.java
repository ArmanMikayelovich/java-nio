package com.energizeglobal.internship;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NonBlockingServer {
    private static Selector selector = null;
    private static final String LOCAL_HOST = "127.0.0.1";
    private static final int PORT = 8089;

    public static void main(String[] args) {


            try {
                selector = Selector.open();
                //We want ti set connection host, port
                // and non blocking mode
                ServerSocketChannel socket = ServerSocketChannel.open();
                ServerSocket serverSocket = socket.socket();
                serverSocket.bind(new InetSocketAddress(LOCAL_HOST, PORT));
                socket.configureBlocking(false);
                int ops = socket.validOps();
                socket.register(selector, ops, null);
                while (true) {
                    //FIXME erb vor Client- i koxmic SocketChannel@ close-a arvum, stex voch mi ban chi poxvum

                    selector.select();

                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectedKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        final boolean valid = key.isValid();
                        if (key.channel().isOpen()) {
                            System.out.println("Channel open for operations");
                        } else {
                            System.out.println("channel closed for operations");
                        }
                        if (key.isAcceptable()) {
                            //New client has been accepted
                            handleAccept(socket, key);
                        } else if (key.isReadable()) {
                            //We can run non-blocking operation READ on our client
                            handleRead(key);
                        }
                        iterator.remove();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

    }

    private static void handleAccept(ServerSocketChannel mySocket, SelectionKey key) throws IOException {
        System.out.println("Connection accepted...");

        //Accept the connection and set non-blocking mode
        SocketChannel client = mySocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    private static void handleRead(SelectionKey key) throws IOException {
        System.out.println("Reading...");

        //create a ServerSocketChannel to read the request

        SocketChannel client = (SocketChannel) key.channel();

        //Create buffer to read data
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        client.read(buffer);

        //Parse data from buffer to String
        String data = new String(buffer.array()).trim();

        if (data
                .length() > 0) {
            System.out.println("Received message: " + data);
            if ("exit".equalsIgnoreCase(data)) {
                client.close();
                System.out.println("Connection closed");
            }
        }
    }
}

