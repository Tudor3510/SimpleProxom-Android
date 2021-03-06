package com.tudor.simpleproxom;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class SocketClass extends Thread {
    public static final int CONNECT = 0x01;
    public static final int VERIFY_CONNECTION = 0x02;

    private static final String MESSAGE_TO_SEND = "start~server";
    private static final int BROADCAST_PORT = 47777;
    private static final int MESSAGE_SERVER_PORT = 33776;
    private static final int SOCKET_TIMEOUT = 500;         //in milliseconds
    private static final int VERIFY_TRY_COUNT = 4;
    private static final int CONNECT_TRY_COUNT = 25;
    private static final String ALREADY_CONNECTED_MESSAGE = "Already Connected";
    private static final String CONNECTED_MESSAGE = "Connected";
    private static final String DISCONNECTED_MESSAGE = "Disconnected";
    private static final String GAME_INSTRUCTION_MESSAGE = "Open Among Us local games";
    private static final String THREAD_ALREADY_RUNNING_MESSAGE = "A connection attempt is running... \nTry again in a few seconds";
    private static final String ERROR_CREATE_SOCKET_MESSAGE = "Error: Could not create the socket";
    private static final String ERROR_TIMEOUT_SOCKET_MESSAGE = "Error: Could not set the timeout for socket";
    private static final String VERIFYING_CONNECTION_MESSAGE = "Verifying connection...";
    private static final String CONNECTING_MESSAGE = "Connecting...";
    private static final String ERROR_RESOLVE_ADDRESS = "Error: Could not resolve the address";
    private static final String ERROR_RECEIVE_PACKETS = "Error: Could not receive packets";
    private static final String ERROR_SEND_PACKETS = "Error: Could not send packets";
    private static final String ERROR_TIMEOUT = "The server did not respond";


    private static volatile int thread_count = 0;


    private String ipAddress = null;
    private DatagramSocket socket = null;

    private boolean justVerify = false;


    public SocketClass setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
        return this;
    }

    public SocketClass setAction(int ACTION_ID) {
        if (ACTION_ID == CONNECT)
            justVerify = false;
        if (ACTION_ID == VERIFY_CONNECTION)
            justVerify = true;

        return this;
    }


    @Override
    public void run() {
        thread_count++;
        if(thread_count > 1){
            setMessage("A connection attempt is running... \nTry again in a few seconds");

            thread_count--;
            return;
        }

        try {
            socket = new DatagramSocket(BROADCAST_PORT);
        } catch (SocketException e) {
            setMessage("Error: Could not create the socket");

            thread_count--;
            return;
        }

        try {
            socket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (SocketException e) {
            setMessage("Error: Could not set the timeout for socket");

            socket.close();
            thread_count--;
            return;
        }

        setMessage("Verifying connection...");


        String receivedMessage = null;
        for (int i=1; i<=VERIFY_TRY_COUNT && receivedMessage == null; i++) {
            byte[] receivedBuf = new byte[256];
            DatagramPacket receivedPacket = new DatagramPacket(receivedBuf, receivedBuf.length);

            try {
                socket.receive(receivedPacket);
                receivedMessage = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                if (!(receivedMessage.charAt(0) == 4 && receivedMessage.charAt(1) == 2 && receivedMessage.charAt(receivedMessage.length() - 1) == '~'))
                    receivedMessage = null;

            } catch (Exception e) {
                if (!(e instanceof SocketTimeoutException)) {
                    setMessage("Error: Could not receive packets");

                    socket.close();
                    thread_count--;
                    return;
                }
            }
        }

        if (receivedMessage != null){
            setMessage("Already connected");

            socket.close();
            thread_count--;
            return;
        }else if (justVerify){
            setMessage("Disconnected");

            socket.close();
            thread_count--;
            return;
        }

        setMessage("Connecting...");

        byte[] toSendBuf = MESSAGE_TO_SEND.getBytes(StandardCharsets.US_ASCII);

        InetAddress toSendAddress = null;
        try {
            toSendAddress = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            setMessage("Error: Could not resolve the address");

            socket.close();
            thread_count--;
            return;
        }
        DatagramPacket packetToSend = new DatagramPacket(toSendBuf, toSendBuf.length, toSendAddress, MESSAGE_SERVER_PORT);

        receivedMessage = null;
        for (int i = 0; i <= CONNECT_TRY_COUNT && receivedMessage == null; i++) {
            try {
                socket.send(packetToSend);
            } catch (Exception e) {
                if (!(e instanceof SocketTimeoutException)) {
                    setMessage("Error: Could not send packets");

                    socket.close();
                    thread_count--;
                    return;
                }
            }


            byte[] receivedBuf = new byte[256];
            DatagramPacket receivedPacket = new DatagramPacket(receivedBuf, receivedBuf.length);

            try {
                socket.receive(receivedPacket);
                receivedMessage = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                if (!(receivedMessage.charAt(0) == 4 && receivedMessage.charAt(1) == 2 && receivedMessage.charAt(receivedMessage.length() - 1) == '~'))
                    receivedMessage = null;
            } catch (Exception e) {
                if (!(e instanceof SocketTimeoutException)) {
                    setMessage("Error: Could not receive packets");

                    socket.close();
                    thread_count--;
                    return;
                }
            }

        }

        if (receivedMessage != null) {
            setMessage("Connected");
        } else {
            setMessage("The server did not respond");
        }

        socket.close();
        thread_count--;
    }
}