package mg.rinelfi.chat.app.routerComponent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mg.rinelfi.Launcher;
import mg.rinelfi.jiosocket.client.PseudoWebClient;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import mg.rinelfi.factory.RJTPRequest;

import org.json.JSONObject;

@SuppressWarnings("restriction")
public class RegistrationController extends Controller{
    private String profile;
    @FXML
    private TextField firstname;
    @FXML
    private TextField lastname;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField passwordConfirmation;
    
    @FXML
    void doAuthentication() {
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/chat/app/routerComponent/AuthenticationView.fxml"));
            Parent view = loader.load();
            ((Controller) loader.getController()).setStage(this.getStage());
            Scene scene = new Scene(view);
            this.getStage().setScene(scene);
        } catch (IOException ex) {
            Logger.getLogger(RegistrationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void exploreImage() {
        FileChooser chooser = new FileChooser();
        File selectedFile = chooser.showOpenDialog(null);
        if(selectedFile != null) this.profile = selectedFile.getAbsolutePath();
        System.out.println("you selected: " + this.profile);
    }
    
    @FXML
    void doRegister() {
		JSONObject data = new JSONObject();
		data.put("firstname", this.firstname.getText());
		data.put("lastname", this.lastname.getText());
		data.put("username", this.username.getText());
		data.put("password", this.password.getText());
        RJTPRequest.getInstance().openConnection().setData(data).post("registration", response -> {
            Platform.runLater(this::doAuthentication);
		});
    }
    
    @FXML
    public void doKeyPressed(KeyEvent event) {
        if (event.getCode().getName().equalsIgnoreCase("enter")) {
            this.doRegister();
        }
    }
    
    @Override
    public void setStage(Stage stage) {
        super.stage = stage;
        super.stage.setTitle("inscription - chat app");
    }
    
    @Override
    public void startSocket() {
    
    }
}
