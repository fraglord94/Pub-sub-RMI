package client;

/**
 * Created by balan016 on 2/9/18.
 */
public class TestCase2 {
    public static void main(String[] args) {
        Client a = new Client();
        Client b = new Client();
        a.joinGroupServer();
        a.subscribe(";;UMN;");
        a.subscribe("Science;;;");

        //Invalid publish article
        a.publish(";;;contents");
        b.joinGroupServer();
        b.publish("Science;Someone;UMN;contents");
    }
}
