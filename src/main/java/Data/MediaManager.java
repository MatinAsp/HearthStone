package Data;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MediaManager {
    static private MediaManager mediaManager = null;
    private ArrayList<MyMediaPlayer> mediaPlayers = new ArrayList<>();
    private String audiosAddress;
    private double volume;
    private MediaManager() {
        volume = GameSettings.getInstance().getVolume();
        audiosAddress = GameConstants.getInstance().getString("audiosAddress");
    }

    static synchronized public MediaManager getInstance() {
        if(mediaManager == null){
            mediaManager = new MediaManager();
        }
        return mediaManager;
    }

    public synchronized void setVolume(double volume){
        this.volume = volume;
        for(MyMediaPlayer mediaPlayer: mediaPlayers){
            mediaPlayer.setVolume(volume);
        }
    }

    public synchronized void playMedia(String fileName, int cycleCount){
        Media media = new Media(Paths.get(audiosAddress + File.separator + fileName).toUri().toString());
        MyMediaPlayer mediaPlayer = new MyMediaPlayer(fileName, media);
        mediaPlayer.setCycleCount(cycleCount);
        mediaPlayer.setVolume(volume);
        mediaPlayers.add(mediaPlayer);
        mediaPlayer.play();
    }

    public synchronized void stopMedia(String fileName){
        for(MyMediaPlayer mediaPlayer: mediaPlayers){
            if (mediaPlayer.getFileName().equals(fileName)){
                mediaPlayer.stop();
                mediaPlayers.remove(mediaPlayer);
                break;
            }
        }
    }
}
