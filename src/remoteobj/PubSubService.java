package remoteobj;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.stream.IntStream;

/**
 * Created by balan016 on 2/8/18.
 */
public interface PubSubService extends Remote{
    public int join(String IP, int port) throws RemoteException;
    public void leave(String IP, int port) throws RemoteException;
    public void ping(int clientId) throws RemoteException;
    public int publish(String article, String IP, int port) throws RemoteException;
    public int subscribe(String IP, int port, String article) throws RemoteException;
    public int unsubscribe(String IP, int port, String article) throws RemoteException;
    public int send() throws RemoteException;
}
