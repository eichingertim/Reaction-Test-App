package com.teapps.hcireactiontest.reaction_tests;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.teapps.hcireactiontest.R;
import com.teapps.hcireactiontest.database.DBHelper;

import java.util.Random;

public class AcousticReactionTestActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPref;

    Button btnStop, btnStart;
    TextView tvTime;

    Handler handler;

    Long timeInMilliSeconds, startTime;
    int seconds, milliseconds;

    MediaPlayer mediaPlayer;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acustic_reaction_test);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        initObjects();
        initHandler();
        initActionBar();

    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initHandler() {
        handler = new Handler();
        dbHelper = new DBHelper(this);
    }

    private void initObjects() {
        btnStop = findViewById(R.id.btnAction);
        btnStart = findViewById(R.id.btnStart);
        tvTime = findViewById(R.id.text_view_time);
        tvTime.setVisibility(View.INVISIBLE);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnStop.setEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test_activities, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.nav_help:
                showHelpDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showHelpDialog() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                startTest();
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                break;
            case R.id.btnAction:
                stopTest();
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                break;
        }
    }

    private void stopTest() {
        handler.removeCallbacks(runnable);
        mediaPlayer.stop();
        addDataToDatabase(sharedPref.getString(getString(R.string.KEY_USERID), "")
                , timeInMilliSeconds.toString(), sharedPref.getString(getString(R.string.KEY_GENDER_STRING), "")
                , sharedPref.getString(getString(R.string.KEY_AGE), "0"));
        tvTime.setVisibility(View.VISIBLE);
    }


    private void startTest() {
        tvTime.setVisibility(View.INVISIBLE);
        tvTime.setText(R.string.default_time_value);
        Random random = new Random();
        int timeTillRed = (random.nextInt(9) + 4) * 1000;

        new CountDownTimer(timeTillRed, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                try {
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);
                    mediaPlayer.start();
                    startTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 200);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();


    }

    public Runnable runnable = new Runnable() {

        public void run() {

            timeInMilliSeconds = SystemClock.uptimeMillis() - startTime;

            seconds = (int) (timeInMilliSeconds / 1000);
            seconds = seconds % 60;

            milliseconds = (int) (timeInMilliSeconds % 1000);

            tvTime.setText(String.format("%02d", seconds) + ":"
                    + String.format("%03d", milliseconds));

            handler.postDelayed(this, 0);

        }

    };

    private void addDataToDatabase(String testUserID, String reactionTime, String gender, String age) {
        boolean insertData = dbHelper.addData(testUserID, getString(R.string.acoustic_test_type), reactionTime, gender, age);
        if (insertData) {
            Toast.makeText(getApplicationContext(), "Data successfully stored", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Data storing failed", Toast.LENGTH_SHORT).show();

        }
    }

}
