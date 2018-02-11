package client;

import remoteobj.PubSubService;

import java.net.InetAddress;
import java.rmi.Naming;

/**
 * Created by balan016 on 2/8/18.
 */
public class Client {
    private int serverAssignedId;
    private PubSubService pubSubService;
    private UdpSubscriptionReceiver udpSubscriptionReceiver;
    private int udpListenerPort;

    public Client(){
        serverAssignedId = -1;
        udpListenerPort = -1;
        try {
            pubSubService = (PubSubService) Naming.lookup("//localhost/PubSubService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUdpListenerPort(int port) {
        udpListenerPort = port;
    }

    public void joinGroupServer(){
        if(serverAssignedId==-1){
            try {
                udpSubscriptionReceiver = new UdpSubscriptionReceiver(this);
                udpSubscriptionReceiver.start();
                serverAssignedId = pubSubService.join(InetAddress.getLocalHost(), udpListenerPort);
                if(serverAssignedId != -1){
                    System.out.println("Joined Group server. Id is "+serverAssignedId);
                    System.out.println("Client listening on port "+udpListenerPort);
                    //pubSubService.ping(serverAssignedId);
                    pubSubService.subscribe("Science", serverAssignedId);
                    pubSubService.publish("Science;Someone;UMN;contents");
                }
                else {
                    System.out.println("ERROR: Join unsuccessful. Maximum number of clients connected");
                    udpSubscriptionReceiver.closeSocket();
                }

            } catch (NullPointerException e) {
                System.out.println("ERROR: Join unsuccessful. Maximum number of clients connected");
                serverAssignedId = -1;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Client is already connected to group server. Id is "+serverAssignedId);
        }
    }

    public void leaveGroupServer(){
        if(serverAssignedId==-1){
            System.out.println("ERROR: No connection was previously established");
        }
        else{
            try {
                udpSubscriptionReceiver.closeSocket();
                pubSubService.leave(InetAddress.getLocalHost(),serverAssignedId); //TODO: fix this based on interface change
                System.out.println("Client has successfully disconnected");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
