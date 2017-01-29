package com.example.disemk.silentchat;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.disemk.silentchat.fragments.ChatFragment;
import com.example.disemk.silentchat.fragments.RoomsFragment;
import com.example.disemk.silentchat.fragments.SettingsFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String APP_TITLE_NAME = "Silent Chat";
    private FragmentTransaction transaction;
    private RoomsFragment roomsFragment;
    private SettingsFragment settingsFragment;
    private ChatFragment chatFragment;
    private static long back_pressed;
    CircleImageView userIcon;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(APP_TITLE_NAME);

//        Intent intent = getIntent();
//        // status == true, in first launch app on device
//        boolean status = intent.getBooleanExtra("status",false);
//        if (status){
//            android.os.Process.killProcess(android.os.Process.myPid());
//            Log.d("MainActivity","-----------------------------------------------=Restart");
//        }

        initialize();
        transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.ma_container, roomsFragment);
        transaction.commit();
        headNDrawerCustom();

    }

    private void initialize() {
        SingletonCM.getInstance().setMainContext(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        roomsFragment = new RoomsFragment();
        settingsFragment = new SettingsFragment();
        chatFragment = new ChatFragment();


    }

    // create custom elements for header navigation drawer
    private void headNDrawerCustom() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);

        if (!SingletonCM.getInstance().getUserIcon().isEmpty()
                && !SingletonCM.getInstance().getUserName().isEmpty()) {

            String imgUrl = SingletonCM.getInstance().getUserIcon().toString();
            String userNameText = SingletonCM.getInstance().getUserName();

            userIcon = (CircleImageView) hView.findViewById(R.id.nd_cap_user_icon);
            userName = (TextView) hView.findViewById(R.id.nd_cap_user_name);
            userName.setText(userNameText);
            Glide.with(MainActivity.this).
                    load(imgUrl).into(userIcon);
        }
//        userIcon = (CircleImageView) hView.findViewById(R.id.nd_cap_user_icon);
//        userName = (TextView) hView.findViewById(R.id.nd_cap_user_name);
//        userName.setText(SingletonCM.getInstance().getUserName());
//        new MyThread().execute(SingletonCM.getInstance().getUserIcon());

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        transaction = getFragmentManager().beginTransaction();

        if (id == R.id.nav_found) {
            // Handle the camera action
            Toast.makeText(getApplicationContext(), "In Develop", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_all_chats) {
            transaction.replace(R.id.ma_container, roomsFragment);
        } else if (id == R.id.nav_settings) {
            transaction.replace(R.id.ma_container, settingsFragment);
        } else if (id == R.id.nav_exit) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                MainActivity.this.finish();
            }
        } else if (id == R.id.nav_favorite) {
            Toast.makeText(getApplicationContext(), "In Develop", Toast.LENGTH_SHORT).show();
        }
        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class MyThread extends AsyncTask<String, Void, Bitmap> {


        private static final String DEBUG_TAG = "my thread - >";

        @Override
        protected Bitmap doInBackground(String... params) {
            publishProgress(new Void[]{});

            String url = "";
            if (params.length > 0) {
                url = params[0];
            }

            InputStream input = null;

            try {
                URL urlConn = new URL(url);
                input = urlConn.openStream();
            } catch (MalformedURLException e) {
                Log.d(DEBUG_TAG, "Oops, Something wrong with URL...");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(DEBUG_TAG, "Oops, Something wrong with inpur stream...");
                e.printStackTrace();
            }
            return BitmapFactory.decodeStream(input);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            userIcon.setImageBitmap(result);
        }
    }

}
