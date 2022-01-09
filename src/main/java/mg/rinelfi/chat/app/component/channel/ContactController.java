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
import mg.rinelfi.chat.beans.Discussion;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class ContactController implements ContactRightClickListener, ContactLeftClickListener, Initializable {
    private Discussion discussion;
    @FXML
    private Label username, messageView;
    @FXML
    private BorderPane it;
    private ContactLeftClickConsumer leftClickConsumer;
    private ContactRightClickConsumer rightClickConsumers;
    private List<String> tokens;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.tokens = new ArrayList<>();
    }
    
    @FXML
    public void doOpenDiscussionAction(MouseEvent event) throws IOException {
        if (MouseButton.SECONDARY == event.getButton()) {
            this.triggerContactRightClick(this.discussion);
        } else if (MouseButton.PRIMARY == event.getButton()) {
            this.triggerContaLeftClick(this.discussion.getUser().getUsername(), this.tokens);
        }
    }
    
    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
        this.update();
    }
    
    public Discussion getDiscussion() {return this.discussion;}
    
    public BorderPane getIt() {
        return it;
    }
    
    public Label getMessageView() {return this.messageView;}
    
    @Override
    public void onContactRightClick(ContactRightClickConsumer consumer) {
        this.rightClickConsumers = consumer;
    }
    
    @Override
    public void triggerContactRightClick(Discussion data) {
        this.rightClickConsumers.consumeContactRightClick(data);
    }
    
    @Override
    public void onContactLeftClick(ContactLeftClickConsumer consumer) {
        this.leftClickConsumer = consumer;
    }
    
    @Override
    public void triggerContaLeftClick(String username, List<String> tokens) {
        this.leftClickConsumer.consumeContactLeftClick(username, tokens);
    }
    
    public void update() {
        this.username.setText(discussion.getUser().getUsername());
        this.messageView.setText(this.discussion.getMessage());
    }

    public List<String> getTokens() {
        return tokens;
    }
    
}
