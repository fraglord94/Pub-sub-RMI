package server;

import javafx.util.Pair;
import remoteobj.PubSubService;

import java.net.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by balan016 on 2/8/18.
 */
public class PubSubServiceImpl extends UnicastRemoteObject implements PubSubService {
    private int MAXCLIENT = 10;

    private DatagramSocket datagramSocket; //for UDP connection to client
    private DatagramPacket[] datagramPackets = new DatagramPacket[MAXCLIENT];
    private BlockingQueue<Integer> availableIdQueue = new ArrayBlockingQueue<Integer>(MAXCLIENT);
    private static ConcurrentHashMap<Integer,Pair<InetAddress,Integer>> clientPortMap= new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String,Set<Integer>> tagSubscribersMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String,Set<Integer>> personSubscribersMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String,Set<Integer>> placeSubscriberMap = new ConcurrentHashMap<>();
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
        int leavingClientId = findClientId(ip,port);
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

    public void publish(String article, InetAddress ip, int port) throws RemoteException {
        String[] fields = article.trim().split(";");
        //TODO: Make set
        Set<Integer> clients = new HashSet<>();
        if(fields[0].trim() != "" && tagSubscribersMap.get(fields[0]) != null)
            clients.addAll(tagSubscribersMap.get(fields[0]));
        if(fields[1].trim() != "" && personSubscribersMap.get(fields[1]) != null)
            clients.addAll(personSubscribersMap.get(fields[1]));
        if(fields[2].trim() != "" && placeSubscriberMap.get(fields[2]) != null)
            clients.addAll(placeSubscriberMap.get(fields[2]));
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
    }

    public int subscribe(String article, InetAddress ip, int port) throws RemoteException {
        int clientId = findClientId(ip, port);
        String fields[] = article.trim().split(";");
        for(int i = 0; i < fields.length; i++){
            if(fields[i].trim() != "" && i == 0) {
                if(!tagSubscribersMap.containsKey(fields[0])){
                    tagSubscribersMap.put(fields[0],new HashSet<>());
                }
                tagSubscribersMap.get(fields[0]).add(clientId);
            }
            if(fields[i].trim() != "" && i == 1) {
                if(!personSubscribersMap.containsKey(fields[1])){
                    personSubscribersMap.put(fields[1],new HashSet<>());
                }
                personSubscribersMap.get(fields[1]).add(clientId);
            }
            if(fields[i].trim() != "" && i == 2) {
                if(!placeSubscriberMap.containsKey(fields[2])){
                    placeSubscriberMap.put(fields[2],new HashSet<>());
                }
                placeSubscriberMap.get(fields[2]).add(clientId);
            }
        }
        return 0;
    }

    private int findClientId(InetAddress ip, int port) {
        int clientId = -1;
        for(Map.Entry<Integer,Pair<InetAddress,Integer>> row : clientPortMap.entrySet()) {
            if(row.getValue().getKey().equals(ip) && row.getValue().getValue().equals(port)) {
                clientId = row.getKey();
            }
        }
        return clientId;
    }
}
