package mg.rinelfi.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import mg.rinelfi.abstraction.ReactionActionListener;
import mg.rinelfi.abstraction.ReactionRequestConsumer;

import java.io.IOException;
import java.util.List;

public abstract class TextMessageController {
    protected int myReaction;
    protected List<ReactionRequestConsumer> owners;
    protected List<DiscussionReactionController> reactions;
    protected HBox reactionPanel;
    protected ReactionController reactionController;
    protected String[] reactionImages;
    
    public void setReactionPanel(HBox reactionPanel) {
        this.reactionPanel = reactionPanel;
    }
    
    public ReactionController getReactionController() {
        return reactionController;
    }
    
    public void setReactionController(ReactionController reactionController) {
        this.reactionController = reactionController;
    }
    
    protected void addReaction(HBox reactionContainer, String image) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/rinelfi/app/DiscussionReactionView.fxml"));
            AnchorPane pane = loader.load();
            DiscussionReactionController controller = loader.getController();
            controller.setReaction(image);
            controller.setCounter(1);
            reactionContainer.getChildren().add(0, pane);
            this.reactions.add(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    protected void removeReaction(HBox reactionContainer, int reaction) {
        String image = "/mg/rinelfi/img/reaction/" + this.reactionImages[Math.abs(reaction) - 1];
        for (DiscussionReactionController consumer : this.reactions) {
            if (consumer.getReaction().equals(image)) {
                consumer.decrementCounter();
                if (consumer.getCounter() == 0) {
                    reactionContainer.getChildren().remove(consumer.getElement());
                    this.reactions.remove(consumer);
                    break;
                }
            }
        }
    }
}
