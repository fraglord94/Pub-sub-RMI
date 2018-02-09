package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by balan016 on 2/9/18.
 */
public class UdpSubscriptionReceiver extends Thread {
    private int clientSocket;
    private byte[] buf = new byte[1024];

    UdpSubscriptionReceiver(int socket){
        clientSocket = socket;
    }

    public void run(){
        try {
            System.out.println("Thread running\n");
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
