package mg.rinelfi.app.container;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import mg.rinelfi.Launcher;
import mg.rinelfi.app.component.message.TextMessageController;
import mg.rinelfi.app.component.message.TextMessageGuestController;
import mg.rinelfi.app.component.message.TextMessageMeController;
import mg.rinelfi.jiosocket.SocketEvents;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TextDiscussionController extends Controller implements Initializable {
    @FXML
    private BorderPane reactLayer;
    @FXML
    private VBox discussionThread;
    @FXML
    private TextField input;
    @FXML
    private Label typingIndicator;
    private List<TextMessageController> textMessageControllers;
    private List<String> guests;
    private boolean someoneTyping;
    private int typingTimeout;
    private Thread typingThread;
    
    public List<String> getGuests() {
        return guests;
    }
    
    @FXML
    public void doGoBackAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/container/DiscussionThreadView.fxml"));
        Parent view = loader.load();
        DiscussionThreadController controller = loader.getController();
        
        controller.setUser(this.getUser());
        controller.setStage(this.getStage());
        controller.setSocket(this.getSocket());
        controller.setToken(this.getToken());
        controller.startSocket();
        controller.getSocket().emit(SocketEvents.BROADCAST_IDENTITY, "");
        
        Scene scene = new Scene(view);
        this.getStage().setScene(scene);
    }
    
    @FXML
    public void doSendMessage() throws IOException {
        final String message = this.input.getText();
        FXMLLoader discussionLoader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/component/message/TextMessageMeView.fxml"));
        FXMLLoader reactionLoader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/component/message/ReactionView.fxml"));
        VBox discussion = discussionLoader.load();
        HBox reactionPanel = reactionLoader.load();
        TextMessageMeController meController = discussionLoader.getController();
        meController.setMessage(message);
        meController.setReactionController(reactionLoader.getController());
        meController.setReactionPanel(reactionPanel);
        meController.onReactionRequest(reaction -> {
            meController.getReactionController().setReaction(reaction);
            meController.getReactionController().loadReaction();
            reactLayer.setCenter(reactionPanel);
            reactLayer.toFront();
        });
        this.textMessageControllers.add(meController);
        this.input.setText("");
        this.discussionThread.getChildren().add(discussion);
        
        // Sending message
        JSONObject json = new JSONObject();
        json.put("sender", this.getToken()).put("target", this.guests.get(0)).put("message", message);
        this.getSocket().emit("message", json.toString());
        this.typingTimeout = 0;
    }
    
    private void doReceiveMessage(final String message) {
        FXMLLoader discussionLoader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/component/message/TextMessageGuestView.fxml"));
        FXMLLoader reactionLoader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/component/message/ReactionView.fxml"));
        try {
            VBox discussion = discussionLoader.load();
            HBox reactionPanel = reactionLoader.load();
            
            TextMessageGuestController guestController = discussionLoader.getController();
            guestController.setMessage(message);
            guestController.setReactionController(reactionLoader.getController());
            guestController.setReactionPanel(reactionPanel);
            guestController.onReactionRequest(reaction -> {
                guestController.getReactionController().setReaction(reaction);
                guestController.getReactionController().loadReaction();
                reactLayer.setCenter(reactionPanel);
                reactLayer.toFront();
            });
            this.textMessageControllers.add(guestController);
            this.discussionThread.getChildren().add(discussion);
            this.sendMessageSeen();
            this.cleanMessageStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void doKeyPressed(KeyEvent event) {
        if (event.getCode().getName().equalsIgnoreCase("enter")) {
            try {
                this.doSendMessage();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            if (!this.someoneTyping) {
                System.out.println("not typing yet");
                this.someoneTyping = true;
                JSONObject json = new JSONObject();
                json.put("target", this.guests.get(0)).put("sender", this.token);
                this.socket.emit("typing on", json.toString());
            }
        }
    }
    
    public void doKeyUp() {
        this.typingTimeout = 2000;
        checkTyping();
    }
    
    @FXML
    public void doBrowseAttach() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.showOpenDialog(null);
    }
    
    @FXML
    public void doCloseReactLayer() {
        reactLayer.toBack();
        for (TextMessageController textMessageController : this.textMessageControllers) {
            textMessageController.getReactionController().resetOnReact();
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.textMessageControllers = new ArrayList<>();
        this.guests = new ArrayList<>();
        this.someoneTyping = false;
    }
    
    @Override
    public void startSocket() {
        /**
         * Initiation of socket use
         */
        this.getSocket().on(SocketEvents.CONNECT, callback -> {
                JSONObject decoder = new JSONObject(callback);
                this.setToken(decoder.get("identifier").toString());
            }).on("message", data -> {
                JSONObject object = new JSONObject(data);
                String sender = object.getString("sender");
                final String message = object.getString("message");
                if (this.guests.contains(sender)) {
                    Platform.runLater(() -> doReceiveMessage(message));
                }
            }).on("typing on", data -> {
                JSONObject object = new JSONObject(data);
                String sender = object.getString("sender");
                System.out.println("typing detected");
                if (this.guests.contains(sender)) {
                    Platform.runLater(() -> {
                        this.typingIndicator.setText(String.format("%s is typing", sender));
                        this.typingIndicator.setVisible(true);
                    });
                }
            }).on("typing off", data -> {
                JSONObject object = new JSONObject(data);
                String sender = object.getString("sender");
                if (this.guests.contains(sender)) {
                    Platform.runLater(() -> {
                        this.typingIndicator.setText("");
                        this.typingIndicator.setVisible(false);
                    });
                }
            }).on("sent", data -> setMessageSent())
            .on("received", data -> setMessageReceived())
            .on("seen", data -> setMessageSeen());
    }
    
    private void checkTyping() {
        if (this.typingThread == null || !this.typingThread.isAlive()) {
            this.typingThread = new Thread(() -> {
                while ((this.typingTimeout -= 100) >= 0) {
                    System.out.println(this.typingTimeout);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                this.someoneTyping = false;
                JSONObject json = new JSONObject();
                json.put("target", this.guests.get(0)).put("sender", this.token);
                this.socket.emit("typing off", json.toString());
            });
            this.typingThread.start();
        }
    }
    
    public void sendMessageSeen() {
        JSONObject emit = new JSONObject();
        emit.put("sender", this.token).put("target", this.guests.get(0));
        this.socket.emit("seen", emit.toString());
    }
    
    public void setMessageSeen() {
        this.cleanMessageStatus();
        final int size = this.textMessageControllers.size();
        if (size > 0) {
            TextMessageController textMessageController = this.textMessageControllers.get(size - 1);
            if (textMessageController instanceof TextMessageMeController) {
                Platform.runLater(() -> ((TextMessageMeController) textMessageController).setSeen(true));
            }
        }
    }
    
    private void cleanMessageStatus() {
        final int size = this.textMessageControllers.size();
        for (int i = 0; i < size - 1; i++) {
            TextMessageController textMessageController = this.textMessageControllers.get(i);
            if (textMessageController instanceof TextMessageMeController) {
                ((TextMessageMeController) textMessageController).getMessageStatusView().setVisible(false);
            }
        }
    }
    
    public void setMessageSent() {
        this.cleanMessageStatus();
        final int size = this.textMessageControllers.size();
        if (size > 0) {
            TextMessageController textMessageController = this.textMessageControllers.get(size - 1);
            if (textMessageController instanceof TextMessageMeController) {
                Platform.runLater(() -> ((TextMessageMeController) textMessageController).setSent(true));
            }
        }
    }
    
    public void setMessageReceived() {
        this.cleanMessageStatus();
        final int size = this.textMessageControllers.size();
        if (size > 0) {
            TextMessageController textMessageController = this.textMessageControllers.get(size - 1);
            if (textMessageController instanceof TextMessageMeController) {
                Platform.runLater(() -> ((TextMessageMeController) textMessageController).setReceived(true));
            }
        }
    }
}
