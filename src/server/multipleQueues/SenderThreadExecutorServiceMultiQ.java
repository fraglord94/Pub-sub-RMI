package server.multipleQueues;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SenderThreadExecutorServiceMultiQ extends Thread{
    @Override
    public void run(){
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < PubSubServiceImplMultiQ.NUM_QUEUES; i++) {
            Runnable worker = new PacketSenderWorkerThreadMultiQ(i);
            executor.execute(worker);
        }
    }
}
