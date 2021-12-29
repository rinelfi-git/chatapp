package mg.rinelfi.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import mg.rinelfi.abstraction.ReactionActionListener;
import mg.rinelfi.abstraction.ReactionRequestConsumer;

import java.util.List;

public abstract class TextMessageController {
    protected int myReaction;
    protected List<ReactionRequestConsumer> owners;
    protected HBox reactionPanel;
    protected ReactionController reactionController;
    
    public void setReactionPanel(HBox reactionPanel) {
        this.reactionPanel = reactionPanel;
    }
    
    public ReactionController getReactionController() {
        return reactionController;
    }
    
    public void setReactionController(ReactionController reactionController) {
        this.reactionController = reactionController;
    }
}
