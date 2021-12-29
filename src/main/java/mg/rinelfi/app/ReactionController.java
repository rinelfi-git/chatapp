package mg.rinelfi.app;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import mg.rinelfi.abstraction.ReactionActionConsumer;
import mg.rinelfi.abstraction.ReactionActionListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReactionController implements Initializable, ReactionActionListener {
    @FXML
    private HBox it;
    private int reaction;
    private List<ReactionActionConsumer> consumers;
    private String[] images;
    
    public void setReaction(int reaction) {
        this.reaction = reaction;
    }
    
    @FXML
    public void doReactHeart() {
        if (this.reaction == 0) this.reaction = -1;
        else this.reaction = 0;
        this.trigger(this.reaction);
        this.loadReaction();
    }
    
    @FXML
    public void doReactLaugh() {
        if (this.reaction == 1) this.reaction = -1;
        else this.reaction = 1;
        this.trigger(this.reaction);
        this.loadReaction();
    }
    
    @FXML
    public void doReactAmazed() {
        if (this.reaction == 2) this.reaction = -1;
        else this.reaction = 2;
        this.trigger(this.reaction);
        this.loadReaction();
    }
    
    @FXML
    public void doReactSad() {
        if (this.reaction == 3) this.reaction = -1;
        else this.reaction = 3;
        this.trigger(this.reaction);
        this.loadReaction();
    }
    
    @FXML
    public void doReactAngry() {
        if (this.reaction == 4) this.reaction = -1;
        else this.reaction = 4;
        this.trigger(this.reaction);
        this.loadReaction();
    }
    
    @FXML
    public void doReactNice() {
        if (this.reaction == 5) this.reaction = -1;
        else this.reaction = 5;
        this.trigger(this.reaction);
        this.loadReaction();
    }
    
    public void loadReaction() {
        for (int i = 0; i < it.getChildren().size(); i++) {
            Button button = (Button) it.getChildren().get(i);
            if (reaction == i) {
                if (!button.getStyleClass().contains("selected")) button.getStyleClass().add("selected");
            } else button.getStyleClass().remove("selected");
        }
    }
    
    public String getImage() {
        if (this.reaction >= 0) return this.images[reaction];
        return null;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.reaction = -1;
        this.consumers = new ArrayList<>();
        this.images = new String[]{
            "love.png",
            "laught.png",
            "amazed.png",
            "sad.png",
            "angry.png",
            "like.png"
        };
    }
    
    @Override
    public void onReact(ReactionActionConsumer consumer) {
        this.consumers.add(consumer);
    }
    
    @Override
    public void trigger(int reaction) {
        this.consumers.forEach(consumer -> consumer.consume(reaction));
    }
}
