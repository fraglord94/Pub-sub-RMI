package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.TimeUnit;

/**
 * Created by balan016 on 2/9/18.
 */
public class UdpSubscriptionReceiver extends Thread {
    private int clientSocket;
    private byte[] buf = new byte[1024];
    private boolean listening;

    UdpSubscriptionReceiver(int socket){
        clientSocket = socket;
        listening = false;
    }

    public boolean isListening() {
        return listening;
    }

    public void run(){
        try {
            System.out.println("Setting up port for client to listen...\n");
            DatagramSocket datagramSocket = new DatagramSocket(clientSocket);
            DatagramPacket datagramPacket = new DatagramPacket(buf, 1024);
            datagramSocket.receive(datagramPacket);
            System.out.println("UDP RECEIVE: "+new String(datagramPacket.getData(),0,datagramPacket.getLength()));
            datagramSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
