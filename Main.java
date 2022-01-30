package COSC1047.EditorPlus;

import javafx.application.Application;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.File;

/**
 * Program Controller
 * */
public class Main extends Application{

    final int SCREEN_WIDTH = 800;
    final int SCREEN_HEIGHT = 550;

    @Override
    public void start(Stage primaryStage) {

        //Pane p = new Pane();
        UI ui = new UI();
        VBox mainPane = (VBox)ui.UIInitializer();


        /** initialize primary stage */
        Scene mainScene = new Scene(mainPane,SCREEN_WIDTH,SCREEN_HEIGHT);
        //primaryStage.setMaxWidth(SCREEN_WIDTH);
        //primaryStage.setMaxHeight(SCREEN_HEIGHT);
        primaryStage.setMinWidth(SCREEN_WIDTH);
        primaryStage.setMinHeight(SCREEN_HEIGHT);
        primaryStage.setTitle("EditorPlus\tAlpha version\tAuthor: Matthew Yan");
        primaryStage.setScene(mainScene);
        primaryStage.show();

    }// end of start

    public static void main(String[] args){
        launch(args);
    }// end of main

}// end of Main class
