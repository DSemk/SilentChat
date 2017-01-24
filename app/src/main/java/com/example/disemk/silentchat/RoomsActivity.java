package com.example.disemk.silentchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.disemk.silentchat.models.ChatRoom;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RoomsActivity extends AppCompatActivity {
    private static final String CHILD_THREE = "silentchat-5454d";
    public String userRoomName;
    private final static String USER_ROOM_NAME_DEFAULT = "Default room name";

    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<ChatRoom, FireChatRoomViewHolder> mFirebaseRecyclerAdapter;
    private RecyclerView mRoomRecyclerView;
    private LinearLayoutManager mLinerLayoutManager;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        initialize();
    }

    private void initialize() {
        mProgressBar = (ProgressBar) findViewById(R.id.ar_progressBar);
        mRoomRecyclerView = (RecyclerView) findViewById(R.id.roomRecyclerView);
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
            protected void populateViewHolder(FireChatRoomViewHolder viewHolder, ChatRoom model, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//                viewHolder.roomNameText.setText(model.getRoomName());
            }
        };

        mFirebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatMsgCount = mFirebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition = mLinerLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (chatMsgCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mRoomRecyclerView.scrollToPosition(positionStart);
                }
            }


        });

        mRoomRecyclerView.setLayoutManager(mLinerLayoutManager);
        mRoomRecyclerView.setAdapter(mFirebaseRecyclerAdapter);

    }

    private static class FireChatRoomViewHolder extends RecyclerView.ViewHolder {
        public TextView roomNameText;
        public TextView userRoomCountText;

        public FireChatRoomViewHolder(View view) {
            super(view);
            roomNameText = (TextView) view.findViewById(R.id.ar_roomName);
            userRoomCountText = (TextView) view.findViewById(R.id.ar_userCount);
        }
    }
}
