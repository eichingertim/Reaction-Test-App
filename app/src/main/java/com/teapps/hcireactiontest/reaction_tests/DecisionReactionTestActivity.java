package com.teapps.hcireactiontest.reaction_tests;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
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

public class DecisionReactionTestActivity extends AppCompatActivity
        implements View.OnClickListener {

    private SharedPreferences sharedPref;

    private Button btnStopRed, btnStopBlue, btnStart;
    private TextView tvTime;
    private View colorContainer;

    private DBHelper dbHelper;

    private Handler handler;

    private Long timeInMilliSeconds, startTime;

    private int randomIntForColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision_reaction_test);

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
        btnStopRed = findViewById(R.id.btnActionRed);
        btnStopBlue = findViewById(R.id.btnActionBlue);
        btnStart = findViewById(R.id.btnStart);
        tvTime = findViewById(R.id.text_view_time);
        tvTime.setVisibility(View.INVISIBLE);
        colorContainer = findViewById(R.id.view_color_container);

        btnStart.setOnClickListener(this);
        btnStopRed.setOnClickListener(this);
        btnStopRed.setEnabled(false);
        btnStopBlue.setOnClickListener(this);
        btnStopBlue.setEnabled(false);

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
                break;
            case R.id.btnActionRed:
                if (randomIntForColor == 0) {
                    stopTest();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.false_button)
                            , Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnActionBlue:
                if (randomIntForColor == 1) {
                    stopTest();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.false_button)
                            , Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void stopTest() {
        handler.removeCallbacks(stopWatchRunnable);
        addDataToDatabase(sharedPref.getString(getString(R.string.KEY_USERID), "")
                , timeInMilliSeconds.toString(), sharedPref.getString(getString(R.string.KEY_GENDER_STRING), "")
                , sharedPref.getString(getString(R.string.KEY_AGE), "0"));
        tvTime.setVisibility(View.VISIBLE);
        btnStart.setEnabled(true);
        btnStopBlue.setEnabled(false);
        btnStopRed.setEnabled(false);
    }


    private void startTest() {
        tvTime.setVisibility(View.INVISIBLE);
        tvTime.setText(R.string.default_time_value);
        colorContainer.setBackground(ContextCompat.getDrawable(getApplicationContext()
                , R.drawable.black_border_shape));
        Random random = new Random();
        int timeTillRed = (random.nextInt(9) + 4) * 1000;

        new CountDownTimer(timeTillRed, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                btnStopBlue.setEnabled(true);
                btnStopRed.setEnabled(true);
                displayTheAction();
            }
        }.start();
        btnStart.setEnabled(false);


    }

    private void displayTheAction() {
        try {
            Random randomForColor = new Random();
            randomIntForColor = randomForColor.nextInt(2);

            if (randomIntForColor == 0) {
                colorContainer.setBackgroundColor(Color.RED);
            } else {
                colorContainer.setBackgroundColor(Color.BLUE);
            }
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(stopWatchRunnable, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Runnable stopWatchRunnable = new Runnable() {

        public void run() {

            timeInMilliSeconds = SystemClock.uptimeMillis() - startTime;

            @SuppressLint("DefaultLocale") String seconds = String.format("%02d"
                    , ((int) (timeInMilliSeconds / 1000))%60);
            @SuppressLint("DefaultLocale") String milliseconds = String.format("%03d"
                    ,(int) (timeInMilliSeconds % 1000));

            String time = seconds + ":" + milliseconds;
            tvTime.setText(time);

            handler.postDelayed(this, 0);

        }

    };

    private void addDataToDatabase(String testUserID, String reactionTime, String gender
            , String age) {
        boolean insertData = dbHelper.addDataToDB(testUserID, getString(R.string.decision_test_type), reactionTime, gender, age);
        if (insertData) {
            Toast.makeText(getApplicationContext(), getString(R.string.database_storing_successfull)
                    , Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.database_storing_failure)
                    , Toast.LENGTH_SHORT).show();

        }
    }
}
