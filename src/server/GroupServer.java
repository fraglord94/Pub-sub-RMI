package server;

import remoteobj.PubSubServiceImpl;

import java.rmi.Naming;

/**
 * Created by balan016 on 2/8/18.
 */
public class GroupServer {
    public static void main(String[] args){
        try {
            System.setProperty("java.rmi.server.hostname","10.0.0.84");
            Naming.rebind("PubSubService",new PubSubServiceImpl());
            System.out.println("Remote Object bound and ready for use");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}