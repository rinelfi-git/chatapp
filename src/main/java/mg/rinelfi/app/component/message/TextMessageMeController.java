package mg.rinelfi.app.component.message;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import mg.rinelfi.abstraction.observer.ReactionActionListener;
import mg.rinelfi.abstraction.observer.ReactionRequestConsumer;
import mg.rinelfi.abstraction.observer.ReactionRequestListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TextMessageMeController extends TextMessageController implements Initializable, ReactionRequestListener {
    @FXML
    private Label message;
    @FXML
    private HBox reactionContainer;
    @FXML
    private Button more, reply, react;
    @FXML
    private ImageView messageStatusView;
    
    private static final String[] messageStatusImages = new String[]{
        "/mg/rinelfi/img/message/status/sent.png",
        "/mg/rinelfi/img/message/status/received.png",
        "/mg/rinelfi/img/message/status/seen.png"
    };
    protected boolean sent, received, seen;
    
    public void setMessage(String string) {
        this.message.setText(string);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.myReaction = -1;
        this.owners = new ArrayList<>();
        this.reactions = new ArrayList<>();
        this.reactionImages = new String[]{
            "love.png",
            "laught.png",
            "amazed.png",
            "sad.png",
            "angry.png",
            "like.png"
        };
        this.messageStatusView.setVisible(false);
    }
    
    @FXML
    public void doReactOn() {
        trigger(this.myReaction);
        ((ReactionActionListener) reactionController).onReact(reaction -> {
            this.myReaction = reaction;
            if (this.myReaction >= 0) {
                String image = "/mg/rinelfi/img/reaction/" + this.reactionImages[reaction];
                boolean alreadyExists = false;
                for (MessageReactionController consumer : this.reactions) {
                    if (consumer.getReaction().equals(image)) {
                        alreadyExists = true;
                        consumer.incrementCounter();
                    }
                }
                if (!alreadyExists) this.addReaction(this.reactionContainer, image);
            } else this.removeReaction(this.reactionContainer, this.myReaction);
        });
    }
    
    @Override
    public void onReactionRequest(ReactionRequestConsumer consumer) {
        this.owners.add(consumer);
    }
    
    @Override
    public void trigger(int reaction) {
        this.owners.forEach(reactionRequestConsumer -> reactionRequestConsumer.consume(reaction));
    }
    
    @FXML
    public void doMouseEntered() {
        this.more.setVisible(true);
        this.reply.setVisible(true);
        this.react.setVisible(true);
    }
    
    @FXML
    public void doMouseExited() {
        this.more.setVisible(false);
        this.reply.setVisible(false);
        this.react.setVisible(false);
    }
    
    public boolean isSent() {
        return sent;
    }
    
    public boolean isReceived() {
        return received;
    }
    
    public boolean isSeen() {
        return seen;
    }
    
    public void setSent(boolean sent) {
        this.sent = sent;
        if (sent) {
            this.setReceived(false);
            this.setSeen(false);
            this.messageStatusView.setImage(new Image(getClass().getResourceAsStream(this.messageStatusImages[0])));
            this.messageStatusView.setVisible(true);
        }
    }
    
    public void setReceived(boolean received) {
        this.received = received;
        if (received) {
            this.setSent(false);
            this.setSeen(false);
            this.messageStatusView.setImage(new Image(getClass().getResourceAsStream(this.messageStatusImages[1])));
            this.messageStatusView.setVisible(true);
        }
    }
    
    public void setSeen(boolean seen) {
        this.seen = seen;
        if (seen) {
            this.setReceived(false);
            this.setSent(false);
            this.messageStatusView.setImage(new Image(getClass().getResourceAsStream(this.messageStatusImages[2])));
            this.messageStatusView.setVisible(true);
        }
    }
    
    public ImageView getMessageStatusView() {
        return messageStatusView;
    }
}