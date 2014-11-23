package data;


import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import model.ClientServiceInterface;

/**
 *
 * @author ANTON,FERNANDO,LUKAS,ROBBY,SUDARSONO
 */
public class MessageQ {

    private Queue<Message> messages;

    public MessageQ() {
        this.messages = new LinkedList();
    }

    public synchronized void add(ClientServiceInterface client, String request) {
        this.messages.add(new Message(client, request));
        notifyAll();
    }

    public synchronized Message poll() {
        while (messages.size() == 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
                System.out.println("wait error " + ex.getMessage());
            }
        }
        notifyAll();
        return messages.poll();
    }

    public static class Message {

        private ClientServiceInterface client;
        private String request;

        public Message(ClientServiceInterface client, String request) {
            this.client = client;
            this.request = request;
        }

        public ClientServiceInterface getClient() {
            return client;
        }

        public String getRequest() {
            return request;
        }

    }
}
