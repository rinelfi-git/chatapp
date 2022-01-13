package mg.rinelfi.chat.app.component.channel;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import mg.rinelfi.chat.observer.ContactLeftClickConsumer;
import mg.rinelfi.chat.observer.ContactLeftClickListener;
import mg.rinelfi.chat.observer.ContactRightClickConsumer;
import mg.rinelfi.chat.observer.ContactRightClickListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import mg.rinelfi.chat.beans.User;

public class ContactController implements ContactRightClickListener, ContactLeftClickListener, Initializable {
    @FXML
    private Label title, lastMessage;
    @FXML
    private BorderPane it;
    private ContactLeftClickConsumer leftClickConsumer;
    private ContactRightClickConsumer rightClickConsumers;
    
    private Map<String, String> tokens;
    private long id;
    private List<User> members;

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tokens = new HashMap<>();
        this.members = new ArrayList<>();
    }
    
    @FXML
    public void doOpenDiscussionAction(MouseEvent event) throws IOException {
        if (MouseButton.SECONDARY == event.getButton()) {
            this.triggerContactRightClick(this.id);
        } else if (MouseButton.PRIMARY == event.getButton()) {
            System.out.println("contect");
            this.triggerContaLeftClick(this.id, this.title.getText(), this.tokens);
        }
    }
    
    public BorderPane getIt() {
        return it;
    }

    public List<User> getMembers() {
        return members;
    }
    
    @Override
    public void onContactRightClick(ContactRightClickConsumer consumer) {
        this.rightClickConsumers = consumer;
    }
    
    @Override
    public void triggerContactRightClick(long channel) {
        this.rightClickConsumers.consumeContactRightClick(channel);
    }
    
    @Override
    public void onContactLeftClick(ContactLeftClickConsumer consumer) {
        this.leftClickConsumer = consumer;
    }
    
    @Override
    public void triggerContaLeftClick(long id, String username, Map<String, String> tokens) {
        this.leftClickConsumer.consumeContactLeftClick(id, username, tokens);
    }

    public Map<String, String> getTokens() {
        return tokens;
    }

    public Label getTitle() {
        return title;
    }

    public Label getLastMessage() {
        return lastMessage;
    }
    
    
}
