package Graphics;

import Log.LogCenter;
import Logic.PlayersManager;
import Models.Cards.Card;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sun.awt.SunToolkit;

import java.io.File;
import java.nio.file.Paths;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage = FXMLLoader.load(getClass().getResource("Graphics.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if(PlayersManager.getInstance().getCurrentPlayer() != null){
            PlayersManager.getInstance().getCurrentPlayer().saveData();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
