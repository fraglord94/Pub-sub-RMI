package remoteobj;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class WorkerThread implements Runnable{
    public int id;
    public WorkerThread(int id){
        this.id = id;
    }
    public void run() {
        //TODO: Read from queue and send the packets
        DatagramPacket packet = PubSubServiceImpl.sendQueue.poll();
        try{
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
        }
       catch (Exception e){
           System.out.println(e.getMessage());
       }

    }

}
