package mg.rinelfi.app;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mg.rinelfi.abstraction.ReactionActionListener;
import mg.rinelfi.abstraction.ReactionRequestListener;
import mg.rinelfi.abstraction.ReactionRequestConsumer;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TextMessageMeController extends TextMessageController implements Initializable, ReactionRequestListener {
    @FXML
    private Label message;
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
    }
    
    @FXML
    public void doReactOn() {
        trigger(this.myReaction);
        ((ReactionActionListener) reactionController).onReact(reaction -> {
            this.myReaction = reaction;
            String image = this.reactionController.getImage() == null ? TextMessageMeController.defaultSticker : "/mg/rinelfi/img/reaction/" + this.reactionController.getImage();
            this.reactionThumbnail.setImage(new Image(getClass().getResourceAsStream(image)));
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
}
