package com.example.disemk.silentchat.activitys;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.disemk.silentchat.R;
import com.example.disemk.silentchat.engine.SingletonCM;
import com.example.disemk.silentchat.fragments.RoomsFragment;
import com.example.disemk.silentchat.fragments.SettingsFragment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String FAVORITE_MODE = "favorite_m";
    private static final String STOCK_MODE = "stock_m";
    private static final String MY_ROOM_MODE = "myRoom_m";
    private static final String ALL_TITLE_NAME = "Все чаты";
    private static final String ALERT_TITLE_NAME = "Поиск комнаты";


    private FragmentTransaction transaction;
    private RoomsFragment roomsFragment;
    private SettingsFragment settingsFragment;
    private static long back_pressed;

    CircleImageView userIcon;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(ALL_TITLE_NAME);

        // setup different items from start
        initialize();

        // show RoomsFragment as default
        transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.ma_container, roomsFragment);
        transaction.commit();

        headNDrawerCustom();

    }

    private void initialize() {
        // push MainActivity.getApplicationContext() to Singleton.
        // I'm use him on all Fragments
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
    }

    // create custom elements for header navigation drawer
    private void headNDrawerCustom() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);

        try {
            // get user data from Singleton and use it's in as header
            if (!SingletonCM.getInstance().getUserIcon().isEmpty()
                    && !SingletonCM.getInstance().getUserName().isEmpty()) {

                String imgUrl = SingletonCM.getInstance().getUserIcon();
                String userNameText = SingletonCM.getInstance().getUserName();

                userIcon = (CircleImageView) hView.findViewById(R.id.nd_cap_user_icon);
                userName = (TextView) hView.findViewById(R.id.nd_cap_user_name);
                userName.setText(userNameText);
                Glide.with(MainActivity.this).
                        load(imgUrl).into(userIcon);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }


    // This metod start when user press FAB - add new room
    public void createAlertDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View view = layoutInflater.inflate(R.layout.alert_dialog_found_room, null);
        AlertDialog.Builder builder = new AlertDialog
                .Builder(new ContextThemeWrapper(MainActivity.this, R.style.myDialog));

        builder.setView(view);

        // This edit text user use to input name new room.
        // Then we create new room onFireBase Database, and show it in ChatFragment whis new room
        final EditText editName = (EditText) view.findViewById(R.id.ad_found_room_et);

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!editName.getText().toString().isEmpty()) {

                            SingletonCM.getInstance()
                                    .setUserFilterRoom(editName.getText().toString());
                            /**
                             * @param STOCK_MODE - launch FireBaseAdapter in default mode
                             *
                             */
                            SingletonCM.getInstance().setfBAdapterMode(STOCK_MODE);

                            RoomsFragment.getRoomsInstanse().setUserFilterText(editName.getText().toString());
                            setTitle("Поиск : " + editName.getText().toString());
                            getFragmentManager()
                                    .beginTransaction()
                                    .detach(roomsFragment)
                                    .attach(roomsFragment)
                                    .commit();
                        } else {
                            Toast.makeText(
                                    MainActivity.this, "Введите имя комнаты",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(ALERT_TITLE_NAME);
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        }
        back_pressed = System.currentTimeMillis();
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();

        /**
         * @param STOCK_MODE - show all FireBase rooms
         * @param FAVORITE_MODE - show all user favorite rooms
         * @param MY_ROOM_MODE - show user created rooms
         *
         * @func bundle.putString("mode", NAME_MODE); - set mode from FireBaseAdapter
         *
         *
         */

        if (id == R.id.nav_found) {
            createAlertDialog();
        } else if (id == R.id.nav_all_chats) {

            setTitle(R.string.all_room_mode_ru);
            RoomsFragment fragment = new RoomsFragment();
            // setUserFilterRoom need by is - "" from init all rooms.
            SingletonCM.getInstance().setUserFilterRoom("");
            bundle.putString("mode", STOCK_MODE);
            fragment.setArguments(bundle);
            transaction.replace(R.id.ma_container, fragment).addToBackStack(null);

        } else if (id == R.id.nav_settings) {

            setTitle(R.string.settings_mode_ru);
            transaction.replace(R.id.ma_container, settingsFragment).addToBackStack(null);

        } else if (id == R.id.nav_favorite) {

            setTitle(R.string.favorite_room_mode_ru);
            ArrayList<String> favoriteRooms = new ArrayList<>();
            try {
                favoriteRooms = SingletonCM.getInstance().getFavoriteRoomList();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (favoriteRooms.size() == 0 || favoriteRooms.isEmpty()) {
                Toast.makeText(this, "Пока здесь пусто", Toast.LENGTH_SHORT).show();
            } else {
                RoomsFragment fragment = new RoomsFragment();
                bundle.putString("mode", FAVORITE_MODE);
                fragment.setArguments(bundle);
                transaction.replace(R.id.ma_container, fragment).addToBackStack(null);
            }

        } else if (id == R.id.nav_myRoom) {

            setTitle(R.string.my_room_mode_ru);
            RoomsFragment fragment = new RoomsFragment();

            bundle.putString("mode", MY_ROOM_MODE);
            fragment.setArguments(bundle);
            transaction.replace(R.id.ma_container, fragment).addToBackStack(null);
        }

        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
