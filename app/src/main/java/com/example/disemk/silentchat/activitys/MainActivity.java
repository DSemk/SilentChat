package com.example.disemk.silentchat.activitys;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.disemk.silentchat.R;
import com.example.disemk.silentchat.engine.BaseDataHelper;
import com.example.disemk.silentchat.engine.BaseDataMaster;
import com.example.disemk.silentchat.engine.SingletonCM;
import com.example.disemk.silentchat.fragments.ChatFragment;
import com.example.disemk.silentchat.fragments.RoomsFragment;
import com.example.disemk.silentchat.fragments.SettingsFragment;
import com.google.android.gms.games.multiplayer.realtime.Room;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String FAVORITE_MODE = "favorite_m";
    private static final String STOCK_MODE = "stock_m";
    private static final String MY_ROOM_MODE = "myRoom_m";
    private static final String APP_TITLE_NAME = "Silent Chat";

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
        setTitle("Все чаты");

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

    }


    public void createAlertDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View view = layoutInflater.inflate(R.layout.alert_dialog_found_room, null);
        AlertDialog.Builder builder = new AlertDialog
                .Builder(new ContextThemeWrapper(MainActivity.this, R.style.myDialog));
        builder.setView(view);
        final EditText editName = (EditText) view.findViewById(R.id.ad_found_room_et);
        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!editName.getText().toString().isEmpty()) {
                            SingletonCM.getInstance()
                                    .setUserFilterRoom(editName.getText().toString());
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
                            return;
                        }
                    }
                }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Поиск комнаты");
        alertDialog.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private static MainActivity mainInstanse = new MainActivity();

    public static MainActivity getMainInstanse() {
        return mainInstanse;
    }

    public void setCustomTitle(String str) {
        setTitle(str);
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
        if (id == R.id.nav_found) {
            createAlertDialog();
        } else if (id == R.id.nav_all_chats) {
            setTitle("Все чаты");
            RoomsFragment fragment = new RoomsFragment();
            // setUserFilterRoom need by is - "" from init all rooms.
            SingletonCM.getInstance().setUserFilterRoom("");
            bundle.putString("mode", STOCK_MODE);
            fragment.setArguments(bundle);
            transaction.replace(R.id.ma_container, fragment).addToBackStack(null);
        } else if (id == R.id.nav_settings) {
            setTitle("Настрйоки");
            transaction.replace(R.id.ma_container, settingsFragment).addToBackStack(null);
        } else if (id == R.id.nav_favorite) {
            setTitle("Избранное");
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
            setTitle("Мои чаты");
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
