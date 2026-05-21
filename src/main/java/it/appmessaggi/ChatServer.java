package it.appmessaggi;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    // Insieme di tutti i canali di output verso i client connessi
    private static Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Il Server della chat è avviato sulla porta " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Rimane in ascolto per nuovi utenti
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuovo utente connesso: " + clientSocket.getRemoteSocketAddress());
                
                // Avvia un thread dedicato per gestire questo specifico utente
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Invia il messaggio a TUTTI i client connessi
    public static void broadcast(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }

    // Gestore del singolo client
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Aggiunge questo utente alla lista globale
                clientWriters.add(out);

                // Il primo messaggio inviato dal client sarà il suo nome utente
                username = in.readLine();
                broadcast("[SERVER] " + username + " si è unito alla chat!");

                String message;
                // Rimane in ascolto dei messaggi inviati da questo utente
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/quit")) {
                        break;
                    }
                    // Formato richiesto: "NomeUtente: messaggio"
                    broadcast(username + ": " + message);
                }
            } catch (IOException e) {
                System.out.println("Connessione interrotta con " + username);
            } finally {
                // Pulizia quando l'utente si disconnette
                if (out != null) clientWriters.remove(out);
                if (username != null) broadcast("[SERVER] " + username + " ha lasciato la chat.");
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
}