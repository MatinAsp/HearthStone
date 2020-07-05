package Graphics;

import Logic.PlayersManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
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
