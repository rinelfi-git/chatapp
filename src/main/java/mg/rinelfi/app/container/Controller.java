package mg.rinelfi.app.container;

import javafx.stage.Stage;
import mg.rinelfi.jiosocket.client.TCPClient;

public abstract class Controller {
    private TCPClient socket;
    protected String token;
    protected Stage stage;
    
    public Stage getStage() {
        return stage;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public TCPClient getSocket() {
        return socket;
    }
    
    public void setSocket(TCPClient socket) {
        this.socket = socket;
    }
    
    protected abstract void startSocket();
}
