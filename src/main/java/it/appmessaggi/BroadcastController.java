package it.appmessaggi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BroadcastController {
    private String IP;
    private String usernameRicevuto;
    private String portaRicevuta;
    
    private PrintWriter out;
    private Socket socket;

    private Stage stage;
    private Scene scene;

    @FXML
    private TextField messageInput;
    @FXML
    private TextArea areaMessaggi;

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPorta(String porta){
        this.portaRicevuta = porta;
    }

    public void tornaAlLogin(){
        try {
            chiudiConnessione();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            stage = (Stage) messageInput.getScene().getWindow(); 
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String usernameRicevuto) {
        this.usernameRicevuto = usernameRicevuto;
    }

    public void connettiAlServer() {
        if (IP == null || IP.isEmpty()) {
            System.out.println("Errore: IP non configurato.");
            return;
        }

        new Thread(() -> {
            try {
                socket = new Socket(IP, Integer.parseInt(portaRicevuta));
                System.out.println("Connesso al server della chat da BroadcastController!");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(usernameRicevuto);
                new Thread(new ReadThread(in, this)).start();

            } catch (IOException e) {
                System.out.println("Impossibile connettersi al server.");
            }
        }).start();
    }

    @FXML
    private void gestisciInvioMessaggio() {
        String msg = messageInput.getText();
        if (msg != null && !msg.trim().isEmpty() && out != null) {
            out.println(msg);
            messageInput.clear();
        }
    }

    private void chiudiConnessione() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static class ReadThread implements Runnable {
        private final BufferedReader in;
        private final BroadcastController controller;

        public ReadThread(BufferedReader in, BroadcastController controller) {
            this.in = in;
            this.controller = controller;
        }

        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                    String messaggio = serverMessage;
                    javafx.application.Platform.runLater(() -> {
                        controller.areaMessaggi.appendText(messaggio + "\n");
                    });
                }
            } catch (IOException e) {
                System.out.println("Connessione interrotta.");
            }
        }
    }
}