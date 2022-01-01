package mg.rinelfi.app.container;

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

import java.io.File;
import java.io.IOException;

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
    void doAuthentication() throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/container/AuthenticationView.fxml"));
        Parent view = loader.load();
        ((Controller) loader.getController()).setStage(this.getStage());
        Scene scene = new Scene(view);
        this.getStage().setScene(scene);
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
