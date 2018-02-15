package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SenderThreadExecutorService extends Thread{
    @Override
    public void run(){
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < PubSubServiceImpl.NUM_QUEUES; i++) {
            Runnable worker = new PacketSenderWorkerThread(i);
            executor.execute(worker);
        }
    }
}
