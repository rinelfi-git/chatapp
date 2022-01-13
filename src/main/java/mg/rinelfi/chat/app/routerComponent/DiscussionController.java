package mg.rinelfi.chat.app.routerComponent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import mg.rinelfi.chat.app.component.discussion.TextMessageController;
import mg.rinelfi.chat.app.component.discussion.TextMessageGuestController;
import mg.rinelfi.chat.app.component.discussion.TextMessageMeController;
import mg.rinelfi.factory.RJTPRequest;
import mg.rinelfi.factory.SocketFactory;
import mg.rinelfi.jiosocket.SocketEvents;
import org.json.JSONArray;

public class DiscussionController extends Controller implements Initializable {

    @FXML
    private BorderPane reactLayer;
    @FXML
    private VBox discussionThread;
    @FXML
    private TextField input;
    @FXML
    private Label typingIndicator, channelName;
    @FXML
    private ScrollPane scroller;
    private List<TextMessageController> textMessageControllers;
    private Map<String, String> tokens;
    private boolean isTyping;
    private int typingTimeout;
    private Thread typingThread;
    private long id;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * Socket initiation
         */
        this.socketFactory = SocketFactory.getInstance();
        this.setSocket(this.socketFactory.getConnection());

        this.textMessageControllers = new ArrayList<>();
        this.isTyping = false;

        this.discussionThread.heightProperty().addListener((observable, oldValue, newValue) -> {
            this.scroller.setVvalue((double) newValue);
        });
        
        
    }

    public void setId(long id) {
        this.id = id;
        RJTPRequest.getInstance().openConnection().setData(new JSONObject().put("channel", this.id)).post("message list", response -> {
            JSONArray messages = response.getJSONArray("messages");
            for (Object message : messages) {
                JSONObject json = (JSONObject) message;
                if (json.getString("user").equals(this.user.getUsername()))
                    Platform.runLater(() -> this.appendMessageMe(json.getString("content")));
                else
                    Platform.runLater(() -> this.doReceiveMessage(json.getString("content")));
            }
        });
    }

    public Map<String, String> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, String> tokens) {
        this.tokens = tokens;
    }

    public Label getChannelNameLabel() {
        return this.channelName;
    }

    @FXML
    public void doGoBackAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/rinelfi/chat/app/routerComponent/ChannelView.fxml"));
        Parent view = loader.load();
        ChannelController controller = loader.getController();

        controller.setUser(super.getUser());
        controller.setToken(this.getToken());
        controller.setStage(this.getStage());
        controller.startSocket();

        Scene scene = new Scene(view);
        this.getStage().setScene(scene);
    }

    @FXML
    public void doSendMessage() {
        final String message = this.input.getText();
        this.appendMessageMe(message);

        // Sending message
        this.isTyping = false;
        this.typingTimeout = 0;
        new Thread(() -> {
            this.tokens.forEach((username, token) -> {
                JSONObject json = new JSONObject();
                json.put("sender", getToken()).put("username", this.user.getUsername()).put("target", token).put("message", message);
                this.socket.emit("message", json.toString());
                json.remove("message");
                this.socket.emit("typing off", json.toString());
            });
        }).start();

        JSONObject output = new JSONObject();
        output.put("username", this.user.getUsername());
        output.put("channel", this.id);
        output.put("message", message);

        RJTPRequest.getInstance().openConnection().setData(output).post("text message", response -> {
            System.out.println(response);
        });
    }

    private void doReceiveMessage(final String message) {
        FXMLLoader discussionLoader = new FXMLLoader(getClass().getResource("/mg/rinelfi/chat/app/component/discussion/TextMessageGuestView.fxml"));
        FXMLLoader reactionLoader = new FXMLLoader(getClass().getResource("/mg/rinelfi/chat/app/component/discussion/ReactionView.fxml"));
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
        }
    }

    @FXML
    public void doKeyPressed(KeyEvent event) {
        if (event.getCode().getName().equalsIgnoreCase("enter")) {
            this.doSendMessage();
        } else {
            if (!this.isTyping) {
                this.isTyping = true;
                this.tokens.forEach((username, token) -> {
                    JSONObject json = new JSONObject();
                    json.put("target", token).put("username", this.user.getUsername()).put("sender", this.getToken());
                    this.socket.emit("typing on", json.toString());
                });
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
    public void startSocket() {
        /**
         * Initiation of socket use
         */
        this.getSocket().on(SocketEvents.CONNECT, data -> {
            JSONObject request = new JSONObject(data);
            this.setToken(request.getString("identifier"));
        }).on("message", data -> {
            JSONObject request = new JSONObject(data);
            String sender = request.getString("sender");
            final String message = request.getString("message");
            if (this.tokens.containsValue(sender)) {
                System.out.println("received message");
                Platform.runLater(() -> doReceiveMessage(message));
            }
        }).on("typing on", data -> {
            JSONObject request = new JSONObject(data);
            String sender = request.getString("sender");
            String username = request.getString("username");
            System.out.println("typing detected");
            if (this.tokens.containsValue(sender)) {
                Platform.runLater(() -> {
                    this.typingIndicator.setText(String.format("%s is typing", username));
                    this.typingIndicator.setVisible(true);
                });
            }
        }).on("typing off", data -> {
            JSONObject request = new JSONObject(data);
            String sender = request.getString("sender");
            if (this.tokens.containsValue(sender)) {
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
                while ((typingTimeout -= 100) >= 0 && this.isTyping) {
                    System.out.println(this.typingTimeout);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                this.isTyping = false;
                new Thread(() -> {
                    this.tokens.forEach((username, token) -> {
                        JSONObject json = new JSONObject();
                        json.put("target", token).put("sender", getToken());
                        this.socket.emit("typing off", json.toString());
                    });
                }).start();
            });
            this.typingThread.start();
        }
    }

    public void sendMessageSeen() {
        new Thread(() -> {
            this.tokens.forEach((username, token) -> {
                JSONObject emit = new JSONObject();
                emit.put("sender", getUser().getUsername()).put("target", token);
                this.socket.emit("seen", emit.toString());
            });
        }).start();
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

    private void appendMessageMe(String message) {
        try {
            FXMLLoader discussionLoader = new FXMLLoader(getClass().getResource("/mg/rinelfi/chat/app/component/discussion/TextMessageMeView.fxml"));
            FXMLLoader reactionLoader = new FXMLLoader(getClass().getResource("/mg/rinelfi/chat/app/component/discussion/ReactionView.fxml"));
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
        } catch (IOException ex) {
            Logger.getLogger(DiscussionController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
