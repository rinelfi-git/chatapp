package mg.rinelfi.chat.app.component.discussion;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import mg.rinelfi.chat.observer.ReactionActionConsumer;
import mg.rinelfi.chat.observer.ReactionActionListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings("restriction")
public class ReactionController implements Initializable, ReactionActionListener {
    @FXML
    private HBox it;
    private int reaction;
    private List<ReactionActionConsumer> consumers;
    
    public void setReaction(int reaction) {
        this.reaction = reaction;
    }
    
    @FXML
    public void doReactHeart() {
        if(this.reaction >= 0){
            int temp = this.reaction * (-1) - 1;
            this.trigger(temp);
        }
        if (this.reaction == 0) this.reaction = -1;
        else this.reaction = 0;
        this.trigger(this.reaction);
        this.loadReaction();
    }
    
    @FXML
    public void doReactLaugh() {
        if(this.reaction >= 0){
            int temp = this.reaction * (-1) - 1;
            this.trigger(temp);
        }
        if (this.reaction == 1) this.reaction = -2;
        else this.reaction = 1;
        this.trigger(this.reaction);
        this.loadReaction();
    }
    
    @FXML
    public void doReactAmazed() {
        if(this.reaction >= 0){
            int temp = this.reaction * (-1) - 1;
            this.trigger(temp);
        }
        if (this.reaction == 2) this.reaction = -3;
        else this.reaction = 2;
        this.trigger(this.reaction);
        this.loadReaction();
    }
    
    @FXML
    public void doReactSad() {
        if(this.reaction >= 0){
            int temp = this.reaction * (-1) - 1;
            this.trigger(temp);
        }
        if (this.reaction == 3) this.reaction = -4;
        else this.reaction = 3;
        this.trigger(this.reaction);
        this.loadReaction();
    }
    
    @FXML
    public void doReactAngry() {
        if(this.reaction >= 0){
            int temp = this.reaction * (-1) - 1;
            this.trigger(temp);
        }
        if (this.reaction == 4) this.reaction = -5;
        else this.reaction = 4;
        this.trigger(this.reaction);
        this.loadReaction();
    }
    
    @FXML
    public void doReactNice() {
        if(this.reaction >= 0){
            int temp = this.reaction * (-1) - 1;
            this.trigger(temp);
        }
        if (this.reaction == 5) this.reaction = -6;
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
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.reaction = -1;
        this.consumers = new ArrayList<>();
    }
    
    @Override
    public void onReact(ReactionActionConsumer consumer) {
        this.consumers.add(consumer);
    }
    
    @Override
    public void resetOnReact() {
        this.consumers.clear();
    }
    
    @Override
    public void trigger(int reaction) {
        for (ReactionActionConsumer consumer : this.consumers) {
            consumer.consumeReactionAction(reaction);
        }
    }
}
