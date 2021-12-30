package mg.rinelfi.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import mg.rinelfi.Launcher;
import mg.rinelfi.abstraction.ReactionActionConsumer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TextDiscussionController extends Controller implements Initializable {
    @FXML
    private BorderPane reactLayer;
    @FXML
    private VBox discussionThread;
    @FXML
    private TextField input;
    private List<TextMessageController> textMessageControllers;
    
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
        VBox discussion = discussionLoader.load();
        HBox reactionPanel = reactionLoader.load();
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
        this.textMessageControllers.add(discussionController);
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
        for (TextMessageController textMessageController : this.textMessageControllers) {
            textMessageController.getReactionController().resetOnReact();
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.textMessageControllers = new ArrayList<>();
        
    }
}
