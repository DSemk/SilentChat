package com.example.disemk.silentchat.activitys;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.disemk.silentchat.R;
import com.example.disemk.silentchat.engine.SingletonCM;
import com.example.disemk.silentchat.fragments.ChatFragment;
import com.example.disemk.silentchat.fragments.RoomsFragment;
import com.example.disemk.silentchat.fragments.SettingsFragment;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_main) {
            createAlertDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                            transaction = getFragmentManager().beginTransaction();
                            transaction.remove(roomsFragment);
                            transaction.commit();

                            transaction = getFragmentManager().beginTransaction();
                            transaction.add(R.id.ma_container, roomsFragment);
                            transaction.commit();
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
        alertDialog.setTitle("Поиск комнаты");
        alertDialog.show();
    }



    @Override
    protected void onRestart() {
        super.onRestart();
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
        if (id == R.id.nav_found) {
            createAlertDialog();
        } else if (id == R.id.nav_all_chats) {
            transaction.remove(roomsFragment);
//            transaction.commit();
            transaction.replace(R.id.ma_container, roomsFragment).addToBackStack(null);
        } else if (id == R.id.nav_settings) {
            transaction.replace(R.id.ma_container, settingsFragment).addToBackStack(null);
        } else if (id == R.id.nav_favorite) {
            Toast.makeText(getApplicationContext(), "In Develop", Toast.LENGTH_SHORT).show();
        }
        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
