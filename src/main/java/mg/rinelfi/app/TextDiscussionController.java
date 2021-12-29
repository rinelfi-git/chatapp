package mg.rinelfi.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import mg.rinelfi.Launcher;

import java.io.IOException;

public class TextDiscussionController extends Controller {
    @FXML
    private BorderPane reactLayer;
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
        FXMLLoader discussionLoader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/TextMessageMeView.fxml"));
        FXMLLoader reactionLoader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/ReactionView.fxml"));
        HBox discussion = discussionLoader.load(),
        reactionPanel = reactionLoader.load();
        TextMessageMeController discussionController = discussionLoader.getController();
        discussionController.setMessage(this.input.getText());
        discussionController.setReactionController(reactionLoader.getController());
        discussionController.setReactionPanel(reactionPanel);
        discussionController.onReactionRequest(reaction -> {
            discussionController.getReactionController().setReaction(reaction);
            discussionController.getReactionController().loadReaction();
            reactLayer.setCenter(reactionPanel);
            reactLayer.toFront();
        });
        this.input.setText("");
        this.discussionThread.getChildren().add(discussion);
    }
    
    @FXML
    public void doBrowseAttach() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.showOpenDialog(null);
    }
    
    @FXML
    public void doCloseReactLayer() {
        reactLayer.toBack();
    }
}
