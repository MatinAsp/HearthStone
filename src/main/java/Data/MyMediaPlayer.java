package Data;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MyMediaPlayer {
    private MediaPlayer mediaPlayer;
    private String fileName;
    public MyMediaPlayer(String fileName, Media media) {
        mediaPlayer = new MediaPlayer(media);
        this.fileName = fileName;
    }

    public void setCycleCount(int cycleCount){
        mediaPlayer.setCycleCount(cycleCount);
    }

    public void play(){
        mediaPlayer.play();
    }

    public void stop(){
        mediaPlayer.stop();
    }

    public String getFileName() {
        return fileName;
    }

    public void setVolume(double volume) {
        mediaPlayer.setVolume(volume);
    }
}
