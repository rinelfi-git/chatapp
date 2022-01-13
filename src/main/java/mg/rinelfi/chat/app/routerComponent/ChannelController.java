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
import java.util.Map;
import java.util.ResourceBundle;
import mg.rinelfi.console.Console;
import mg.rinelfi.factory.RJTPRequest;
import mg.rinelfi.factory.SocketFactory;
import mg.rinelfi.factory.TokenManager;

@SuppressWarnings("restriction")
public class ChannelController extends Controller implements Initializable {

    @FXML
    private AnchorPane contactOption, sessionOption;
    @FXML
    private VBox mainSession;
    @FXML
    private VBox discussionList, contactList;
    @FXML
    private BorderPane newChannelPane;
    private ObservableList<Node> channelNodes;
    private List<ContactController> channelControllers;

    @Override
    public void setStage(Stage stage) {
        super.stage = stage;
        super.stage.setTitle(this.user.getUsername() + " - chat app");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialization");
        /**
         * Socket initiation
         */
        this.socketFactory = SocketFactory.getInstance();
        this.setSocket(this.socketFactory.getConnection());

        /**
         * GUI component initiation
         */
        this.channelControllers = new ArrayList<>();
        this.channelNodes = FXCollections.observableArrayList();
        Bindings.bindContentBidirectional(channelNodes, discussionList.getChildren());
        mainSession.toFront();
        sessionOption.toBack();
        contactOption.toBack();
        newChannelPane.toBack();

        RJTPRequest.getInstance().openConnection().post("users", response -> {
            JSONArray users = response.getJSONArray("users");
            Console.log(getClass(), users.toString());
        });

        RJTPRequest.getInstance().openConnection().post("channels", response -> {
            JSONArray channels = response.getJSONArray("channels");
            Console.log(getClass(), "users : " + channels);
            for (Object channel : channels) {
                JSONObject decoded = (JSONObject) channel;
                System.out.println();
                if (decoded.getString("type").equals("user_channel")) {
                    for (Object userChannelLink : decoded.getJSONObject("channel").getJSONArray("userChannelLinks")) {
                        JSONObject jsonUser = ((JSONObject) userChannelLink).getJSONObject("user");
                        if (!jsonUser.getString("username").equals(this.user.getUsername())) {
                            List<User> members = new ArrayList<>();
                            User user = new User();
                            user.setUsername(jsonUser.getString("username"));
                            user.setNickname(((JSONObject) userChannelLink).getString("username"));
                            members.add(user);
                            Platform.runLater(() -> addDiscussion(decoded.getJSONObject("channel").getLong("id"), user.getNickname(), members));
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
        System.out.println("closed");
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

    private synchronized void addDiscussion(long id, String title, List<User> members) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/rinelfi/chat/app/component/channel/ContactView.fxml"));
            this.channelNodes.add(loader.load());
            ContactController contact = loader.getController();
            this.channelControllers.add(contact);

            contact.getTitle().setText(title);
            contact.getLastMessage().setText("");
            contact.getMembers().addAll(members);
            TokenManager.getInstance().getTokens().forEach((tokenOwner, token) -> {
                if (tokenOwner.equals(contact.getTitle().getText())) {
                    contact.getTokens().put(tokenOwner, token);
                }
            });

            contact.setId(id);
            contact.onContactRightClick(observation -> {
                mainSession.toBack();
                sessionOption.toBack();
                contactOption.toFront();
            });
            contact.onContactLeftClick((scopedId, channelName, tokens) -> {
                    System.out.println("click");
                try {
                    final FXMLLoader textDiscussionLoader = new FXMLLoader(getClass().getResource("/mg/rinelfi/chat/app/routerComponent/DiscussionView.fxml"));
                    Parent textDiscussionView = textDiscussionLoader.load();
                    DiscussionController discussionController = textDiscussionLoader.getController();

                    discussionController.setId(scopedId);
                    discussionController.setUser(this.getUser());
                    discussionController.setStage(this.getStage());
                    discussionController.setToken(getToken());
                    discussionController.setTokens(tokens);
                    discussionController.getChannelNameLabel().setText(channelName);
                    discussionController.startSocket();
                    discussionController.sendMessageSeen();

                    Scene scene = new Scene(textDiscussionView);
                    this.getStage().setScene(scene);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
        }
    }

    private void clearDiscussionThread() {
        this.channelNodes.clear();
    }

    public void startSocket() {
        /**
         * Initiation of socket use
         */
        this.socket.on(SocketEvents.CONNECT, data -> {
            JSONObject request = new JSONObject(data);
            this.setToken(request.get("token").toString());
            JSONObject json = new JSONObject();
            json.put("token", this.token);
            json.put("username", this.user.getUsername());
            this.socket.emit("token connect message", json.toString());
        }).on("token connect message", data -> {
            Console.log(getClass(), "new token : " + this.channelControllers.size());
            JSONObject request = new JSONObject(data);
            String token = request.getString("token");
            String username = request.getString("username");
            for (ContactController contactController : this.channelControllers) {
                System.out.println("username : " + contactController.getTitle());
                if (contactController.getTitle().getText().equals(username)) {
                    Platform.runLater(() -> contactController.getLastMessage().setText("connected"));
                    contactController.getTokens().put(username, token);
                    TokenManager.getInstance().getTokens().put(username, token);

                    JSONObject json = new JSONObject();
                    json.put("token", this.token);
                    json.put("username", this.user.getUsername());
                    json.put("target", token);
                    this.socket.emit("token connect reply", json.toString());
                }
            }
        }).on("token connect reply", data -> {
            Console.log(getClass(), "new reply token : " + channelControllers.size());
            JSONObject request = new JSONObject(data);
            String token = request.getString("token");
            String username = request.getString("username");
            TokenManager.getInstance().getTokens().put(username, token);
            for (ContactController contactController : this.channelControllers) {
                Console.log(getClass(), "loop on username : " + contactController.getTitle().getText() + " && " + username);
                if (contactController.getTitle().getText().equals(username)) {
                    Platform.runLater(() -> contactController.getLastMessage().setText("connected"));
                    contactController.getTokens().put(username, token);
                }
            }
        }).on(SocketEvents.TOKEN_DISCONNECT, (data) -> {
            JSONObject request = new JSONObject(data);
            String token = request.getString("token");
            for (ContactController contactController : this.channelControllers) {
                if (contactController.getTokens().containsValue(token))
                    contactController.getTokens().remove(token);
            }
        }).on("message", data -> {
            JSONObject request = new JSONObject(data);
            String sender = request.getString("sender");
            String target = request.getString("target");
            String message = request.getString("message");
            System.out.println("incoming from : " + sender);
            for (ContactController contactController : this.channelControllers) {
                if (contactController.getTokens().containsValue(sender)) {
                    Platform.runLater(() -> {
                        contactController.getLastMessage().setText(message);
                        contactController.getLastMessage().getStyleClass().remove("not-read");
                        contactController.getLastMessage().getStyleClass().add("not-read");
                    });
                    JSONObject emit = new JSONObject();
                    emit.put("sender", target).put("target", sender);
                    this.socket.emit("received", emit.toString());
                    break;
                }
            }
        });
    }
}
