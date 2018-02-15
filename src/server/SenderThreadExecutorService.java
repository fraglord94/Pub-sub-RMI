package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SenderThreadExecutorService extends Thread{
    @Override
    public void run(){
        ExecutorService executor = Executors.newFixedThreadPool(50);
        for (int i = 0; i < 50; i++) {
            Runnable worker = new PacketSenderWorkerThread(i);
            executor.execute(worker);
        }
    }
}
