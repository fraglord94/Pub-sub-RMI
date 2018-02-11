package remoteobj;

public class WorkerThread implements Runnable{
    public int id;
    public WorkerThread(int id){
        this.id = id;
    }
    public void run() {
        //TODO: Read from queue and send the packets
        PubSubServiceImpl.sendQueue.poll();
        System.out.println("Hello from thread " + id);

    }

}
