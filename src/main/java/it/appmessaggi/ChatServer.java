package it.appmessaggi;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private Scanner scanner = new Scanner(System.in);
    private static Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>());
    private static Set<Socket> clientSockets = Collections.synchronizedSet(new HashSet<>());  
    private int PORT = 0;
    private ServerSocket serverSocket;
    private volatile boolean running = true; 

    public ChatServer(){
        try {
            System.out.print("\nInserisci una porta: ");
            PORT = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Numero porta invalido!");
            System.exit(1);
        }

        try {
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("Il tuo indirizzo IP locale è: " + ip.getHostAddress());
        } catch (UnknownHostException e) {
            System.err.println("Impossibile trovare l'indirizzo IP: " + e.getMessage());
        }

        System.out.println("Il Server della chat è avviato sulla porta " + PORT + "...");
        System.out.println("Scrivi '/stop' in qualsiasi momento per spegnere il server.");
        
        Thread comandoThread = new Thread(new GestoreComandiServer());
        comandoThread.setDaemon(true); 
        comandoThread.start();

        try {
            serverSocket = new ServerSocket(PORT);
            while (running) {
                Socket clientSocket = serverSocket.accept();
                if (!running) break;
                
                System.out.println("Nuovo utente connesso: " + clientSocket.getRemoteSocketAddress());
                clientSockets.add(clientSocket);
                
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            } else {
                System.out.println("ServerSocket chiuso correttamente.");
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }
    
    public static void broadcast(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }

    private class GestoreComandiServer implements Runnable {
        @Override
        public void run() {
            while (running) {
                String comando = scanner.nextLine();
                if (comando.equalsIgnoreCase("/stop")) {
                    spegniServer();
                    break;
                }
            }
        }
    }

    private void spegniServer() {
        System.out.println("Spegnimento del server in corso...");
        running = false;
        broadcast("IL SERVER STA PER ESSERE CHIUSO.");

        
        synchronized (clientSockets) {
            for (Socket s : clientSockets) {
                try {
                    if (!s.isClosed()) s.close();
                } catch (IOException e) {
                    
                }
            }
        }

        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura del ServerSocket: " + e.getMessage());
        }

        System.out.println("Server spento con successo. Arrivederci!");
        System.exit(0);
    }

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

                clientWriters.add(out);

                username = in.readLine();
                broadcast(username + " si è unito alla chat!");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/quit")) {
                        break;
                    }
                    broadcast(username + ": " + message);
                }
            } catch (IOException e) {
                System.out.println("Connessione interrotta con " + username);
            } finally {
                if (out != null) clientWriters.remove(out);
                clientSockets.remove(socket);
                if (username != null) broadcast(username + " ha lasciato la chat.");
                try {
                    socket.close();
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}