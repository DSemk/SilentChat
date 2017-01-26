package com.example.disemk.silentchat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

/**
 * Created by icoper on 26.01.17.
 */

public class SettingsActivity extends AppCompatActivity {
    private ImageView backgroundOne;
    private ImageView backgroundSecond;
    private ImageView backgroundThird;
    private ImageView backgroundFour;

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


        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.image_back1:
                        SingletonCM.getInstance().setBackgroundID(R.drawable.back_1_ltl);
                        break;
                    case R.id.image_back2:
                        SingletonCM.getInstance().setBackgroundID(R.drawable.back_2_ltl);
                        break;
                    case R.id.image_back3:
                        SingletonCM.getInstance().setBackgroundID(R.drawable.back_3_ltl);
                        break;
                    case R.id.image_back4:
                        SingletonCM.getInstance().setBackgroundID(R.drawable.back_4_ltl);
                        break;
                }
                Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
            }
        };

        backgroundOne.setOnClickListener(mOnClickListener);
        backgroundSecond.setOnClickListener(mOnClickListener);
        backgroundThird.setOnClickListener(mOnClickListener);
        backgroundFour.setOnClickListener(mOnClickListener);


    }

}
