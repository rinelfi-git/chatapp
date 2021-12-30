package mg.rinelfi.app;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import mg.rinelfi.abstraction.ReactionActionListener;
import mg.rinelfi.abstraction.ReactionRequestConsumer;
import mg.rinelfi.abstraction.ReactionRequestListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TextMessageGuestController extends TextMessageController implements Initializable, ReactionRequestListener {
    @FXML
    private Label message;
    @FXML
    private HBox reactionContainer;
    @FXML
    private Button more, reply, react;
    
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
    }
    
    @FXML
    public void doReactOn() {
        trigger(this.myReaction);
        ((ReactionActionListener) reactionController).onReact(reaction -> {
            this.myReaction = reaction;
            if (this.myReaction >= 0) {
                String image = "/mg/rinelfi/img/reaction/" + this.reactionImages[reaction];
                boolean alreadyExists = false;
                for (DiscussionReactionController consumer : this.reactions) {
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
}
