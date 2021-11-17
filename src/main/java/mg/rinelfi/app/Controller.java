package mg.rinelfi.app;

import javafx.stage.Stage;

public abstract class Controller {
    protected Stage stage;
    
    public Stage getStage() {
        return stage;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
