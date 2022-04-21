package com.example.studytimerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.health.TimerStat;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {


    private TextView Titledisplay;

    private EditText Coursecode;
    private String hh, mm, ss;
    private Chronometer timerText;

    private boolean running;
    private long pauseoffset;
    private long Time, etime;
    String TIME;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Titledisplay = findViewById(R.id.Title);

        Coursecode = findViewById(R.id.taskname);
        timerText = findViewById(R.id.Timer);


        timerText.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                Time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h = (int) (Time / 3600000);
                int m = (int) (Time - h * 3600000) / 60000;
                int s = (int) (Time - h * 3600000 - m * 60000) / 1000;
                hh = h < 10 ? "0" + h : h + "";
                mm = m < 10 ? "0" + m : m + "";
                ss = s < 10 ? "0" + s : s + "";
                chronometer.setText(hh + ":" + mm + ":" + ss);

            }

        });

        checkSharedPreferences();
    }









    public void start(View v) {

        if (!running) {
            //START THE TIME COUNT WHEN CLICK THE START IMAGE BUTTON.
            // setBase use the total time minus the application stop time.
            etime = SystemClock.elapsedRealtime();
            timerText.setBase(SystemClock.elapsedRealtime() - pauseoffset);
            timerText.start();
            running = true;
        }

    }

    public void pause(View v) {

        if (running) {
            timerText.stop();
            //pauseoffset is the time that count when the application stop.
            pauseoffset = SystemClock.elapsedRealtime() - timerText.getBase();
            running = false;

        }
    }



    public void reset(View v) {
        String time = hh + ":" + mm + ":" + ss;
        if(Coursecode.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter the task name", Toast.LENGTH_SHORT).show();
        }
        else {
            Titledisplay.setText("You spent "+ time + " on "  +Coursecode.getText().toString() +" last time.");
            SharedPreferences sharedPreferences =getSharedPreferences("com.example.studytimerapp", MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("coursecode", Titledisplay.getText().toString());

            timerText.setBase(SystemClock.elapsedRealtime());


            pauseoffset = 0;

            editor.apply();
        }

    }

    public void checkSharedPreferences()
    {
        SharedPreferences shared = getSharedPreferences("com.example.studytimerapp", MODE_PRIVATE);
        String timecheck = shared.getString("coursecode","");
        Titledisplay.setText(timecheck);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("timer", Time);
        outState.putBoolean("timerRunning", running);
        outState.putLong("etime", etime);

        outState.putLong("pauseoffset", pauseoffset);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Time = savedInstanceState.getLong("Time");
        running = savedInstanceState.getBoolean("timerRunning");
        etime = savedInstanceState.getLong("etime");
        pauseoffset = savedInstanceState.getLong("pauseoffset");

        if(running){
            timerText.setBase(etime - pauseoffset);
            timerText.start();
            running = true;
        }
    }
}