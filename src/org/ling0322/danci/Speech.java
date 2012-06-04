package org.ling0322.danci;

import java.io.File;

import android.media.MediaPlayer;
import android.util.Log;

public class Speech {
    private Speech() {
        File speech = new File(Config.SPEECH_PATH.concat("/A"));
        speechLibExists = speech.exists();
        
        mediaPlayer = new MediaPlayer();
    }
    
    private static Speech instance = null;
    private MediaPlayer mediaPlayer;
    
    public static Speech getInstance() {
        if (instance == null)
            instance = new Speech();
        
        return instance;
    }
    
    public void speak(String word) {
        try {
            File speechFile = getSpeechFile(word);
            if (speechFile == null)
                return;
            
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setVolume(1, 1);
            mediaPlayer.setDataSource(speechFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            
        } catch (Exception e) {
            Log.d("speech", String.format("speak word %s failed", word));
        }
    }
    
    public boolean isSpeechExist(String word) {
        if (speechLibExists() == false)
            return false;
        
        if (null == getSpeechFile(word))
            return false;
        else
            return true;
    }
    
    private File getSpeechFile(String word) {
        word = word.toLowerCase();
        File speechFile = new File(String.format("%s/%c/%s.mp3", Config.SPEECH_PATH, word.charAt(0), word));
        if (speechFile.exists() == true)
            return speechFile;
        else
            return null;
    }
    
    private boolean speechLibExists;
    public boolean speechLibExists() {
        return speechLibExists;
    }
}
