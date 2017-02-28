package com.example.disemk.silentchat.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.disemk.silentchat.R;
import com.example.disemk.silentchat.engine.SingletonCM;

/**
 * Created by icoper on 26.01.17.
 */

public class SettingsFragment extends Fragment {

    private static final String APP_PREFERENCES = "silent_pref";
    private static final String APP_PREFERENCES_BACKGROUND_ID = "backgroundId";
    private SharedPreferences mSharedPreferences;
    private Context context;

    private ImageView backgroundOne;
    private ImageView backgroundSecond;
    private ImageView backgroundThird;
    private ImageView backgroundFour;
    private ImageView backgroundFive;
    private ImageView backgroundSix;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = SingletonCM.getInstance().getMainContext();
        initializeItem(view);
        return view;
    }



    private void initializeItem(View container) {
        backgroundOne = (ImageView) container.findViewById(R.id.image_back1);
        backgroundSecond = (ImageView)container. findViewById(R.id.image_back2);
        backgroundThird = (ImageView) container.findViewById(R.id.image_back3);
        backgroundFour = (ImageView) container.findViewById(R.id.image_back4);
        backgroundFive = (ImageView) container.findViewById(R.id.image_back5);
        backgroundSix = (ImageView)container. findViewById(R.id.image_back6);


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
        mSharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(APP_PREFERENCES_BACKGROUND_ID, backId);
        editor.apply();
    }

    private void restartAppDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.alert_dialog_savestate, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Предупреждение!");
        alertDialog.show();

    }

}
