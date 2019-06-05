package com.teapps.hcireactiontest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;
import com.teapps.hcireactiontest.database.DBHelper;
import com.teapps.hcireactiontest.reaction_tests.AcousticReactionTestActivity;
import com.teapps.hcireactiontest.reaction_tests.DecisionReactionTestActivity;
import com.teapps.hcireactiontest.reaction_tests.FilterReactionTestActivity;
import com.teapps.hcireactiontest.reaction_tests.VisualReactionTestActivity;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    private Button btnVisual, btnAcoustic, btnDesicions, btnFilter;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        initObjects();
        setOnClickListeners();

    }

    private void setOnClickListeners() {
        setOnClickListener(VisualReactionTestActivity.class, btnVisual);
        setOnClickListener(AcousticReactionTestActivity.class, btnAcoustic);
        setOnClickListener(DecisionReactionTestActivity.class, btnDesicions);
        setOnClickListener(FilterReactionTestActivity.class, btnFilter);
    }

    private void setOnClickListener(final Class cls, Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, cls));
            }
        });
    }

    private void initObjects() {
        btnVisual = findViewById(R.id.btnVisual);
        btnAcoustic = findViewById(R.id.btnAcustics);
        btnDesicions = findViewById(R.id.btnDesicions);
        btnFilter = findViewById(R.id.btnFilter);
    }

    private void exportDB() {

        DBHelper dbHelper = new DBHelper(this);

        File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "csvfiles");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir
                , sharedPref.getString(getString(R.string.KEY_USERID), "testuser") + ".csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM reaction_test_app_database", null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            Toast.makeText(getApplicationContext(), "CSV-Datei gespeichert", Toast.LENGTH_LONG).show();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_user_info:
                showUserInfoDialog();
                break;
            case R.id.action_save_database_to_csv:
                try {
                    exportDB();
                    DBHelper dbHelper = new DBHelper(this);
                    dbHelper.clearDatabase();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Fehler bei CSV schreiben", Toast.LENGTH_SHORT).show();
                }
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void showUserInfoDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout_main, null);
        builder.setView(dialogView);

        final EditText txtUserID = dialogView.findViewById(R.id.txt_user_id);
        final EditText txtAge = dialogView.findViewById(R.id.txt_age);
        final RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group_gender);

        fillDataIfAvailable(txtUserID, txtAge, radioGroup);

        builder.setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getAndSaveData(txtUserID, txtAge, radioGroup);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, null);
        builder.show();

    }

    private void fillDataIfAvailable(EditText txtUserID, EditText txtAge, RadioGroup radioGroup) {

        txtUserID.setText(sharedPref.getString(getString(R.string.KEY_USERID), ""));
        txtAge.setText(sharedPref.getString(getString(R.string.KEY_AGE), ""));
        radioGroup.check(sharedPref.getInt(getString(R.string.KEY_GENDER), R.id.rb_male));

    }

    private void getAndSaveData(EditText txtUserID, EditText txtAge, RadioGroup radioGroup) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.KEY_USERID), txtUserID.getText().toString());
        editor.putString(getString(R.string.KEY_AGE), txtAge.getText().toString());
        editor.putInt(getString(R.string.KEY_GENDER), radioGroup.getCheckedRadioButtonId());
        editor.commit();

    }
}
