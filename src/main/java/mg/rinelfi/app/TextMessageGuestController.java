package mg.rinelfi.app;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import mg.rinelfi.abstraction.ReactionRequestListener;
import mg.rinelfi.abstraction.ReactionRequestConsumer;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TextMessageGuestController extends TextMessageController implements Initializable, ReactionRequestListener {
    @FXML
    private Label message;
    private static final String defaultSticker = "/mg/rinelfi/img/reaction/sticker_guest.png";
    
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
