package okkero.util;

public class MockSender {

    String lastReceivedMessage;

    public void receive(String message) {
        lastReceivedMessage = message;
    }

}
