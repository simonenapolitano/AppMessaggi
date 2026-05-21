package it.appmessaggi;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField IPInput;
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField portaInput;
    
    private Stage stage;
    private Scene scene;

    @FXML
    public void creaChatClient(ActionEvent event) throws IOException {
        // Controlla che l'utente abbia inserito i dati di base
        if (IPInput.getText().isEmpty() || usernameInput.getText().isEmpty() || portaInput.getText().isEmpty()) {
            System.out.println("Inserisci IP, porta e Username validi!");
            return;
        }
        cambiaScena();
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