package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private boolean isServiceConnected;
    private MusicService musicService;
    private ImageView btnPlay, btnStopService, imgSong, btnSkip, btnReturn;
    private Animation animation;
    private SeekBar timeLine;
    private TextView tvTimePlay, tvTimeOut;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            MusicService.MyBinder myBinder = (MusicService.MyBinder) iBinder;
            musicService = myBinder.getMyService();
            isServiceConnected = true;

            Time();
            TimeOut();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            isServiceConnected = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = findViewById(R.id.btnPlay);
        btnStopService = findViewById(R.id.btnBack);
        btnSkip = findViewById(R.id.btnSkip);
        btnReturn = findViewById(R.id.btnReturn);
        imgSong = findViewById(R.id.img_song);
        timeLine = findViewById(R.id.timeLine);
        tvTimePlay = findViewById(R.id.timePlay);
        tvTimeOut = findViewById(R.id.timeOut);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anirotate);

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServiceConnected){
                    musicService.skipMusic(musicService.getMediaPlayer().getCurrentPosition() - 5000);
                }else{
                    Toast.makeText(MainActivity.this, "Service chưa hoạt động", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServiceConnected){
                    musicService.skipMusic(musicService.getMediaPlayer().getCurrentPosition() + 10000);
                }else{
                    Toast.makeText(MainActivity.this, "Service chưa hoạt động", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStopService();
            }
        });

        final Intent intent = new Intent(MainActivity.this, MusicService.class);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServiceConnected){
                    if(musicService.isPlaying()) {
                        musicService.pauseMusic();
                        btnPlay.setImageResource(R.drawable.ic_play);
                        imgSong.clearAnimation();
                    }else{
                        musicService.playMusic();
                        btnPlay.setImageResource(R.drawable.ic_pause);
                        imgSong.startAnimation(animation);
                    }
                    Time();
                    TimeOut();
                }else{
                    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    imgSong.startAnimation(animation);
                }
            }
        });

        timeLine.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicService.getMediaPlayer().seekTo(timeLine.getProgress());
            }
        });
    }

    private void onClickStopService() {
        if(musicService.isPlaying()){
            unbindService(serviceConnection);
            isServiceConnected = false;
            btnPlay.setImageResource(R.drawable.ic_play);
            imgSong.clearAnimation();
        }else{
            unbindService(serviceConnection);
            isServiceConnected = false;
        }
    }

    private void TimeOut(){
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        tvTimeOut.setText(format.format(musicService.getMediaPlayer().getDuration()));
        timeLine.setMax(musicService.getMediaPlayer().getDuration());
    }

    private void Time(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat format = new SimpleDateFormat("mm:ss");
                tvTimePlay.setText(format.format(musicService.getMediaPlayer().getCurrentPosition()));
                timeLine.setProgress(musicService.getMediaPlayer().getCurrentPosition());
                musicService.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        imgSong.clearAnimation();
                        btnPlay.setImageResource(R.drawable.ic_play);
                        Time();
                        TimeOut();
                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 10);
    }
}