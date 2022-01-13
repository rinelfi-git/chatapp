package mg.rinelfi.chat.app.routerComponent;

import java.util.Map;
import javafx.stage.Stage;
import mg.rinelfi.chat.beans.User;
import mg.rinelfi.factory.SocketFactory;
import mg.rinelfi.jiosocket.client.SocketClient;

public abstract class Controller {
    protected SocketFactory socketFactory;
    protected SocketClient socket;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    public SocketFactory getSocketFactory() {
        return socketFactory;
    }

    public void setSocketFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    public SocketClient getSocket() {
        return socket;
    }

    public void setSocket(SocketClient socket) {
        this.socket = socket;
    }
    
    public abstract void startSocket();
}
