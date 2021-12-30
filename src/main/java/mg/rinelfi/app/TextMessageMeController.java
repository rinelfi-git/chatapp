package mg.rinelfi.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import mg.rinelfi.abstraction.ReactionActionConsumer;
import mg.rinelfi.abstraction.ReactionActionListener;
import mg.rinelfi.abstraction.ReactionRequestConsumer;
import mg.rinelfi.abstraction.ReactionRequestListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class TextMessageMeController extends TextMessageController implements Initializable, ReactionRequestListener {
    @FXML
    private Label message;
    @FXML
    private HBox reactionContainer;
    @FXML
    private ImageView reactionThumbnail;
    private static final String defaultSticker = "/mg/rinelfi/img/reaction/sticker_me.png";
    
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
            System.out.println("execution");
            this.myReaction = reaction;
            if (this.myReaction >= 0) {
                String image = "/mg/rinelfi/img/reaction/" + this.reactionImages[reaction];
                boolean alreadyExists = false;
                for (DiscussionReactionController consumer : this.reactions) {
                    if (consumer.getReaction().equals(image)) {
                        System.out.println("exists : " + consumer);
                        alreadyExists = true;
                        consumer.incrementCounter();
                    }
                }
                if (!alreadyExists) this.addReaction(image);
            } else this.removeReaction(this.myReaction);
        });
    }
    
    private void removeReaction(int reaction) {
        String image = "/mg/rinelfi/img/reaction/" + this.reactionImages[Math.abs(reaction) - 1];
        for (DiscussionReactionController consumer : this.reactions) {
            if (consumer.getReaction().equals(image)) {
                consumer.decrementCounter();
                if(consumer.getCounter() == 0) {
                    this.reactionContainer.getChildren().remove(consumer.getElement());
                    this.reactions.remove(consumer);
                    System.out.println("deletion : " + this.reactions);
                    break;
                }
            }
        }
    }
    
    @Override
    public void onReactionRequest(ReactionRequestConsumer consumer) {
        this.owners.add(consumer);
    }
    
    @Override
    public void trigger(int reaction) {
        this.owners.forEach(reactionRequestConsumer -> reactionRequestConsumer.consume(reaction));
    }
    
    @Override
    protected void addReaction(String image) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/rinelfi/app/DiscussionReactionView.fxml"));
            AnchorPane pane = loader.load();
            DiscussionReactionController controller = loader.getController();
            controller.setReaction(image);
            controller.setCounter(1);
            reactionContainer.getChildren().add(0, pane);
            this.reactions.add(controller);
            System.out.println("addition : " + this.reactions + " => " + Arrays.toString(this.reactions.toArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
