package remoteobj;

import remoteobj.PubSubService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by balan016 on 2/8/18.
 */
public class PubSubServiceImpl extends UnicastRemoteObject implements PubSubService {
    private int MAXCLIENT = 10;
    private BlockingQueue<Integer> availableIdQueue = new ArrayBlockingQueue<Integer>(MAXCLIENT);

    public PubSubServiceImpl() throws RemoteException {
        super();
        for(int i=0;i<MAXCLIENT;i++){
            availableIdQueue.offer(i);
        }
    }

    public int join() throws RemoteException {
        return availableIdQueue.poll();
    }

    public void leave(int id) throws RemoteException{
        availableIdQueue.offer(id);
    }

    public int publish() throws RemoteException {
        return 0;
    }

    public int subscribe() throws RemoteException {
        System.out.println("Subscribe called");
        return 0;
    }
}
