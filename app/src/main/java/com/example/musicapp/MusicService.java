package com.example.musicapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {

    private MyBinder myBinder = new MyBinder();
    private MediaPlayer mediaPlayer;

    public class MyBinder extends Binder {
        MusicService getMyService() {
            return MusicService.this;
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.codondanhchoai);
    }

    @Override
    public IBinder onBind(Intent intent) {
        mediaPlayer.start();
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.reset();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void playMusic() {
        mediaPlayer.start();
    }

    public void pauseMusic() {
        mediaPlayer.pause();
    }

    public void skipMusic(int pos){
        mediaPlayer.seekTo(pos);
    }
}