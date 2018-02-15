package client;

public class ClientTestRunner {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            ClientTest clientTest = new ClientTest();
            try {
                clientTest.driver();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
