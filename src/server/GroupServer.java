package server;

import java.rmi.Naming;

/**
 * Created by balan016 on 2/8/18.
 */
public class GroupServer {
    public static void main(String[] args){
        try {
            Naming.rebind("PubSubService",new PubSubServiceImpl());
            System.out.println("Remote Object bound and ready for use");
            RegistryServerConnector registryServerConnector = new RegistryServerConnector();
            registryServerConnector.registerGroupServer();
            registryServerConnector.communicate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}