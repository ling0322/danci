package org.ling0322.danci;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.Preference;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

class Mp3Speech {
    private Mp3Speech() {
        File speech = new File(Config.SPEECH_PATH.concat("/A"));
        mSpeechLibExists = speech.exists();
        mMediaPlayer = new MediaPlayer();
    }
    
    private static Mp3Speech mInstance = null;
    private MediaPlayer mMediaPlayer;
    
    public static Mp3Speech getInstance() {
        if (mInstance == null)
            mInstance = new Mp3Speech();
        
        return mInstance;
    }
    
    public void speak(String word) {
        try {
            File speechFile = getSpeechFile(word);
            if (speechFile == null)
                return;
            
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setVolume(1, 1);
            mMediaPlayer.setDataSource(speechFile.getAbsolutePath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            
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
    
    private boolean mSpeechLibExists;
    public boolean speechLibExists() {
        return mSpeechLibExists;
    }
}

class Speech implements OnInitListener {
    private TextToSpeech mTextToSpeech;
    private boolean mIsAvailable = false;

    public Speech(Activity activity) {
        mTextToSpeech = new TextToSpeech(activity, this);
    }
    
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int supported = mTextToSpeech.setLanguage(Locale.US);
            if (supported > 0)
                mIsAvailable = true;
        }
    }
    
    public void speak(String word) {
        if (mIsAvailable == true) {
            mTextToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
