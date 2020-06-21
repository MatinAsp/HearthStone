package Graphics;

import Logic.PlayersManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //URL url = new File("src/main/resources/Graphics/Graphics.fxml").toURI().toURL();
        //primaryStage = FXMLLoader.load(url);
        primaryStage = FXMLLoader.load(getClass().getResource("Graphics.fxml"));

        // primaryStage = FXMLLoader.load(getClass().getClassLoader().getResource("Graphics.fxml"));
     //   getClass().getClassLoader().getResource("ui_layout.fxml")

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
