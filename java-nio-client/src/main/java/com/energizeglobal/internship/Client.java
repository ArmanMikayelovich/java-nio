package com.energizeglobal.internship;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    private static final String LOCAL_HOST = "127.0.0.1";
    private static final int PORT = 8089;

    public static void main(String[] args) {
        try( SocketChannel client =  SocketChannel.open(new InetSocketAddress(LOCAL_HOST, PORT))) {
            String[] messages = new String[]{
                    "Message 1",
                    "Message 2",
                    "Message 3"
            };

            System.out.println("Starting client");

            for (String msg : messages) {
                System.out.println("Prepared message: " + msg);
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                buffer.put(msg.getBytes());
                buffer.flip();
                int bytesWriten = client.write(buffer);
                System.out.println(String.format("Sending message: %s \n bufferBytes: %d", msg, bytesWriten));
                System.out.println("Client connection closed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client finished his work.");
    }
}
