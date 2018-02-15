package client;

/**
 * Created by balan016 on 2/9/18.
 */
public class TestCase1 {
    public static void main(String[] args) {
        Client a = new Client();
        Client b = new Client();
        a.joinGroupServer();
        a.subscribe(";;UMN;");

        //Invalid subscribe article
        a.subscribe("; ; ;");
        a.subscribe("Science;;;");
        a.publish(";;;contents");

        //Invalid subscribe article
        a.unsubscribe("Sciencee;;;");
        b.joinGroupServer();
        b.publish("Science;Someone;UMN;contents");
    }
}
