package romanentertainmentsoftware.ballsofsteel;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import java.util.ArrayList;

/**
 * Created by Roman Entertainment Software LLC on 4/24/2018.
 */

public class Sound {
    private Context context;
    private final String TAG = "SOUND";
    public int numberOfSounds;
    private ArrayList<MediaPlayer> mp = new ArrayList();

    //Used mainly for MainActivity when leaving app to find out what sounds are currently being played to return to
    public ArrayList<Boolean> wasPlaying = new ArrayList();

    //Used mainly to make sure a sound is played only once
    public ArrayList<Boolean> playing = new ArrayList();

    public ArrayList<Boolean> loopEnabled = new ArrayList();

    public Sound(Context context){
        Log.d(TAG, "Sound() fired");
        this.context = context;
    }

    public void load(int resourceID){
        int currentSound = numberOfSounds;

        MediaPlayer mediaPlayer = MediaPlayer.create(context, resourceID);
        mp.add(mediaPlayer);
        wasPlaying.add(false);
        playing.add(false);
        loopEnabled.add(false);

        numberOfSounds++;
    }

    public void load(int resourceID, boolean repeatable){
        int currentSound = numberOfSounds;

        MediaPlayer mediaPlayer = MediaPlayer.create(context, resourceID);
        mp.add(mediaPlayer);
        mp.get(currentSound).setLooping(repeatable);
        wasPlaying.add(false);
        playing.add(false);

        if (!repeatable)
            loopEnabled.add(false);
        else
            loopEnabled.add(true);

        numberOfSounds++;
    }

    public void playOnce(final int soundNumber){
        if (mp.get(soundNumber) != null) {
            if (!mp.get(soundNumber).isPlaying() && wasPlaying.get(soundNumber) == false && playing.get(soundNumber) == false) {

                mp.get(soundNumber).start();
                wasPlaying.set(soundNumber, true);
                playing.set(soundNumber, true);

                mp.get(soundNumber).setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        Log.d(TAG, "playOnce() " + String.valueOf(soundNumber) + " is complete");
                        wasPlaying.set(soundNumber, false);
                    }
                });
            }
        }
        else{
            Log.d(TAG, String.valueOf(soundNumber) + " is null");
        }
    }

    public void play(final int soundNumber){
        //This plays the sound regardless if it is playing already;
        if (mp.get(soundNumber) != null) {
            mp.get(soundNumber).start();
            wasPlaying.set(soundNumber, true);
            playing.set(soundNumber, true);

            mp.get(soundNumber).setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "playOnce() " + String.valueOf(soundNumber) + " is complete");
                    wasPlaying.set(soundNumber, false);
                    if (loopEnabled.get(soundNumber) == true)
                        playing.set(soundNumber, false);
                }
            });
        }
        else{
            Log.d(TAG, String.valueOf(soundNumber) + " is null");
        }
    }

    public void pause(final int soundNumber){
        mp.get(soundNumber).pause();
    }

    public void stop(final int soundNumber) {
        //This plays the sound regardless if it is playing already;
        if (mp.get(soundNumber) != null) {
            mp.get(soundNumber).pause();
            seekTo(soundNumber, 0);
            mp.get(soundNumber).stop();
            wasPlaying.set(soundNumber, false);
            playing.set(soundNumber, false);
        }
    }

    public void seekTo(final int soundNumber, int milliseconds){
        mp.get(soundNumber).seekTo(milliseconds);
    }

    public void release(){
        if(numberOfSounds > 0) {
            for (int i = 0; i < numberOfSounds; i++) {
                if (wasPlaying.get(i) == true)
                    mp.get(i).stop();
                mp.get(i).release();
            }

            if (mp != null){
                mp.clear();
                mp = null;
            }

            if (wasPlaying != null){
                wasPlaying.clear();
                wasPlaying = null;
            }

            if (playing != null){
                playing.clear();
                playing = null;
            }

            if (loopEnabled != null){
                loopEnabled.clear();
                loopEnabled = null;
            }
        }
    }
}
