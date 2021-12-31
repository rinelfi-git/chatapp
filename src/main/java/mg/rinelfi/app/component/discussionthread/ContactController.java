package mg.rinelfi.app.component.discussionthread;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import mg.rinelfi.abstraction.Observable;
import mg.rinelfi.abstraction.Observer;
import mg.rinelfi.app.container.Controller;
import mg.rinelfi.beans.Discussion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactController extends Controller implements Observable {
    private List<Observer> observers;
    private Discussion discussion;
    @FXML
    private Label username, textMessage;
    
    @FXML
    public void doOpenDiscussionAction(MouseEvent event) throws IOException {
        if (MouseButton.SECONDARY == event.getButton()) {
            this.update(this.discussion);
        } else if (MouseButton.PRIMARY == event.getButton()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mg/rinelfi/app/container/TextDiscussionView.fxml"));
            Parent view = loader.load();
            ((Controller) loader.getController()).setStage(this.getStage());
            Scene scene = new Scene(view);
            this.getStage().setScene(scene);
        }
    }
    
    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }
    
    @Override
    public void addObserver(Observer observer) {
        if(this.observers == null) this.observers = new ArrayList<>();
        this.observers.add(observer);
    }
    
    @Override
    public void update(Object data) {
        this.observers.forEach(observer -> observer.update(data));
    }
    
    @Override
    protected void startSocket() {
    
    }
}
