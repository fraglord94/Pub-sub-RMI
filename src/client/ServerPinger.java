package client;

import java.rmi.RemoteException;

public class ServerPinger extends Thread {
    private Client client;
    private UdpSubscriptionReceiver udpSubscriptionReceiver;
    public ServerPinger(Client client, UdpSubscriptionReceiver udpSubscriptionReceiver) {
        this.client = client;
        this.udpSubscriptionReceiver = udpSubscriptionReceiver;
    }

    public void run() {
        try{
            while(true){
                client.pingServer();
            }
        } catch (RemoteException e) {
            udpSubscriptionReceiver.closeSocket();
            System.out.println("ERROR: Lost connection to server. Client ending...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
