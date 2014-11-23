
import model.Listener;
import model.Processor;

/**
 *
 * @author ANTON,FERNANDO,LUKAS,ROBBY,SUDARSONO
 */
public class Main {
    
    public static void main(String[] args) {
//        String configFile = "authentication_server.cfg";
        String configFile = args[0];
        String serverConfFile = configFile; 
        
        Processor processor = new Processor();
        Listener listener = new Listener(serverConfFile, processor);
        
        listener.start();
        processor.start();
    }
}
