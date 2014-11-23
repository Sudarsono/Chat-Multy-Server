package model;

import data.MessageQ;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANTON,FERNANDO,LUKAS,ROBBY,SUDARSONO
 */
public class Processor extends Thread {

    private LinkedList<Socket> clientL;
    private ArrayList<String> loggedUser;
    private MessageQ messageQ;
    private String userConfigFile;

    public Processor() {
        this.messageQ = new MessageQ();
        this.clientL = new LinkedList();
        this.loggedUser = new ArrayList();
    }

    public void setUserConfigFile(String userConfigFile) {
        this.userConfigFile = userConfigFile;
    }

    public LinkedList<Socket> getClientL() {
        return this.clientL;
    }

    public MessageQ getMessageQ() {
        return messageQ;
    }

    private boolean isExist(String userName) {
        boolean exist = false;
        BufferedReader reader = null;
        FileReader fileReader = null;
        try {
            reader = new BufferedReader(fileReader = new FileReader(userConfigFile));
            String line = reader.readLine();
            while (line != null && exist == false) {
                String[] lineSplit = line.split("-");
                if (lineSplit.length >= 3 && lineSplit[0].equals("register")
                        && lineSplit[1].equals(userName)) {
                    exist = true;
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return exist;
    }

    private int loggedInIndex(String username) {
        int loggedIn = -1;
        int i = 0;
        int length = loggedUser.size();
        while (loggedIn == -1 && i < length) {
            if (username.equals(loggedUser.get(i))) {
                loggedIn = i;
            }
            i++;
        }
        return loggedIn;
    }

    private boolean logout(String username) {
        boolean loggedIn = false;
        int loggedInIndex = loggedInIndex(username);
        if (loggedInIndex >= 0) {
            loggedIn = true;
            loggedUser.remove(loggedInIndex);
        }
        return loggedIn;
    }

    private boolean login(String userName, String password) {
        boolean valid = false;
        BufferedReader reader = null;
        FileReader fileReader = null;
        try {
            reader = new BufferedReader(fileReader = new FileReader(userConfigFile));
            String line = reader.readLine();
            while (line != null && valid == false) {
                String[] lineSplit = line.split("-");
                if (lineSplit.length >= 3 && lineSplit[0].equals("register")
                        && lineSplit[1].equals(userName) && lineSplit[2].equals(password)) {
                    valid = true;
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return valid;
    }

    private void writeUserConf(String content) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            File file = new File(userConfigFile);
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (IOException ex) {
            Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            MessageQ.Message message = messageQ.poll();
            String[] reqSplit = message.getRequest().split("-");

            if (reqSplit[0].equals("LOGIN")) {
                if (loggedInIndex(reqSplit[1]) == -1 && login(reqSplit[1], reqSplit[2])) {
                    message.getClient().responClient("OK");
                    loggedUser.add(reqSplit[1]);
                    String date = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(new Date());
                    writeUserConf("\nlogin-" + reqSplit[1] + "-" + date);
                } else {
                    message.getClient().responClient("!OK");
                }
            } else if (reqSplit[0].equals("LOGOUT")) {
                if (logout(reqSplit[1])) {
                    message.getClient().responClient("OK");
                    String date = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(new Date());
                    writeUserConf("\nlogout-" + reqSplit[1] + "-" + date);
                } else {
                    message.getClient().responClient("!OK");
                }
            } else if (reqSplit[0].equals("REG")) {
                if (isExist(reqSplit[1])) {
                    message.getClient().responClient("!OK");
                } else {
                    message.getClient().responClient("OK");
                    String date = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(new Date());
                    writeUserConf("\nregister-" + reqSplit[1] + "-" + reqSplit[2] + "-" + date);
                }
            }
        }
    }
}
