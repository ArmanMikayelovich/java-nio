package com.energizeglobal.internship;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    private static final String LOCAL_HOST = "127.0.0.1";
    private static final int PORT = 8089;

    public static void main(String[] args) {
        try (SocketChannel client = SocketChannel.open(new InetSocketAddress(LOCAL_HOST, PORT))) {
            client.configureBlocking(false);
            String[] messages = new String[]{
                    "exit",
                    "exit",
                    "Message 3",
                    "Message 4"
            };

            System.out.println("Starting client");
            for (String msg : messages) {
                if (!isSocketClosed(client)) {
                    System.out.println("Prepared message: " + msg);
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    buffer.put(msg.getBytes());
                    buffer.flip();
                    if (client.isOpen()) {
                        System.out.println("Client still connected.");
                    }
                    try {
                        int bytesWriten = client.write(buffer);
                        System.out.println(String.format("Sending message: %s \n bufferBytes: %d", msg, bytesWriten));
                    } catch (IOException ex) {
                        System.out.println("Connection aborted.");
                        break;
                    }
                    buffer.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client finished his work.");
    }

    private static Boolean isSocketClosed(SocketChannel client) throws IOException {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(24);
         byteBuffer.flip();
        if (client.read(byteBuffer) !=0) {
            final String messageFromServer = new String(byteBuffer.asCharBuffer().array());
            if ("exit".equalsIgnoreCase(messageFromServer)) {
                client.close();
            }
        }
        return !client.isOpen();
    }
}
