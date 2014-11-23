package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANTON,FERNANDO,LUKAS,ROBBY,SUDARSONO
 */
public class Listener extends Thread {

    private Processor processor;
    private String serverConfigFile;
    private int port;

    public Listener(String serverConfigFile, Processor processor) {
        this.serverConfigFile = serverConfigFile;
        this.processor = processor;
    }

    private void readConfigFile() throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(serverConfigFile));
        processor.setUserConfigFile(reader.readLine().toString().split("_=_")[1]);
        this.port = Integer.parseInt(reader.readLine().split("_=_")[1]);
    }

    public void run() {
        ServerSocket serverSocket;
        try {
            readConfigFile();
            serverSocket = new ServerSocket(port);
            System.out.println("Autentication Server start on port: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                processor.getClientL().add(socket);
                new ClientServiceInterface(processor, socket).start();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
