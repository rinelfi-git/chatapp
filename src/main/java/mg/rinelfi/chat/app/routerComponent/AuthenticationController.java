package mg.rinelfi.chat.app.routerComponent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import mg.rinelfi.chat.beans.User;
import mg.rinelfi.factory.RJTPRequest;
import mg.rinelfi.factory.SocketFactory;
import mg.rinelfi.jiosocket.SocketEvents;

@SuppressWarnings("restriction")
public class AuthenticationController extends Controller implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    @FXML
    void doConnection() {
        JSONObject data = new JSONObject();
        data.put("username", this.username.getText());
        data.put("password", this.password.getText());
        RJTPRequest.getInstance().openConnection().setData(data).post("connection", response -> {
            if (response.getBoolean("match")) {
                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/mg/rinelfi/chat/app/routerComponent/ChannelView.fxml"));
                        User user = new User();
                        user.setUsername(response.getJSONObject("user").getString("username"));
                        user.setFirstname(response.getJSONObject("user").getString("firstname"));
                        user.setLastname(response.getJSONObject("user").getString("lastname"));
                        Parent discussionThreadView = loader.load();
                        ChannelController channelController = loader.getController();
                        channelController.setUser(user);
                        channelController.setStage(this.getStage());
                        channelController.setToken(this.getToken());
                        channelController.startSocket();

                        Scene scene = new Scene(discussionThreadView);
                        this.getStage().setScene(scene);

                        this.socket.setAutoreconnection(true);
                        this.socket.connect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else
                System.out.println("Please retry");
        });

    }

    @FXML
    public void openRegister() throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/mg/rinelfi/chat/app/routerComponent/RegistrationView.fxml"));
        Parent view = loader.load();
        ((Controller) loader.getController()).setStage(this.getStage());
        Scene scene = new Scene(view);
        this.getStage().setScene(scene);
    }

    @FXML
    public void doKeyPressed(KeyEvent event) {
        if (event.getCode().getName().equalsIgnoreCase("enter")) {
            this.doConnection();
        }
    }

    @Override
    public void setStage(Stage stage) {
        super.stage = stage;
        super.stage.setTitle("login - chat app");
    }

    @Override
    public void startSocket() {
        this.getSocket().on(SocketEvents.CONNECT, data -> {
            this.setToken(data.get("identifier").toString());
            System.out.println("connected as : " + this.token);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.socketFactory = SocketFactory.getInstance();
        this.setSocket(this.socketFactory.getConnection());
        System.out.println("socket in autentication : " + this.socket);
    }
}
