package client;

import java.util.ArrayList;
import java.util.List;

public class ClientTest {
    private static final int TOTAL_CLIENTS = 10;

    private static final List<String> articleList = new ArrayList<>();

    private static final List<Client> clientList = new ArrayList<>(TOTAL_CLIENTS);

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

    public static void main(String[] args) {
        initializeArticleSet();
        initializeClients();
        for (int i = 0; i < TOTAL_CLIENTS; i++) {
            Client currentClient = clientList.get(i);
            currentClient.joinGroupServer();
            System.out.println("The current client Id is\t" + currentClient.getClientId());
        }
    }
}
