package mg.rinelfi.app.component.discussionthread;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import mg.rinelfi.abstraction.observer.ContactLeftClickConsumer;
import mg.rinelfi.abstraction.observer.ContactLeftClickListener;
import mg.rinelfi.abstraction.observer.ContactRightClickConsumer;
import mg.rinelfi.abstraction.observer.ContactRightClickListener;
import mg.rinelfi.beans.Discussion;

import java.io.IOException;

public class ContactController implements ContactRightClickListener, ContactLeftClickListener {
    private Discussion discussion;
    @FXML
    private Label username, textMessage;
    private ContactLeftClickConsumer leftClickConsumer;
    private ContactRightClickConsumer rightClickConsumers;
    
    @FXML
    public void doOpenDiscussionAction(MouseEvent event) throws IOException {
        if (MouseButton.SECONDARY == event.getButton()) {
            this.triggerContactRightClick(this.discussion);
        } else if (MouseButton.PRIMARY == event.getButton()) {
            this.triggerContaLeftClick(this.discussion);
        }
    }
    
    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
        this.update();
    }
    
    public Discussion getDiscussion() {return this.discussion;}
    
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
    public void triggerContaLeftClick(Discussion data) {
        this.leftClickConsumer.consumeContactLeftClick(data);
    }
    
    public void update() {
        this.username.setText(discussion.getUser().getUsername());
        this.textMessage.setText(this.discussion.getMessage());
    }
}
