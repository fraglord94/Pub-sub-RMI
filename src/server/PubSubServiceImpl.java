package server;

import javafx.util.Pair;
import remoteobj.PubSubService;

import java.net.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by balan016 on 2/8/18.
 */
public class PubSubServiceImpl extends UnicastRemoteObject implements PubSubService {
    private int MAXCLIENT = 1;

    private DatagramSocket datagramSocket; //for UDP connection to client
    private DatagramPacket[] datagramPackets = new DatagramPacket[MAXCLIENT];
    private BlockingQueue<Integer> availableIdQueue = new ArrayBlockingQueue<Integer>(MAXCLIENT);
    private static ConcurrentHashMap<Integer,Pair<InetAddress,Integer>> clientPortMap= new ConcurrentHashMap<>(); //TODO: also include IP in the map
    private static ConcurrentHashMap<String,List<Integer>> tagSubscribersMap = new ConcurrentHashMap<>();
    public static BlockingQueue<DatagramPacket> sendQueue = new ArrayBlockingQueue<>(500);

    public PubSubServiceImpl() throws RemoteException {
        super();
        SenderThreadExecutorService senderThreadExecutorService = new SenderThreadExecutorService();
        senderThreadExecutorService.start();
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
        clientPortMap.put(clientId,new Pair<>(ip,port));
        return clientId;
    }

    public void leave(InetAddress ip, int port) throws RemoteException{
        int leavingClientId = -1;
        for(Map.Entry<Integer,Pair<InetAddress,Integer>> row : clientPortMap.entrySet()) {
            if(row.getValue().getKey().equals(ip) && row.getValue().getValue().equals(port)) {
                leavingClientId = row.getKey();
            }
        }
        if(leavingClientId!=-1){
            availableIdQueue.offer(leavingClientId);
        }
    }

    public void ping(int clientId) throws RemoteException{
        System.out.println("Ping request from "+clientId);
        try {
            String message = "Hello dear client";
            datagramPackets[clientId] = new DatagramPacket(message.getBytes(),message.length(), clientPortMap.get(clientId).getKey(), clientPortMap.get(clientId).getValue());
            System.out.println("Sending packet to "+clientPortMap.get(clientId).getKey().toString()+" & Port:"+clientPortMap.get(clientId).getValue());
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
                DatagramPacket packet = new DatagramPacket(article.getBytes(),article.length(), clientPortMap.get(client).getKey(), clientPortMap.get(client).getValue());
                System.out.println("Added packet "+ packet.toString() +" to queue");
                sendQueue.offer(packet);
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int subscribe(String category, int clientId) throws RemoteException {
        if(!tagSubscribersMap.containsKey(category)){
            tagSubscribersMap.put(category,new ArrayList<>());
        }
        tagSubscribersMap.get(category).add(clientId);
        return 0;
    }
}
