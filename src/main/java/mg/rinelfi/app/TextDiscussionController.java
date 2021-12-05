package mg.rinelfi.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import mg.rinelfi.Launcher;

import java.io.IOException;

public class TextDiscussionController extends Controller {
    @FXML
    private VBox discussionThread;
    @FXML
    private TextField input;
    @FXML
    public void doGoBackAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/DiscussionThreadView.fxml"));
        Parent view = loader.load();
        ((Controller) loader.getController()).setStage(this.getStage());
        Scene scene = new Scene(view);
        this.getStage().setScene(scene);
    }
    
    @FXML
    public void doSendMessage() throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/TextMessageMeView.fxml"));
        HBox discussion = loader.load();
        ((TextMessageMeController) loader.getController()).setMessage(this.input.getText());
        this.input.setText("");
        this.discussionThread.getChildren().add(discussion);
    }
    
    @FXML
    public void doBrowseAttach() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.showOpenDialog(null);
    }
}
