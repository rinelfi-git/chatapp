package mg.rinelfi.app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TextMessageMeController{
    @FXML
    private Label message;
    
    public void setMessage(String string) {
        this.message.setText(string);
    }
}
