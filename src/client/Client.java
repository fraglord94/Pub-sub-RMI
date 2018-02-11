package client;

import remoteobj.PubSubService;

import java.rmi.Naming;
import java.util.concurrent.TimeUnit;

/**
 * Created by balan016 on 2/8/18.
 */
public class Client {
    private int serverAssignedId;
    private PubSubService pubSubService;
    private UdpSubscriptionReceiver udpSubscriptionReceiver;

    public Client(){
        serverAssignedId = -1;
        try {
            pubSubService = (PubSubService) Naming.lookup("//localhost/PubSubService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void joinGroupServer(){
        if(serverAssignedId==-1){
            try {
                serverAssignedId = pubSubService.join();
                System.out.println("Joined Group server. Id is "+serverAssignedId);
                int clientPort = Integer.parseInt("5000"+Integer.toString(serverAssignedId));
                udpSubscriptionReceiver = new UdpSubscriptionReceiver(clientPort);
                udpSubscriptionReceiver.start();
                TimeUnit.SECONDS.sleep(3);
                System.out.println("Client listening on port "+clientPort);
                //pubSubService.ping(serverAssignedId);
                pubSubService.subscribe("Science", serverAssignedId);
                pubSubService.publish("Science;Someone;UMN;contents");

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
                pubSubService.leave(serverAssignedId);
                System.out.println("Client has successfully disconnected");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
