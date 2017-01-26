package com.example.disemk.silentchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.disemk.silentchat.models.ChatRoom;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RoomsActivity extends AppCompatActivity {
    private static final String TITEL_NAME = "Комнаты";
    private static final String CHILD_THREE = "all rooms";
    public String userRoomName;

    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<ChatRoom, FireChatRoomViewHolder> mFirebaseRecyclerAdapter;
    private RecyclerView mRoomRecyclerView;
    private LinearLayoutManager mLinerLayoutManager;
    private ProgressBar mProgressBar;
    private FloatingActionButton mAddRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);
        setTitle(TITEL_NAME);
        setBackground();
        initialize();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_settings) {
            Intent intent = new Intent(RoomsActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        mProgressBar = (ProgressBar) findViewById(R.id.ar_progressBar);
        mRoomRecyclerView = (RecyclerView) findViewById(R.id.roomRecyclerView);
        mAddRoom = (FloatingActionButton) findViewById(R.id.ar_addRoom);

        mLinerLayoutManager = new LinearLayoutManager(this);
        mLinerLayoutManager.setStackFromEnd(true);
        mRoomRecyclerView.setLayoutManager(mLinerLayoutManager);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatRoom, FireChatRoomViewHolder>(
                ChatRoom.class,
                R.layout.room_list,
                FireChatRoomViewHolder.class,
                mDatabaseReference.child(CHILD_THREE)
        ) {
            @Override
            protected void populateViewHolder(final FireChatRoomViewHolder viewHolder, ChatRoom model, final int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.roomNameText.setText(model.getRoomName());
                viewHolder.roomNameText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickCustom(v, position, viewHolder);
                    }
                });
            }
        };

        mFirebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int roomNameCount = mFirebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition = mLinerLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (roomNameCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mRoomRecyclerView.scrollToPosition(positionStart);
                }
            }

        });


        mRoomRecyclerView.setLayoutManager(mLinerLayoutManager);
        mRoomRecyclerView.setAdapter(mFirebaseRecyclerAdapter);

        mAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewRoom();
            }
        });
    }

    private void onItemClickCustom(View v, int position, FireChatRoomViewHolder viewHolder) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userRoom", viewHolder.roomNameText.getText().toString());
        startActivity(intent);
    }

    private void addNewRoom() {
        LayoutInflater layoutInflater = LayoutInflater.from(RoomsActivity.this);
        View view = layoutInflater.inflate(R.layout.room_add_new, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(RoomsActivity.this);
        builder.setView(view);
        final EditText editName = (EditText) view.findViewById(R.id.ad_addNewRoom_et);

        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editName.getText().toString().isEmpty()) {
//                            Toast.makeText(getApplicationContext(),"Введите название комнаты",Toast.LENGTH_SHORT).show();
                        } else {
                            userRoomName = editName.getText().toString();
                            ChatRoom newRoom = new ChatRoom(
                                    userRoomName);
                            mDatabaseReference.child(CHILD_THREE).push().setValue(newRoom);
                            Intent intent = new Intent(RoomsActivity.this, ChatActivity.class);
                            intent.putExtra("userRoom", userRoomName);
                            startActivity(intent);
                        }
                    }
                }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Введите название комнаты");
        alertDialog.show();


    }

    public static class FireChatRoomViewHolder extends RecyclerView.ViewHolder {
        public TextView roomNameText;
//        public TextView userRoomCountText;

        public FireChatRoomViewHolder(View view) {
            super(view);
            roomNameText = (TextView) view.findViewById(R.id.ar_roomName);
//            userRoomCountText = (TextView) view.findViewById(R.id.ar_userCount);
        }
    }

    private void setBackground() {
        RecyclerView view = (RecyclerView) findViewById(R.id.roomRecyclerView);
        int id = SingletonCM.getInstance().getBackgroundID();
        if (id != 0) {
            view.setBackgroundResource(id);
        }

    }

}
