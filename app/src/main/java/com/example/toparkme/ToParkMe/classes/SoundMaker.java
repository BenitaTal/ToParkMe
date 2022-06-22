package com.example.toparkme.ToParkMe.classes;


import android.content.ContextWrapper;
import android.media.MediaPlayer;

public class SoundMaker {

    private MediaPlayer mp;

    public SoundMaker() {
        this.mp = new MediaPlayer();
    }


    public MediaPlayer getMp() {
        return this.mp;
    }

    public void setMpAndPlay(ContextWrapper cw, int sample) {
        this.mp = MediaPlayer.create(cw,sample);
        this.mp.start();
    }
}