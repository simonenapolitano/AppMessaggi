package it.appmessaggi;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_IP = "172.20.10.3"; // 'localhost' per testarlo sullo stesso PC
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try {
            
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connesso al server della chat!");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            // Chiede il nome utente al terminale
            System.out.print("Inserisci il tuo nome utente: ");
            String username = scanner.nextLine();
            out.println(username); // Lo invia subito al server come identificativo

            // FONDAMENTALE: Avvia un thread per leggere i messaggi in arrivo dal server
            new Thread(new ReadThread(in)).start();

            // Questo ciclo principale si occupa SOLO di leggere quello che scrivi tu sul cmd
            System.out.println("Puoi iniziare a scrivere. Digita '/quit' per uscire.");
            while (true) {
                String msg = scanner.nextLine();
                out.println(msg);
                if (msg.equalsIgnoreCase("/quit")) {
                    break;
                }
            }

            socket.close();
            System.exit(0);

        } catch (IOException e) {
            System.out.println("Impossibile connettersi al server. Assicurati che sia avviato.");
        }
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

// Sostituisci questa classe dentro ChatClient.java
