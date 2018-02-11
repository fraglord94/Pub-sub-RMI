package server;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
public class WorkerThread implements Runnable{
    public int id;
    public WorkerThread(int id){
        this.id = id;
    }
    public void run() {
       DatagramPacket packet = PubSubServiceImpl.sendQueue.poll();
        if(packet != null){
            System.out.println("Sending message: " + packet.toString() + " to client " + packet.getPort());
            try{
                DatagramSocket socket = new DatagramSocket();
                for(int i=0;i<5;i++){
                    socket.send(packet);
                }
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }


    }

}
