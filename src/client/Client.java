package client;

import remoteobj.PubSubService;

import java.rmi.Naming;

/**
 * Created by balan016 on 2/8/18.
 */
public class Client {
    public static void main (String[] args){
        try {
            PubSubService pubSubService = (PubSubService) Naming.lookup("//"+args[0]+"/PubSubService");
            int retval = pubSubService.publish();
            System.out.println("Returned "+retval);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
