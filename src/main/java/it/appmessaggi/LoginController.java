package it.appmessaggi;

import java.io.IOException;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField IPInput;
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField portaInput;
    @FXML
    private Label errorLabel;
    
    private Stage stage;
    private Scene scene;

    @FXML
    public void creaChatClient(ActionEvent event) throws IOException {
        if (IPInput.getText().isEmpty() || usernameInput.getText().isEmpty() ||  usernameInput.getText().isBlank() || portaInput.getText().isEmpty()) {
            errorLabel.setText("Inserisci IP, porta e Username validi!");
            return;
        }
        int porta;
        try {
            porta = Integer.parseInt(portaInput.getText());
        } catch (NumberFormatException e) {
            errorLabel.setText("La porta deve essere un numero valido!");
            return;
        }
        new Thread(() -> {
            try {
                Socket socket = new Socket(IPInput.getText(), porta);
                socket.close();

                javafx.application.Platform.runLater(() -> {
                    try {
                        cambiaScena();
                    } catch (IOException e) {
                        System.out.println("Errore nel caricamento della nuova scena.");
                    }
                });
            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    errorLabel.setText("Impossibile connettersi al server.");
                });
                return;
            }
        }).start();
    }

    private void cambiaScena() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/messaggi.fxml"));
        Parent root = loader.load();
        BroadcastController broadcastController = loader.getController();
        broadcastController.setIP(IPInput.getText());
        broadcastController.setUsername(usernameInput.getText());
        broadcastController.setPorta(portaInput.getText());
        broadcastController.connettiAlServer();
        stage = (Stage) IPInput.getScene().getWindow(); 
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}