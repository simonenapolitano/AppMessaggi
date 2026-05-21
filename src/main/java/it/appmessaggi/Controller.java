package it.appmessaggi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {
    @FXML
    private TextField IPInput;
    @FXML
    private TextField usernameInput;
    private Stage stage;
    private Scene scene;

    public void creaChatClient(ActionEvent event) throws IOException{
        new ChatClient();
    }

    class ChatClient{
        private static final int SERVER_PORT = 12345;

        public ChatClient(){
            try {
                final String SERVER_IP = IPInput.getText();
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                System.out.println("Connesso al server della chat!");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in);

                String username = usernameInput.getText();
                out.println(username); // Lo invia subito al server come identificativo

                // FONDAMENTALE: Avvia un thread per leggere i messaggi in arrivo dal server
                new Thread(new ReadThread(in)).start();

                cambiaScena();
                // Questo ciclo principale si occupa SOLO di leggere quello che scrivi tu sul cmd
                /*System.out.println("Puoi iniziare a scrivere. Digita '/quit' per uscire.");
                while (true) {
                    String msg = scanner.nextLine();
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

        private void cambiaScena() throws IOException {
            Parent root = FXMLLoader.load(getClass().getResource("messaggi.fxml"));
            
            // Recupera lo stage attuale da un elemento esistente (es. un anchorPane)
            stage = (Stage)IPInput.getScene().getWindow(); 
            
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }

        // Thread interno che rimane in ascolto in background dei messaggi degli altri
        private static class ReadThread implements Runnable {
            private BufferedReader in;

            public ReadThread(BufferedReader in) {
                this.in = in;
            }

            @Override
            public void run() {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        // TRUCCO ANSI:
                        // \r       -> Sposta il cursore all'inizio della riga corrente
                        // \033[K   -> Cancella tutto quello che c'è sulla riga dal cursore in poi
                        System.out.print("\r\033[K");
                        
                        // Stampiamo il messaggio che ci è appena arrivato dal server
                        System.out.println(serverMessage);
                        
                        // Opzionale: se vuoi mostrare un piccolo indicatore per far capire 
                        // all'utente che può ancora scrivere (es. un "Tu > ")
                        System.out.print("Tu > "); 
                    }
                } catch (IOException e) {
                    System.out.println("\nConnessione al server interrotta.");
                }
            }
        }
    }
}
