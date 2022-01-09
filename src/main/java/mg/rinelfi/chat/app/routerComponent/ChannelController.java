package mg.rinelfi.chat.app.routerComponent;

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
import mg.rinelfi.chat.app.component.channel.ContactController;
import mg.rinelfi.chat.beans.Discussion;
import mg.rinelfi.chat.beans.User;
import mg.rinelfi.jiosocket.SocketEvents;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import mg.rinelfi.factory.RJTPRequest;
import mg.rinelfi.factory.SocketFactory;
import mg.rinelfi.jiosocket.client.PseudoWebClient;

@SuppressWarnings("restriction")
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
         * Socket initiation
         */
        this.socketFactory = SocketFactory.getInstance();
        this.setSocket(this.socketFactory.getConnection());

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

        RJTPRequest.getInstance().openConnection().post("channels", response -> {
            JSONArray channels = response.getJSONArray("channels");
            for (Object channel : channels) {
                JSONObject decoded = (JSONObject) channel;
                if (decoded.getString("type").equals("user_channel")) {
                    for (Object userChannelLink : decoded.getJSONObject("channel").getJSONArray("userChannelLinks")) {
                        JSONObject jsonUser = ((JSONObject) userChannelLink).getJSONObject("user");
                        if (!jsonUser.getString("username").equals(this.user.getUsername())) {
                            addDiscussion(jsonUser.getString("username"));
                        }
                    }
                }
            }
        });
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
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/chat/app/routerComponent/AuthenticationView.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/rinelfi/chat/app/component/channel/ContactView.fxml"));
            this.contactNodes.add(loader.load());
            ContactController contact = loader.getController();
            this.contactControllers.add(contact);

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
            contact.onContactLeftClick((contactName, tokens) -> {
                try {
                    final FXMLLoader textDiscussionLoader = new FXMLLoader(getClass().getResource("/mg/rinelfi/chat/app/routerComponent/DiscussionView.fxml"));
                    Parent textDiscussionView = textDiscussionLoader.load();
                    DiscussionController discussionController = textDiscussionLoader.getController();

                    discussionController.setUser(this.getUser());
                    discussionController.setStage(this.getStage());
                    discussionController.setToken(this.getToken());
                    discussionController.setTokens(tokens);
                    discussionController.getConnectedUsername().setText(contactName);
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
        this.socket.on(SocketEvents.CONNECT, data -> {
            this.setToken(data.get("identifier").toString());
            RJTPRequest.getInstance().openConnection()
                .setData(new JSONObject().put("username", this.user.getUsername()).put("token", this.token)).post("register token", response -> {
                System.out.println(response.toString());
            });
        }).on(SocketEvents.TOKEN_CONNECT, data -> {
            /**
             * Rearranger le code pour mathcer toutes les controllers actifs sur
             * les identités recues avec leur nom d'utilisateur
             */
            this.setToken(data.getString("token"));
            JSONObject json = new JSONObject();
            json.put("token", this.token);
            json.put("username", this.user.getUsername());
            System.out.println("received token : " + this.user.getUsername());
            this.socket.emit("token emission", json.toString());
        }).on(SocketEvents.TOKEN_DISCONNECT, (data) -> {
            String token = data.getString("token");
            for (ContactController contactController : this.contactControllers) {
                if (contactController.getTokens().contains(token)) {
                    contactController.getTokens().remove(token);
                }
            }
        }).on("token emission", data -> {
            String token = data.getString("token");
            String username = data.getString("username");
            System.out.println("emission request : " + username);
            for (ContactController contactController : this.contactControllers) {
                if (contactController.getDiscussion().getUser().getUsername().equals(username)) {
                    contactController.getTokens().add(token);
                    JSONObject json = new JSONObject();
                    json.put("token", this.token);
                    json.put("username", this.user.getUsername());
                    json.put("target", token);
                    this.socket.emit("token unicast", json.toString());
                }
            }
        }).on("token unicast", data -> {
            String token = data.getString("token");
            String username = data.getString("username");
            System.out.println("unicast: " + username);
            for (ContactController contactController : this.contactControllers) {
                if (contactController.getDiscussion().getUser().getUsername().equals(username)) {
                    contactController.getTokens().add(token);
                }
            }
        }).on("message", data -> {
            System.out.println("message incoming");
            String sender = data.getString("sender");
            String target = data.getString("target");
            String message = data.getString("message");
            for (ContactController contactController : this.contactControllers) {
                if (contactController.getDiscussion().getUser().getUsername().equals(sender)) {
                    contactController.getDiscussion().setMessage(message);
                    contactController.getMessageView().getStyleClass().remove("not-read");
                    contactController.getMessageView().getStyleClass().add("not-read");
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
