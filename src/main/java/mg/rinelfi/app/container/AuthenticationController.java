package mg.rinelfi.app.container;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
    void doConnection(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/rinelfi/app/container/DiscussionThreadView.fxml"));
        Parent view = loader.load();
        Controller controller = loader.getController();
        controller.setStage(this.getStage());
        controller.setSocket(this.getSocket());
        controller.startSocket();
        Scene scene = new Scene(view);
        this.getStage().setScene(scene);
        this.getSocket().connect();
    }
    
    @FXML
    void openRegister() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/rinelfi/app/container/RegistrationView.fxml"));
        Parent view = loader.load();
        ((Controller) loader.getController()).setStage(this.getStage());
        Scene scene = new Scene(view);
        this.getStage().setScene(scene);
    }
    
    @Override
    public void setStage(Stage stage) {
        super.stage = stage;
        super.stage.setTitle("login - chat app");
    }
    
    @Override
    protected void startSocket() {
        this.getSocket().on(SocketEvents.CONNECT, callback -> {
            JSONObject decoder = new JSONObject(callback);
            this.setToken(decoder.get("identifier").toString());
            System.out.println(this.getToken());
        });
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setSocket(new TCPClient("localhost", 2046));
    }
}
