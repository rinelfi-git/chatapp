package mg.rinelfi.app;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mg.rinelfi.Launcher;
import mg.rinelfi.beans.Discussion;
import mg.rinelfi.jiosocket.client.TCPClient;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DiscussionThreadController extends Controller implements Initializable {
    TCPClient socket;
    @FXML
    private AnchorPane contactOption, sessionOption;
    @FXML
    private VBox mainSession;
    @FXML
    private VBox discussionList;
    private ObservableList<Node> discussionThread;
    
    @Override
    public void setStage(Stage stage) {
        super.stage = stage;
        super.stage.setTitle("Rinelfi - chat app");
        for(int i = 0; i < 12; i++) {
            this.addDiscussion();
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * GUI component initiation
         */
        this.discussionThread = FXCollections.observableArrayList();
        Bindings.bindContentBidirectional(discussionThread, discussionList.getChildren());
        mainSession.toFront();
        sessionOption.toBack();
        contactOption.toBack();
        
        /**
         * Initiation of socket use
         */
        this.socket = new TCPClient("localhost", 21345);
        this.socket.onConnection(callback -> {
            System.out.println("Connection");
        }).emit("client.server", "down");
        this.socket.connect();
    }
    
    @FXML
    public void doOpenSessionOption() {
        mainSession.toBack();
        sessionOption.toFront();
        contactOption.toBack();
    }
    
    @FXML
    public void doCloseOptions() {
        mainSession.toFront();
        sessionOption.toBack();
        contactOption.toBack();
    }
    
    @FXML
    public void doDisconnect(MouseEvent event)  throws IOException {
        if(MouseButton.PRIMARY == event.getButton()) {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/AuthenticationView.fxml"));
            Parent view = loader.load();
            ((Controller) loader.getController()).setStage(this.getStage());
            Scene scene = new Scene(view);
            this.getStage().setScene(scene);
        }
    }
    
    private void addDiscussion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ContactView.fxml"));
            this.discussionThread.add(loader.load());
            ContactController contact = loader.getController();
            contact.setStage(this.getStage());
            contact.setDiscussion(new Discussion());
            contact.addObserver(discussion -> {
                mainSession.toBack();
                sessionOption.toBack();
                contactOption.toFront();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
