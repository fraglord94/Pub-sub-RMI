package server.multipleQueues;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
public class PacketSenderWorkerThreadMultiQ implements Runnable{
    public int id;
    private DatagramSocket socket;
    public PacketSenderWorkerThreadMultiQ(int id){
        this.id = id;
    }
    public void run() {
        DatagramPacket packet = null;
        try {
            socket = new DatagramSocket();
            while(true){
                synchronized (PubSubServiceImplMultiQ.sendQueue){
                    if(PubSubServiceImplMultiQ.sendQueue[id] != null){
                        packet = PubSubServiceImplMultiQ.sendQueue[id].take();
                        System.out.println("Sending message: " + packet.toString() + " to client " + packet.getPort());
                        socket.send(packet);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
