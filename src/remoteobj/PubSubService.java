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
    public boolean ping() throws RemoteException;
    public int publish(String article, InetAddress ip, int port) throws RemoteException;
    public int subscribe(String category, InetAddress ip, int port) throws RemoteException;
    public int unsubscribe(String category, InetAddress ip, int port) throws RemoteException;
}
