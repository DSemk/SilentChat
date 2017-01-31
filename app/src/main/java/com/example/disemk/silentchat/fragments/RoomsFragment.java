package com.example.disemk.silentchat.fragments;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.disemk.silentchat.R;
import com.example.disemk.silentchat.engine.SingletonCM;
import com.example.disemk.silentchat.models.ChatRoom;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RoomsFragment extends android.app.Fragment {
    private Context context;
    private static final String CHILD_THREE = "all rooms";
    private static final String APP_PREFERENCES = "silent_pref";
    private static final String APP_PREFERENCES_BACKGROUND_ID = "backgroundId";
    private String userFilterText;
    private ChatFragment chatFragment;
    private SharedPreferences mSharedPreferences;
    private FragmentTransaction fragmentTransaction;
    public String userRoomName;
    private static RoomsFragment roomsInstanse = new RoomsFragment();

    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<ChatRoom, FireChatRoomViewHolder> mFirebaseRecyclerAdapter;
    private RecyclerView mRoomRecyclerView;
    private LinearLayoutManager mLinerLayoutManager;
    private ProgressBar mProgressBar;
    private FloatingActionButton mAddRoom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_rooms,container,false);

        context = SingletonCM.getInstance().getMainContext();
        initialize(view);
        setBackground(view);
        return view;
    }

    private void initialize(View container) {
        // it's use when user input name for found room
        try {
            userFilterText = "";
            if (!SingletonCM.getInstance().getUserFilterRoom().isEmpty()) {
                userFilterText = SingletonCM.getInstance().getUserFilterRoom();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        chatFragment = new ChatFragment();

        mProgressBar = (ProgressBar) container.findViewById(R.id.ar_progressBar);
        mRoomRecyclerView = (RecyclerView) container.findViewById(R.id.roomRecyclerView);
        mAddRoom = (FloatingActionButton) container.findViewById(R.id.ar_addRoom);

        mLinerLayoutManager = new LinearLayoutManager(context.getApplicationContext());
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
                if (userFilterText.isEmpty()) {
                    viewHolder.roomNameText.setText(model.getRoomName());
                    viewHolder.roomNameText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClickCustom(viewHolder);
                        }
                    });
                } else if (model.getRoomName().contains(userFilterText)) {
                    viewHolder.roomNameText.setText(model.getRoomName());
                    viewHolder.roomNameText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClickCustom(viewHolder);
                        }
                    });
                } else {
                    viewHolder.roomNameText.setVisibility(View.GONE);
                }
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
                addNewRoom(context);
            }
        });
    }

    private void onItemClickCustom(FireChatRoomViewHolder viewHolder) {
        SingletonCM.getInstance().setUserRoom(viewHolder.roomNameText.getText().toString());
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.ma_container, chatFragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void addNewRoom(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.room_add_new, null);
        AlertDialog.Builder builder = new AlertDialog
                .Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setView(view);
        final EditText editName = (EditText) view.findViewById(R.id.ad_addNewRoom_et);
        builder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!editName.getText().toString().isEmpty()) {

                            userRoomName = editName.getText().toString();
                            ChatRoom newRoom = new ChatRoom(userRoomName);
                            mDatabaseReference.child(CHILD_THREE).push().setValue(newRoom);
                            SingletonCM.getInstance().setUserRoom(userRoomName);
                            fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.ma_container, chatFragment).addToBackStack(null);
                            fragmentTransaction.commit();
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

    private void setFilter(String name) {

    }

    private void setBackground(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.roomRecyclerView);
        int id = SingletonCM.getInstance().getBackgroundID();
        if (id != 0) {
            recyclerView.setBackgroundResource(id);
        }
    }

    public static RoomsFragment getRoomsInstanse() {
        return roomsInstanse;
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


}
