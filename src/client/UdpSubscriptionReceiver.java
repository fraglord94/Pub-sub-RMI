package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

/**
 * Created by balan016 on 2/9/18.
 */
public class UdpSubscriptionReceiver extends Thread {
    private byte[] buf = new byte[1024];
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private Client client;
    private boolean running;
    UdpSubscriptionReceiver(Client client){
        try {
            //System.out.println("Setting up port for client " + client.getClientId() + " to listen...\n");
            datagramSocket = new DatagramSocket();
            client.setUdpListenerPort(datagramSocket.getLocalPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
        datagramPacket = new DatagramPacket(buf, 1024);
        running = false;
        this.client = client;
    }

    public void closeSocket() {
        running = false;
        datagramSocket.close();
    }

    public void run(){
        running = true;
        try {
            while(running == true) {
                datagramSocket.receive(datagramPacket);
                System.out.println("UDP RECEIVE from CLIENT " + client.getClientId() +  " : " +new String(datagramPacket.getData(),0,datagramPacket.getLength()));
            }
        } catch (Exception e) {
            if(running != false) {
                e.printStackTrace();
            }
        }
    }
}
