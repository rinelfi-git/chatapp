package mg.rinelfi.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthenticationController extends Controller {
    @FXML
    private TextField username;
    
    @FXML
    private PasswordField password;
    
    @FXML
    void doConnection(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/rinelfi/app/DiscussionThreadView.fxml"));
        Parent view = loader.load();
        ((Controller) loader.getController()).setStage(this.getStage());
        Scene scene = new Scene(view);
        this.getStage().setScene(scene);
    }
    
    @FXML
    void openRegister() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/rinelfi/app/RegistrationView.fxml"));
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
}
