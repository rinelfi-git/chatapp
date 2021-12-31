package mg.rinelfi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mg.rinelfi.app.container.Controller;

public class Launcher extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Chat App");
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/mg/rinelfi/app/container/AuthenticationView.fxml"));
        Parent parent = loader.load();
        ((Controller)loader.getController()).setStage(primaryStage);
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
