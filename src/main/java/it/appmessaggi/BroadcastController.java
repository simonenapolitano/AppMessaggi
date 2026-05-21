package it.appmessaggi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.fxml.FXML;

public class BroadcastController {
    private static final int SERVER_PORT = 12345;
    private String IP;
    private String usernameRicevuto;

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setUsername(String usernameRicevuto) {
        this.usernameRicevuto = usernameRicevuto;
    }
    class ChatClient{
        public ChatClient(){
            try {
                final String SERVER_IP = IP;
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                System.out.println("Connesso al server della chat!");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                String username = usernameRicevuto;
                out.println(usernameRicevuto); // Lo invia subito al server come identificativo

                // Questo ciclo principale si occupa SOLO di leggere quello che scrivi tu sul cmd
                /*System.out.println("Puoi iniziare a scrivere. Digita '/quit' per uscire.");
                while (true) {
                    String msg = messageInput.getText();
                    out.println(msg);
                    if (msg.equalsIgnoreCase("/quit")) {
                        break;
                    }
                }*/

                socket.close();
                System.exit(0);

            } catch (IOException e) {
                System.out.println("Impossibile connettersi al server. Assicurati che sia avviato.");
            }
        }
    }
    @FXML
    public void initialize(){
        if(!IP.isEmpty()){
            new ChatClient();
        } else{
            System.out.println("Tutti gay");
        }
        
    }
}
