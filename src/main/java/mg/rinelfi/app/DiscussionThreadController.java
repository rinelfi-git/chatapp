package mg.rinelfi.app;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DiscussionThreadController extends Controller implements Initializable {
    @FXML
    private VBox discussionList;
    private ObservableList<Node> discussionThread;
    
    @Override
    public void setStage(Stage stage) {
        super.stage = stage;
        super.stage.setTitle("Rinelfi - chat app");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.discussionThread = FXCollections.observableArrayList();
        Bindings.bindContentBidirectional(discussionThread, discussionList.getChildren());
        
    }
    
    public void doAddDiscussion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ContactView.fxml"));
            this.discussionThread.add(loader.load());
            ((Controller) loader.getController()).setStage(this.getStage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
