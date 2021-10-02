package com.example.musicapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service{

    private MyBinder myBinder = new MyBinder();
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Music", "MyService onBind");
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Music", "MyService onStartCommand");
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            Song song = (Song) bundle.get("object_song");
            startMusic(song);
        }

        return START_NOT_STICKY;
    }

    private void startMusic(Song song) {
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(), song.getResource());
        }

        mediaPlayer.start();
        isPlaying = true;
    }

    public void pauseMusic(){
        if(mediaPlayer != null && isPlaying){
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public void resumeMusic(){
        if(mediaPlayer != null && !isPlaying){
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    @Override
    public void onDestroy() {
        Log.e("Music", "MyService onDestroy");
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    public class MyBinder extends Binder{
        MusicService getMyService (){
            return MusicService.this;
        }
    }

}
