package remoteobj;

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by balan016 on 2/8/18.
 */
public interface PubSubService extends Remote{
    public int join(InetAddress ip, int port) throws RemoteException;
    public void leave(InetAddress ip, int port) throws RemoteException;
    public void ping(int clientId) throws RemoteException;
    public int publish(String article) throws RemoteException;
    public int subscribe(String category, int clientId) throws RemoteException;
    public int send() throws RemoteException;
}
