package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANTON,FERNANDO,LUKAS,ROBBY,SUDARSONO
 */
public class ClientServiceInterface extends Thread {

    private Processor processor;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientServiceInterface(Processor processor, Socket socket) {
        this.processor = processor;
        this.socket = socket;
    }

    private void listenClient(String clientRequest) throws NullPointerException {
        String[] reqSplit = clientRequest.split("-");
        if (reqSplit[0].equals("LOGOUT") && reqSplit.length == 2) {
            processor.getMessageQ().add(this, clientRequest);
        } else if (reqSplit[0].equals("LOGIN") && reqSplit.length == 3) {
            processor.getMessageQ().add(this, clientRequest);
        } else if (reqSplit[0].equals("REG") && reqSplit.length == 3) {
            processor.getMessageQ().add(this, clientRequest);
        } else {
            responClient("\"" + clientRequest + "\"" + " IS NOT RECOGNIZE.");
        }
    }

    public void responClient(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println("AUTENTICATION SERVER READY");
            boolean logout = false;
            while (true) {
                listenClient(in.readLine());
            }
        } catch (NullPointerException ex) {
            System.out.println("Client " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() 
                + " KICKED from server.");
            //Logger.getLogger(ClientServiceInterface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientServiceInterface.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientServiceInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
