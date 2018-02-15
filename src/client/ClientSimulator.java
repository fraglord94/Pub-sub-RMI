package client;

/**
 * Created by balan016 on 2/9/18.
 */
public class ClientSimulator {
    public static void main(String[] args) {
        Client a = new Client();
        Client b = new Client();
        a.joinGroupServer();
        a.subscribe(";;UMN;");
        a.subscribe("; ; ;");
        a.subscribe("Science;;;");
        a.publish(";;;contents");
        a.unsubscribe("Sciencee;;;");
        b.joinGroupServer();
        b.publish("Science;Someone;UMN;contents");
        //a.leaveGroupServer();
        //a.leaveGroupServer();
        //b.joinGroupServer();
        //a.joinGroupServer();
//        a.leaveGroupServer();
    }
}
