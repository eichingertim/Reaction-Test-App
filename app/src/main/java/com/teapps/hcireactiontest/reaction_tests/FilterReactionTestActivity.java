package com.teapps.hcireactiontest.reaction_tests;

import android.content.Context;
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
import java.util.Timer;

public class FilterReactionTestActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPref;

    DBHelper dbHelper;

    Button btnStop, btnStart;
    View colorContainer;
    TextView tvTime;

    Handler handler;

    Long TimeInMilliSeconds, startTime;
    int seconds, milliseconds;

    String[] colors = {"#00FF00", "#0000FF", "#563488", "#FF007F", "#00FFFF", "#FFFF00"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_reaction_test);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        initObjects();
        initHandlerAndDatabase();
        initActionBar();

    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initHandlerAndDatabase() {
        handler = new Handler();
        dbHelper = new DBHelper(this);
    }

    private void initObjects() {
        btnStop = findViewById(R.id.btnAction);
        btnStart = findViewById(R.id.btnStart);
        colorContainer = findViewById(R.id.view_color_container);
        tvTime = findViewById(R.id.text_view_time);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                startTest();
                break;
            case R.id.btnAction:
                stopTest();
                break;
        }
    }

    private void stopTest() {
        handler.removeCallbacks(runnable);
        addDataToDatabase(sharedPref.getString(getString(R.string.KEY_USERID), ""), TimeInMilliSeconds.toString());
    }


    private void startTest() {
        tvTime.setText("00:000");
        colorContainer.setBackground(ContextCompat.getDrawable(getApplicationContext()
                , R.drawable.black_border_shape));
        Random random = new Random();
        int timeTillRed = (random.nextInt(9) + 2) * 1000;

        new CountDownTimer(timeTillRed, 1000) {
            public void onTick(long millisUntilFinished) {
                Random random1 = new Random();
                int colorIndex = random1.nextInt(3);
                colorContainer.setBackgroundColor(Color.parseColor(colors[colorIndex]));
            }

            public void onFinish() {
                colorContainer.setBackgroundColor(Color.parseColor("#ff0000"));
                startTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
            }
        }.start();


    }

    public Runnable runnable = new Runnable() {

        public void run() {

            TimeInMilliSeconds = SystemClock.uptimeMillis() - startTime;

            seconds = (int) (TimeInMilliSeconds / 1000);
            seconds = seconds % 60;

            milliseconds = (int) (TimeInMilliSeconds % 1000);

            tvTime.setText(String.format("%02d", seconds) + ":"
                    + String.format("%03d", milliseconds));

            handler.postDelayed(this, 0);

        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addDataToDatabase(String testUserID, String reactionTime) {
        boolean insertData = dbHelper.addData(testUserID, getString(R.string.filter_test_type), reactionTime);
        if (insertData) {
            Toast.makeText(getApplicationContext(), "Data successfully stored", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Data storing failed", Toast.LENGTH_SHORT).show();

        }
    }

}
