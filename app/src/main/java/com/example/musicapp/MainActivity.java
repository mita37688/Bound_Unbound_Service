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

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private boolean isServiceConnected;
    private MusicService musicService;
    private ImageView btnPlay, btnStartService, btnStopService, imgSong;
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
        btnStartService = findViewById(R.id.btnPlayService);
        btnStopService = findViewById(R.id.btnBack);
        imgSong = findViewById(R.id.img_song);
        timeLine = findViewById(R.id.timeLine);
        tvTimePlay = findViewById(R.id.timePlay);
        tvTimeOut = findViewById(R.id.timeOut);

        btnPlay.setVisibility(View.INVISIBLE);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anirotate);

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStartService();
            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStopService();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicService.isPlaying()) {
                    musicService.pauseMusic();
                    btnPlay.setImageResource(R.drawable.ic_play);
                    imgSong.clearAnimation();
                }else{
                    musicService.resumeMusic();
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    imgSong.startAnimation(animation);
                }

                Time();
                TimeOut();
            }
        });

        timeLine.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if(musicService.getMediaPlayer() != null && fromUser)
//                    musicService.getMediaPlayer().seekTo(progress * 1000);
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

    private void onClickStartService() {
        Intent intent = new Intent(this, MusicService.class);

        Song song = new Song("Trắc trở", R.raw.tractro);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", song);
        intent.putExtras(bundle);

        startService(intent);

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        btnPlay.setImageResource(R.drawable.ic_pause);
        btnPlay.setVisibility(View.VISIBLE);
        btnStartService.setVisibility(View.GONE);

        imgSong.startAnimation(animation);
    }

    private void onClickStopService() {
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);

        if(isServiceConnected){
            unbindService(serviceConnection);
            isServiceConnected = false;
        }

        btnPlay.setVisibility(View.INVISIBLE);
        btnStartService.setVisibility(View.VISIBLE);

        imgSong.clearAnimation();
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
                        btnPlay.setVisibility(View.INVISIBLE);
                        btnStartService.setVisibility(View.VISIBLE);
                        Time();
                        TimeOut();
                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 100);
    }
}