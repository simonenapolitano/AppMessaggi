package it.appmessaggi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import java.awt.Toolkit;

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

    @FXML
    private Button emojiButton;

    @FXML
    private Button messaggiPrivatiButton;

    @FXML
    private TextField usernameInput;

    @FXML
    private TextField privateMessageInput;

    private boolean messaggioPrivatoMandato = false;

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPorta(String porta){
        this.portaRicevuta = porta;
    }

    public PrintWriter getOut() {
        return this.out;
    }

    @FXML
    private void mostraEmojiPicker() {
        ContextMenu emojiPopup = new ContextMenu();
        
        GridPane emojiGrid = new GridPane();
        emojiGrid.setHgap(6);
        emojiGrid.setVgap(6);
        emojiGrid.setPadding(new Insets(10));

        List<String> listaTutteLeEmoji = new ArrayList<>();

        for (int i = 0x1F600; i <= 0x1F637; i++) {
            listaTutteLeEmoji.add(new String(Character.toChars(i)));
        }

        for (int i = 0x1F446; i <= 0x1F44F; i++) {
            listaTutteLeEmoji.add(new String(Character.toChars(i)));
        }
        for (int i = 0x1F440; i <= 0x1F443; i++) {
            listaTutteLeEmoji.add(new String(Character.toChars(i)));
        }
        
        listaTutteLeEmoji.add(new String(Character.toChars(0x270B)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x270C)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F590)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F596)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F595)));

        for (int i = 0x1F400; i <= 0x1F43E; i++) {
            listaTutteLeEmoji.add(new String(Character.toChars(i)));
        }
        for (int i = 0x1F330; i <= 0x1F346; i++) {
            listaTutteLeEmoji.add(new String(Character.toChars(i)));
        }
        for (int i = 0x1F311; i <= 0x1F320; i++) {
            listaTutteLeEmoji.add(new String(Character.toChars(i)));
        }

        listaTutteLeEmoji.add(new String(Character.toChars(0x1F34A)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F34E)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F354)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F355)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F37A)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F37B)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x2615)));

        listaTutteLeEmoji.add(new String(Character.toChars(0x2764)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F499)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F49A)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F49B)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F49C)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F5A4)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F494)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x2728)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F6D2)));
        listaTutteLeEmoji.add(new String(Character.toChars(0x1F6E0)));

        for (int i = 0x1F6E1; i <= 0x1F6FF; i++) {
            listaTutteLeEmoji.add(new String(Character.toChars(i)));
        }

        int colonne = 10;

        for (int i = 0; i < listaTutteLeEmoji.size(); i++) {
            String emoji = listaTutteLeEmoji.get(i);
            Button btnEmoji = new Button(emoji);
            
            btnEmoji.setStyle("-fx-background-color: transparent; -fx-font-size: 20px; -fx-cursor: hand; -fx-padding: 2;");
            
            btnEmoji.setOnMouseEntered(e -> btnEmoji.setStyle("-fx-background-color: #e2e8f0; -fx-font-size: 20px; -fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 2;"));
            btnEmoji.setOnMouseExited(e -> btnEmoji.setStyle("-fx-background-color: transparent; -fx-font-size: 20px; -fx-cursor: hand; -fx-padding: 2;"));

            btnEmoji.setOnAction(e -> {
                messageInput.appendText(emoji);
                messageInput.requestFocus();
            });

            int riga = i / colonne;
            int colonna = i % colonne;
            emojiGrid.add(btnEmoji, colonna, riga);
        }

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(emojiGrid);
        scrollPane.setPrefViewportWidth(320);
        scrollPane.setPrefViewportHeight(250);
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);

        CustomMenuItem customMenuItem = new CustomMenuItem(scrollPane);
        customMenuItem.setHideOnClick(false);
        emojiPopup.getItems().add(customMenuItem);

        emojiPopup.show(emojiButton, javafx.geometry.Side.TOP, 0, -5);
    }

    @FXML
    private void mostraMessaggiPrivati(){
        messaggioPrivatoMandato = false;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/messaggioPrivato.fxml"));
            
            loader.setController(this); 
            
            Parent root = loader.load();
            Stage messaggiPrivatiStage = new Stage();
            messaggiPrivatiStage.setTitle("Messaggio privato");
            
            Scene messaggiPrivatiScene = new Scene(root);
            messaggiPrivatiStage.setScene(messaggiPrivatiScene);

            Stage mainStage = (Stage) messaggiPrivatiButton.getScene().getWindow();
            messaggiPrivatiStage.initOwner(mainStage);
            messaggiPrivatiStage.initModality(Modality.WINDOW_MODAL);
            
            messaggiPrivatiStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore nel caricamento del file FXML del popup.");
        }
    }

    @FXML
    private void inviaMessaggioPrivatoController(){
        String destinatario = usernameInput.getText().trim();
        String messaggio = privateMessageInput.getText().trim();

        System.out.println("Destinatario: " + destinatario);
        System.out.println("Messaggio che privato che vuoi mandare: " + messaggio);

        if (destinatario.isEmpty() || messaggio.isEmpty()) {
            System.out.println("Destinatario o messaggio mancanti!");
            return;
        }

        if (out != null) {
            out.println("/privato:" + destinatario + ":" + messaggio);
            
            privateMessageInput.clear();
            Stage popup = (Stage)privateMessageInput.getScene().getWindow();
            if(popup != null){
                popup.close();
            }
        } else {
            System.out.println("Non sei connesso al server!");
        }
        messaggioPrivatoMandato = true;
    }

    public void tornaAlLogin(){
        try {
            chiudiConnessione();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
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
                Thread t = new Thread(new ReadThread(in, this));
                t.setDaemon(true);
                t.start();

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

                        Stage stageAttuale = (Stage) controller.areaMessaggi.getScene().getWindow();
                        if (stageAttuale.isIconified() || !stageAttuale.isFocused()) {
                            Toolkit.getDefaultToolkit().beep();
                            String notifica = messaggio;
                            int maxCaratteri = 40;
                            if (notifica.length() > maxCaratteri) {
                                notifica = notifica.substring(0, maxCaratteri) + "...";
                            }
                            Notifications.create().title("💬 Uazzapp 2").text(notifica).position(Pos.TOP_RIGHT).darkStyle().showInformation();
                        }

                    });
                }
            } catch (IOException e) {
                System.out.println("Connessione interrotta.");
            }
        }

    }
}