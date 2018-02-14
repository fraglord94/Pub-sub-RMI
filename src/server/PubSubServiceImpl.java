package server;

import javafx.util.Pair;
import remoteobj.PubSubService;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by balan016 on 2/8/18.
 */
public class PubSubServiceImpl extends UnicastRemoteObject implements PubSubService {
    private final static Logger LOGGER = Logger.getLogger(PubSubService.class.getName());

    static BlockingQueue<DatagramPacket> sendQueue = new ArrayBlockingQueue<>(500);
    private static ConcurrentHashMap<Integer, Pair<InetAddress, Integer>> clientPortMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Set<Integer>> tagSubscribersMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Set<Integer>> personSubscribersMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Set<Integer>> placeSubscriberMap = new ConcurrentHashMap<>();

    private final int MAX_CLIENT = 10;
    private final int MAX_STRING_LENGTH = 120;

    private DatagramSocket datagramSocket; //for UDP connection to client
    private DatagramPacket[] datagramPackets = new DatagramPacket[MAX_CLIENT];
    private BlockingQueue<Integer> availableIdQueue = new ArrayBlockingQueue<>(MAX_CLIENT);

    public PubSubServiceImpl() throws RemoteException {
        super();
        SenderThreadExecutorService senderThreadExecutorService = new SenderThreadExecutorService();
        senderThreadExecutorService.start();
        for (int i = 0; i < MAX_CLIENT; i++) {
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
        clientPortMap.put(clientId, new Pair<>(ip, port));
        return clientId;
    }

    public void leave(InetAddress ip, int port) throws RemoteException {
        int leavingClientId = findClientId(ip, port);
        if (leavingClientId != -1) {
            availableIdQueue.offer(leavingClientId);
        }
    }

    public void ping(int clientId) throws RemoteException {
        System.out.println("Ping request from " + clientId);
        try {
            String message = "Hello dear client";
            datagramPackets[clientId] = new DatagramPacket(message.getBytes(), message.length(), clientPortMap.get(clientId).getKey(), clientPortMap.get(clientId).getValue());
            System.out.println("Sending packet to " + clientPortMap.get(clientId).getKey().toString() + " & Port:" + clientPortMap.get(clientId).getValue());
            datagramSocket.send(datagramPackets[clientId]);
            System.out.println("Packet sent to " + clientId);
        } catch (UnknownHostException e) {
            System.out.println("ERROR: Host name not valid");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish(String article, InetAddress ip, int port) throws RemoteException {
        if (!validatePublishString(article))
            System.out.println("ERROR while validating publish string. ");
        String[] fields = article.trim().split(";");
        //TODO: Make set
        Set<Integer> clients = new HashSet<>();
        if (!Objects.equals(fields[0].trim(), "") && tagSubscribersMap.get(fields[0]) != null)
            clients.addAll(tagSubscribersMap.get(fields[0]));
        if (!Objects.equals(fields[1].trim(), "") && personSubscribersMap.get(fields[1]) != null)
            clients.addAll(personSubscribersMap.get(fields[1]));
        if (!Objects.equals(fields[2].trim(), "") && placeSubscriberMap.get(fields[2]) != null)
            clients.addAll(placeSubscriberMap.get(fields[2]));
        try {
            for (int client : clients) {
                DatagramPacket packet = new DatagramPacket(article.getBytes(), article.length(), clientPortMap.get(client).getKey(), clientPortMap.get(client).getValue());
                System.out.println("Added packet " + packet.toString() + " to queue");
                sendQueue.offer(packet);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public int subscribe(String article, InetAddress ip, int port) throws RemoteException {
        if (!validateSubscribeString(article))
            System.out.println("ERROR while validating subscribe string. ");
        int clientId = findClientId(ip, port);
        String fields[] = article.trim().split(";");
        for (int i = 0; i < fields.length; i++) {
            if (!Objects.equals(fields[i].trim(), "") && i == 0) {
                if (!tagSubscribersMap.containsKey(fields[0])) {
                    tagSubscribersMap.put(fields[0], new HashSet<>());
                }
                tagSubscribersMap.get(fields[0]).add(clientId);
            }
            if (!Objects.equals(fields[i].trim(), "") && i == 1) {
                if (!personSubscribersMap.containsKey(fields[1])) {
                    personSubscribersMap.put(fields[1], new HashSet<>());
                }
                personSubscribersMap.get(fields[1]).add(clientId);
            }
            if (!Objects.equals(fields[i].trim(), "") && i == 2) {
                if (!placeSubscriberMap.containsKey(fields[2])) {
                    placeSubscriberMap.put(fields[2], new HashSet<>());
                }
                placeSubscriberMap.get(fields[2]).add(clientId);
            }
        }
        return 0;
    }

    private int findClientId(InetAddress ip, int port) {
        int clientId = -1;
        for (Map.Entry<Integer, Pair<InetAddress, Integer>> row : clientPortMap.entrySet()) {
            if (row.getValue().getKey().equals(ip) && row.getValue().getValue().equals(port)) {
                clientId = row.getKey();
            }
        }
        return clientId;
    }

    private boolean validatePublishString(String category) {
        boolean valid = true;
        LOGGER.info("Beginning to validate the string. ");
        try {
            //TODO - is this correct ?
            if (category.getBytes().length >= MAX_STRING_LENGTH)
                throw new InvalidInputStringException("Publish String is of invalid size. ");
            String[] array = category.split(";");
            if (array.length <= 1)
                throw new InvalidInputStringException("Publish String does not have enough contents. ");
            try {
                if (array[array.length - 1] == null || array[array.length - 1].length() == 0)
                    throw new InvalidInputStringException("The contents field is null or empty. ");
            } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                throw new InvalidInputStringException("The contents field is null or empty. ");
            }
            if (array[0] == null || array[0].length() == 0)
                throw new InvalidInputStringException("The topic field is empty. ");
        } catch (InvalidInputStringException e) {
            valid = false;
            LOGGER.log(Level.SEVERE, "The client failed to publish due to: " + e.getMessage());
        }
        return valid;
    }

    private boolean validateSubscribeString(String category) {
        boolean valid = true;
        LOGGER.info("Beginning to validate the string. ");
        try {
            //TODO - is this correct ?
            if (category.getBytes().length >= MAX_STRING_LENGTH)
                throw new InvalidInputStringException("Subscribe String is of invalid size. ");
            String[] array = category.split(";");
            if (array[0] == null || array[0].length() == 0)
                throw new InvalidInputStringException("The subscribe field should have one of the first three fields filled. ");
            if (array.length > 3)
                throw new InvalidInputStringException("The subscribe string contains a Content field. ");
        } catch (InvalidInputStringException e) {
            valid = false;
            LOGGER.log(Level.SEVERE, "The client failed to publish due to: " + e.getMessage());
        }
        return valid;
    }
}
