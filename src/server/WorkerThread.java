package server;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
public class WorkerThread implements Runnable{
    public int id;
    private DatagramSocket socket;
    public WorkerThread(int id){
        this.id = id;
    }
    public void run() {
        DatagramPacket packet = null;
        try {
            socket = new DatagramSocket();
            while(true){
                packet = PubSubServiceImpl.sendQueue.take();
                System.out.println("Sending message: " + packet.toString() + " to client " + packet.getPort());
                socket.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
