package client;

/**
 * Created by balan016 on 2/9/18.
 */
public class ClientSimulator {
    public static void main(String[] args) {
        Client a = new Client();
        Client b = new Client();
        a.joinGroupServer();
//        a.subscribe(";Someone;;");
        a.publish("hello;;;contents");
        //a.leaveGroupServer();
        //b.joinGroupServer();
        //a.joinGroupServer();
//        a.leaveGroupServer();
    }
}
