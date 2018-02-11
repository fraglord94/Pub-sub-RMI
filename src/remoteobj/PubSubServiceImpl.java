package remoteobj;

import java.net.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
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
    private int MAXCLIENT = 1;

    private DatagramSocket datagramSocket; //for UDP connection to client
    private DatagramPacket[] datagramPackets = new DatagramPacket[MAXCLIENT];
    private BlockingQueue<Integer> availableIdQueue = new ArrayBlockingQueue<Integer>(MAXCLIENT);
    private ConcurrentHashMap<Integer,Integer> clientPortMap= new ConcurrentHashMap<>(); //TODO: also include IP in the map
    private static ConcurrentHashMap<String,List<Integer>> tagSubscribersMap = new ConcurrentHashMap<>();
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

    public int join(InetAddress ip, int port) throws RemoteException {
        int clientId = -1; //Java local variables are thread safe
        try {
            clientId = availableIdQueue.poll();
        } catch (NullPointerException e) {
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        clientPortMap.put(clientId,port);
        return clientId;
    }

    public void leave(InetAddress ip, int port) throws RemoteException{
        availableIdQueue.offer(port); //TODO: this interface is completely wrong. It deletes based on id - not port
    }

    public void ping(int clientId) throws RemoteException{
        System.out.println("Ping request from "+clientId);
        try {
            String message = "Hello dear client";
            datagramPackets[clientId] = new DatagramPacket(message.getBytes(),message.length(), InetAddress.getByName("127.0.0.1"), clientPortMap.get(clientId));
            System.out.println("Sending packet to "+clientPortMap.get(clientId));
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
        List<Integer> clients = tagSubscribersMap.get(fields[0]);
        try{
            for(int client : clients){
                DatagramPacket packet = new DatagramPacket(article.getBytes(),article.length(), InetAddress.getByName("127.0.0.1"), clientPortMap.get(client));
                System.out.println("Added packet "+ packet.toString() +" to queue");
                sendQueue.offer(packet);
                send();
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int subscribe(String category, int clientId) throws RemoteException {
        System.out.println("Subscribe called");
        if(!tagSubscribersMap.containsKey(category)){
            tagSubscribersMap.put(category,new ArrayList<>());
        }
        tagSubscribersMap.get(category).add(clientId);
        return 0;
    }
    public int send() throws RemoteException{
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
