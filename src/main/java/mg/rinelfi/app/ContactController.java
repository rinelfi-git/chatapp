package mg.rinelfi.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import mg.rinelfi.Launcher;

import java.io.IOException;

public class ContactController extends Controller {
    @FXML
    private Label username, textMessage;
    
    public void doOpenDiscussionAction(MouseEvent event) throws IOException {
        if (MouseButton.SECONDARY == event.getButton()) {
            System.out.println("popup");
        } else if (MouseButton.PRIMARY == event.getButton()) {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/TextDiscussionView.fxml"));
            Parent view = loader.load();
            ((Controller) loader.getController()).setStage(this.getStage());
            Scene scene = new Scene(view);
            this.getStage().setScene(scene);
        }
    }
}
