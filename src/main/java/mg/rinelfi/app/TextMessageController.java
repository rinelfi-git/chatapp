package mg.rinelfi.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
    protected boolean sent, received, seen;
    
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
    
    public boolean isSent() {
        return sent;
    }
    
    public void setSent(boolean sent) {
        this.sent = sent;
        if(sent) {
            this.setReceived(false);
            this.setSeen(false);
        }
    }
    
    public boolean isReceived() {
        return received;
    }
    
    public void setReceived(boolean received) {
        this.received = received;
        if(received) {
            this.setSent(false);
            this.setSeen(false);
        }
    }
    
    public boolean isSeen() {
        return seen;
    }
    
    public void setSeen(boolean seen) {
        this.seen = seen;
        if(seen) {
            this.setReceived(false);
            this.setSent(false);
        }
    }
}
