package mg.rinelfi.app.container;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import mg.rinelfi.beans.User;
import mg.rinelfi.jiosocket.SocketEvents;
import mg.rinelfi.jiosocket.client.TCPClient;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthenticationController extends Controller implements Initializable {
    @FXML
    private TextField username;
    
    @FXML
    private PasswordField password;
    
    @FXML
    void doConnection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/rinelfi/app/container/DiscussionThreadView.fxml"));
            Parent discussionThreadView = loader.load();
            DiscussionThreadController discussionThreadController = loader.getController();
            discussionThreadController.setUser(new User());
            discussionThreadController.getUser().setUsername(username.getText());
            discussionThreadController.setStage(this.getStage());
            discussionThreadController.setSocket(this.getSocket());
            discussionThreadController.setToken(this.getToken());
            discussionThreadController.startSocket();
            
            Scene scene = new Scene(discussionThreadView);
            this.getStage().setScene(scene);
            this.getSocket().connect();
        } catch (Exception e) {}
    }
    
    @FXML
    public void openRegister() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/rinelfi/app/container/RegistrationView.fxml"));
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
        this.getSocket().on(SocketEvents.CONNECT, callback -> {
            JSONObject decoder = new JSONObject(callback);
            this.setToken(decoder.get("identifier").toString());
        });
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (this.socket != null) this.socket = null;
        this.setSocket(new TCPClient("localhost", 2046));
    }
}
