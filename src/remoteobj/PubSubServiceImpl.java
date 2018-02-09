package remoteobj;

import remoteobj.PubSubService;
import sun.reflect.annotation.ExceptionProxy;

import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by balan016 on 2/8/18.
 */
public class PubSubServiceImpl extends UnicastRemoteObject implements PubSubService {
    private int MAXCLIENT = 10;
    private DatagramSocket datagramSocket; //for UDP connection to client
    private DatagramPacket[] datagramPackets = new DatagramPacket[MAXCLIENT];
    private BlockingQueue<Integer> availableIdQueue = new ArrayBlockingQueue<Integer>(MAXCLIENT);

    public PubSubServiceImpl() throws RemoteException {
        super();
        for(int i=0;i<MAXCLIENT;i++){
            availableIdQueue.offer(i);
        }
        try {
            datagramSocket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int join() throws RemoteException {
        return availableIdQueue.poll();
    }

    public void leave(int id) throws RemoteException{
        availableIdQueue.offer(id);
    }

    public void ping(int clientId) throws RemoteException{
        System.out.println("Ping request from "+clientId);
        int clientPort = Integer.parseInt("5000"+Integer.toString(clientId));
        try {
            String message = "Hello dear client";
            datagramPackets[clientId] = new DatagramPacket(message.getBytes(),message.length(), InetAddress.getByName("127.0.0.1"), clientPort);
            datagramSocket.send(datagramPackets[clientId]);
        } catch (UnknownHostException e) {
            System.out.println("ERROR: Host name not valid");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int publish() throws RemoteException {
        return 0;
    }

    public int subscribe() throws RemoteException {
        System.out.println("Subscribe called");
        return 0;
    }
}
