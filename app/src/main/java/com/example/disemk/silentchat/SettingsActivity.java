package com.example.disemk.silentchat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.disemk.silentchat.models.ChatRoom;

/**
 * Created by icoper on 26.01.17.
 */

public class SettingsActivity extends AppCompatActivity {
    public static final String APP_PREFERENCES = "mysettings_silent";
    public static final String APP_PREFERENCES_BACKGROUND_ID = "backgroundId";

    private SharedPreferences mSharedPreferences;

    private ImageView backgroundOne;
    private ImageView backgroundSecond;
    private ImageView backgroundThird;
    private ImageView backgroundFour;
    private ImageView backgroundFive;
    private ImageView backgroundSix;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.main_title_ru);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        initializeItem();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeItem() {
        backgroundOne = (ImageView) findViewById(R.id.image_back1);
        backgroundSecond = (ImageView) findViewById(R.id.image_back2);
        backgroundThird = (ImageView) findViewById(R.id.image_back3);
        backgroundFour = (ImageView) findViewById(R.id.image_back4);
        backgroundFive = (ImageView) findViewById(R.id.image_back5);
        backgroundSix = (ImageView) findViewById(R.id.image_back6);


        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.image_back1:
                        SingletonCM.getInstance().setBackgroundID(R.drawable.back_1);
                        saveStateBackground(R.drawable.back_1);
                        break;
                    case R.id.image_back2:
                        SingletonCM.getInstance().setBackgroundID(R.drawable.back_2);
                        saveStateBackground(R.drawable.back_2);
                        break;
                    case R.id.image_back3:
                        SingletonCM.getInstance().setBackgroundID(R.drawable.back_3);
                        saveStateBackground(R.drawable.back_3);
                        break;
                    case R.id.image_back4:
                        SingletonCM.getInstance().setBackgroundID(R.drawable.back_4);
                        saveStateBackground(R.drawable.back_4);
                        break;
                    case R.id.image_back5:
                        SingletonCM.getInstance().setBackgroundID(R.drawable.back_5);
                        saveStateBackground(R.drawable.back_5);
                        break;
                    case R.id.image_back6:
                        SingletonCM.getInstance().setBackgroundID(R.drawable.back_6);
                        saveStateBackground(R.drawable.back_6);
                        break;

                }
                restartAppDialog();
            }
        };

        backgroundOne.setOnClickListener(mOnClickListener);
        backgroundSecond.setOnClickListener(mOnClickListener);
        backgroundThird.setOnClickListener(mOnClickListener);
        backgroundFour.setOnClickListener(mOnClickListener);
        backgroundFive.setOnClickListener(mOnClickListener);
        backgroundSix.setOnClickListener(mOnClickListener);

    }

    private void saveStateBackground(int backId) {
        mSharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(APP_PREFERENCES_BACKGROUND_ID, backId);
        editor.apply();
    }

    private void restartAppDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(SettingsActivity.this);
        View view = layoutInflater.inflate(R.layout.alert_dialog_savestate, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setView(view);

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplication(), "Применено", Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Предупреждение!");
        alertDialog.show();

    }

}
