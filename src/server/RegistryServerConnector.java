package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RegistryServerConnector {
    private boolean connected;
    private String registerString;
    private String deregisterString;
    private String getListString;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private byte[] buf = new byte[1024];

    public RegistryServerConnector() {
        connected = false;
        try {
            datagramSocket = new DatagramSocket();
            registerString = "[\"Register;RMI;" + InetAddress.getLocalHost().getHostAddress() + ";" + datagramSocket.getLocalPort() + ";PubSubService;1099\"]";
            deregisterString = "[\"Deregister;RMI;" + InetAddress.getLocalHost().getHostName() + ";1099\"]";
            getListString = "[\"GetList;RMI;" + InetAddress.getLocalHost().getHostName() + ";1099\"]";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean registerGroupServer() {
        try {
            datagramPacket = new DatagramPacket(registerString.getBytes(), registerString.length(), InetAddress.getByName("128.101.35.147"), 5105);
            datagramSocket.send(datagramPacket);
            System.out.println("Sent: " + registerString);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void communicate() {
        try {
            String message = "heartbeat";
            //datagramSocket.setSoTimeout(5000);
            while (true) {
                datagramPacket = new DatagramPacket(buf, 1024);
                datagramSocket.receive(datagramPacket);
                String receivedMessage = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                System.out.println("Received from server: " + receivedMessage);
                datagramPacket = new DatagramPacket(receivedMessage.getBytes(), receivedMessage.length(), InetAddress.getByName("128.101.35.147"), 5105);
                datagramSocket.send(datagramPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
