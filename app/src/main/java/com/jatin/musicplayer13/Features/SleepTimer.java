package com.jatin.musicplayer13.Features;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jatin.musicplayer13.R;

public class SleepTimer extends AppCompatActivity {


    private Handler mhandler;
        MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleeptimer);




        SeekBar SleepTimerBar= (SeekBar) findViewById(R.id.TimerBar);
        SleepTimerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            progressChangedValue=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(SleepTimer.this,"Timer is at : " +progressChangedValue,Toast.LENGTH_SHORT).show();

            }
        });


        Button setTimer=(Button) findViewById(R.id.SetTimer);
        setTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SleepTimer.this,"Set Timer",Toast.LENGTH_SHORT).show();

            }
        });

        Button cancelTimer=(Button) findViewById(R.id.CancelTimer);
        cancelTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SleepTimer.this, "Cancel Timer" ,Toast.LENGTH_SHORT).show();

            }
        });

/*

        */
/**for sleep timer setting*//*

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.pause();
            }
        },10000);

*/

    }
        public void SetTimer(){

        mediaPlayer.pause();

        }


}
