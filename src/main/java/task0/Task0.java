package main.java.task0;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.task0.db.LevelDBManager;
import main.java.task0.gui.Task0GUI;

public class Task0 extends Application{
    private Task0GUI graphicInterface;

    public static void main(String[] args) {
        launch(args);
        LevelDBManager.getInstance().close();
    }
    
    public void start(Stage stage) {
        graphicInterface = new Task0GUI();
        
        Scene scene = new Scene(graphicInterface.getOuterVbox());
        stage.setScene(scene);
        stage.show();
    }
}