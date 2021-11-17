package mg.rinelfi.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mg.rinelfi.Launcher;

import java.io.IOException;

public class ContactController extends Controller{
    @FXML
    private Label username, textMessage;
    
    public void doOpenDiscussionAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/TextDiscussionView.fxml"));
        Parent view = loader.load();
        ((Controller) loader.getController()).setStage(this.getStage());
        Scene scene = new Scene(view);
        this.getStage().setScene(scene);
    }
    
    @Override
    public void setStage(Stage stage) {
        super.stage = stage;
        super.stage.setTitle("Guest - chat app");
    }
}
