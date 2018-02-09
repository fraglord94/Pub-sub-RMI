package remoteobj;

import remoteobj.PubSubService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by balan016 on 2/8/18.
 */
public class PubSubServiceImpl extends UnicastRemoteObject implements PubSubService {
    public PubSubServiceImpl() throws RemoteException {
        super();
    }

    public int publish() throws RemoteException {
        System.out.println("Publish called");
        return 0;
    }

    public int subscribe() throws RemoteException {
        System.out.println("Subscribe called");
        return 0;
    }
}
