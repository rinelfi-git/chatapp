package mg.rinelfi.app.routerComponent;

import javafx.stage.Stage;
import mg.rinelfi.beans.User;
import mg.rinelfi.jiosocket.client.TCPClient;

public abstract class Controller {
    protected TCPClient socket;
    protected String token;
    protected User user;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public abstract void startSocket();
}
