package KillerSudoku;

import KillerSudoku.GUI.RootPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

// Bozovic Boban 1/18 C

public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    } // Pocetak programa

    @Override
    public void start(Stage primaryStage) throws Exception {
        initStage(primaryStage);
    }

    private void initStage(Stage primaryStage){ // Inicijalizacija za JavaFX i KillerSudoku.GUI.RootPane
        primaryStage.setResizable(true);
        primaryStage.setTitle("KillerSudoku");
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.setScene(new Scene(new RootPane(primaryStage), 755, 800));
        primaryStage.setOnCloseRequest(e -> {
            System.exit(0);
        });
        primaryStage.show();
    }
}