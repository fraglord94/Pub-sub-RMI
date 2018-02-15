package client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ClientTest {
    private static final int TOTAL_CLIENTS = 10;

    private static final List<String> articleList = new ArrayList<>();

    private static final List<Client> clientList = new ArrayList<>(TOTAL_CLIENTS);

    private static final Random random = new Random();

    static void initializeArticleSet() {
        articleList.add("“Sports;; contents");
        articleList.add("“;Someone;UMN; contents");
        articleList.add("Science;Someone;UMN;contents");
        articleList.add("“Science;;UMN;");
        articleList.add(";;;contents");

    }

    static void initializeClients() {
        for (int i = 0; i < TOTAL_CLIENTS; i++) {
            clientList.add(new Client());
        }
    }

    public void driver() throws NoSuchFieldException, IllegalAccessException {
        initializeClients();
        initializeArticleSet();
        for (int i = 0; i < TOTAL_CLIENTS; i++) {
            int randomIndex = random.nextInt(TOTAL_CLIENTS);
            String article = articleList.get(randomIndex);

            Client currentClient = clientList.get(i);
            System.out.println("Attempting to join. ");
            currentClient.joinGroupServer();
            System.out.println("The current client Id is\t" + currentClient.getClientId());

            Field field = currentClient.getClass().getDeclaredField("udpListenerPort");
            field.setAccessible(true);
            int udpListenerPort = (Integer) field.get(currentClient);
            System.out.println("The UDP listening port is\t" + udpListenerPort);

            System.out.println("Attempting to publish. ");
            currentClient.publish(article);
            System.out.println("Attempting to subscribe. ");
            currentClient.publish(article);

            System.out.println("Attempting to unsubscribe. ");
            currentClient.unsubscribe(article);

            System.out.println("Leaving the group server. ");
            currentClient.leaveGroupServer();
        }
    }
}
