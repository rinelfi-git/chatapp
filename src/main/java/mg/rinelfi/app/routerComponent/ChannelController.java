package mg.rinelfi.app.routerComponent;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mg.rinelfi.Launcher;
import mg.rinelfi.app.component.channel.ContactController;
import mg.rinelfi.beans.Discussion;
import mg.rinelfi.beans.User;
import mg.rinelfi.jiosocket.SocketEvents;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChannelController extends Controller implements Initializable {
    @FXML
    private AnchorPane contactOption, sessionOption;
    @FXML
    private VBox mainSession;
    @FXML
    private VBox discussionList;
    @FXML
    private BorderPane newChannelPane;
    private ObservableList<Node> contactNodes;
    private List<ContactController> contactControllers;
    
    @Override
    public void setStage(Stage stage) {
        super.stage = stage;
        super.stage.setTitle(this.user.getUsername() + " - chat app");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * GUI component initiation
         */
        this.contactControllers = new ArrayList<>();
        this.contactNodes = FXCollections.observableArrayList();
        Bindings.bindContentBidirectional(contactNodes, discussionList.getChildren());
        mainSession.toFront();
        sessionOption.toBack();
        contactOption.toBack();
        newChannelPane.toBack();
    }
    
    @FXML
    public void doOpenSessionOption() {
        mainSession.toBack();
        sessionOption.toFront();
        contactOption.toBack();
        newChannelPane.toBack();
    }
    
    @FXML
    public void doCloseOptions() {
        mainSession.toFront();
        sessionOption.toBack();
        contactOption.toBack();
        newChannelPane.toBack();
    }
    
    @FXML
    public void doDisconnect(MouseEvent event) throws IOException {
        if (MouseButton.PRIMARY == event.getButton()) {
            this.socket.setAutoreconnection(false);
            this.getSocket().disconnect();
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/routerComponent/AuthenticationView.fxml"));
            Parent view = loader.load();
            ((Controller) loader.getController()).setStage(this.getStage());
            Scene scene = new Scene(view);
            this.getStage().setScene(scene);
        }
    }
    
    @FXML
    public void doNewChannel() {
        newChannelPane.toFront();
        mainSession.toBack();
        sessionOption.toBack();
        contactOption.toBack();
    }
    
    private void addDiscussion(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/rinelfi/app/component/channel/ContactView.fxml"));
            this.contactNodes.add(loader.load());
            ContactController contact = loader.getController();
            this.contactControllers.add(contact);
    
            System.out.println("adding target : " + username);
            
            User user = new User();
            user.setUsername(username);
            Discussion discussion = new Discussion();
            discussion.setUser(user);
            contact.setDiscussion(discussion);
            
            contact.onContactRightClick(observation -> {
                mainSession.toBack();
                sessionOption.toBack();
                contactOption.toFront();
            });
            contact.onContactLeftClick(data -> {
                try {
                    final FXMLLoader textDiscussionLoader = new FXMLLoader(getClass().getResource("/mg/rinelfi/app/routerComponent/DiscussionView.fxml"));
                    Parent textDiscussionView = textDiscussionLoader.load();
                    DiscussionController discussionController = textDiscussionLoader.getController();
                    discussionController.getGuests().add(data.getUser().getUsername());
    
                    discussionController.setUser(this.getUser());
                    discussionController.setStage(this.getStage());
                    discussionController.setSocket(this.getSocket());
                    discussionController.setToken(this.getToken());
                    discussionController.getConnectedUsername().setText(data.getUser().getUsername());
                    discussionController.startSocket();
                    discussionController.sendMessageSeen();
                    
                    Scene scene = new Scene(textDiscussionView);
                    this.getStage().setScene(scene);
                } catch (Exception e) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void clearDiscussionThread() {
        this.contactNodes.clear();
    }
    
    public void startSocket() {
        /**
         * Initiation of socket use
         */
        this.getSocket().on(SocketEvents.CONNECT, callback -> {
            JSONObject decoder = new JSONObject(callback);
            this.setToken(decoder.get("identifier").toString());
            JSONObject json = new JSONObject();
            json.put("old", this.token).put("current", this.user.getUsername());
            this.setToken(this.user.getUsername());
            this.socket.emit(SocketEvents.RELOAD_IDENTITY, json.toString());
        }).on(SocketEvents.BROADCAST_IDENTITY, identities -> {
            Platform.runLater(() -> {
                JSONObject input = new JSONObject(identities);
                JSONArray array = input.getJSONArray("identities");
                this.contactNodes.clear();
                this.contactControllers.clear();
                for (Object identity : array) {
                    if (!identity.toString().equals(this.getToken())) addDiscussion(identity.toString());
                }
            });
        }).on("message", data -> {
            JSONObject json = new JSONObject(data);
            String sender = json.getString("sender");
            String target = json.getString("target");
            String message = json.getString("message");
            for (ContactController contactController : this.contactControllers) {
                if (contactController.getDiscussion().getUser().getUsername().equals(sender)) {
                    contactController.getDiscussion().setMessage(message);
                    Platform.runLater(() -> contactController.update());
                    JSONObject emit = new JSONObject();
                    emit.put("sender", target).put("target", sender);
                    this.socket.emit("received", emit.toString());
                    break;
                }
            }
        });
    }
}
