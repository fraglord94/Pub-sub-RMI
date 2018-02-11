package remoteobj;

import remoteobj.PubSubService;
import sun.reflect.annotation.ExceptionProxy;

import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by balan016 on 2/8/18.
 */
public class PubSubServiceImpl extends UnicastRemoteObject implements PubSubService {
    private int MAXCLIENT = 20;

    private DatagramSocket datagramSocket; //for UDP connection to client
    private DatagramPacket[] datagramPackets = new DatagramPacket[MAXCLIENT];
    private BlockingQueue<Integer> availableIdQueue = new ArrayBlockingQueue<Integer>(MAXCLIENT);
    private static ConcurrentHashMap<String,List<Integer>> map = new ConcurrentHashMap<>();
    public static ConcurrentLinkedQueue<DatagramPacket> sendQueue = new ConcurrentLinkedQueue<>();

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
        System.out.println(availableIdQueue.peek());
        return availableIdQueue.poll();
    }

    public void leave(int id) throws RemoteException{
        availableIdQueue.offer(id);
    }

    public void ping(int clientId) throws RemoteException{
        System.out.println("Ping request from "+clientId);
        int clientPort = 5000 + clientId;
        try {
            String message = "Hello dear client";
            datagramPackets[clientId] = new DatagramPacket(message.getBytes(),message.length(), InetAddress.getByName("127.0.0.1"), clientPort);
            System.out.println("Sending packet to "+clientId);
            datagramSocket.send(datagramPackets[clientId]);
            System.out.println("Packet sent to "+clientId);
        } catch (UnknownHostException e) {
            System.out.println("ERROR: Host name not valid");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int publish(String article) throws RemoteException {
        String[] fields = article.trim().split(";");
        List<Integer> clients = map.get(fields[0]);
        try{
            for(int client : clients){
                int clientPort = 5000 + client;
                DatagramPacket packet = new DatagramPacket(article.getBytes(),article.length(), InetAddress.getByName("127.0.0.1"), clientPort);
                sendQueue.offer(packet);
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int subscribe(String category, int clientId) throws RemoteException {
        System.out.println("Subscribe called");
        if(!map.containsKey(category)){
            map.put(category,new ArrayList<>());
        }
        map.get(category).add(clientId);
        return 0;
    }
    public int send(String article, int clientId) throws RemoteException{
        ExecutorService executor = Executors.newFixedThreadPool(50);
        for (int i = 0; i < 10; i++) {
            Runnable worker = new WorkerThread(i);
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("Finished all threads");
        return 0;
    }
}
