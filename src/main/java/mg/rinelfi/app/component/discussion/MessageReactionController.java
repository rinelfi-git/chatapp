package mg.rinelfi.app.component.discussion;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class MessageReactionController {
    @FXML
    private ImageView viewer;
    @FXML
    private Label counterLabel;
    @FXML
    private AnchorPane it;
    private String reaction;
    private int counter;
    
    public String getReaction() {
        return reaction;
    }
    
    public void setReaction(String reaction) {
        this.reaction = reaction;
        this.viewer.setImage(new Image(getClass().getResourceAsStream(this.reaction)));
    }
    
    public int getCounter() {
        return counter;
    }
    
    public void setCounter(int counter) {
        this.counter = counter;
        this.counterLabel.setText(String.valueOf(this.counter));
        if(this.counter <= 1) this.counterLabel.setVisible(false);
        else this.counterLabel.setVisible(true);
    }
    
    public void incrementCounter() {
        this.setCounter(this.counter + 1);
    }
    
    public void decrementCounter() {
        this.setCounter(this.counter - 1);
    }
    
    public AnchorPane getElement() {
        return it;
    }
}
