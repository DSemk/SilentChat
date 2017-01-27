package com.example.disemk.silentchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.disemk.silentchat.models.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private static final String CHILD_TREE = "massages";
    public static final String APP_PREFERENCES = "mysettings_silent";
    public static final String APP_PREFERENCES_BACKGROUND_ID = "backgroundId";

    private SharedPreferences mSharedPreferences;
    private String romName;
    private int msgLayout;

    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<ChatMessage, FirechatMsgViewHolder> mFBAdapter;
    private RecyclerView mMsgRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String mUsername;
    private String mPhotoUrl;

    private Button mSendButtn;
    private EditText mMsgEText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        romName = getIntent().getStringExtra("userRoom");
        setTitle("Комната : " + romName);
        setBackground();
        initialize();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // init all components
    private void initialize() {
        msgLayout = R.layout.chat_message;

        mMsgEText = (EditText) findViewById(R.id.msgEditText);
        mSendButtn = (Button) findViewById(R.id.sendButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        mMsgRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        mMsgRecyclerView.setLayoutManager(mLayoutManager);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        setmFBAdapterUn();
        setBackground();

        mSendButtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMsgEText.getText().toString().isEmpty()) {
                    ChatMessage frendlyMsg = new ChatMessage(
                            mMsgEText.getText().toString(), mUsername, mPhotoUrl, romName);
                    mDatabaseReference.child(romName).push().setValue(frendlyMsg);
                    mMsgEText.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Enter msg first!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mFBAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int frendMsgCount = mFBAdapter.getItemCount();
                int lastPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastPosition == -1 || (positionStart >= (frendMsgCount)) && lastPosition == (positionStart - 1)) {
                    mMsgRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMsgRecyclerView.setLayoutManager(mLayoutManager);
        mMsgRecyclerView.setAdapter(mFBAdapter);


    }

    //TODO : If it's this user msg, use chat_message_my layout, else chat_message

    private void setmFBAdapterUn() {

        mFBAdapter = new FirebaseRecyclerAdapter<ChatMessage, FirechatMsgViewHolder>(
                ChatMessage.class,
                R.layout.chat_message,
                FirechatMsgViewHolder.class,
                mDatabaseReference.child(romName)
        ) {

            @Override
            protected void populateViewHolder(FirechatMsgViewHolder firechatMsgViewHolder, ChatMessage chatMessage, int i) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

//                if(mFirebaseUser.getDisplayName().equals(chatMessage.getName())){
//                    mLayoutManager.setOrientation(Gravity.RIGHT);
//                }
//                else {
//                    mLayoutManager.setOrientation(Gravity.LEFT);
//                }

                firechatMsgViewHolder.msgText.setText(chatMessage.getText());
                firechatMsgViewHolder.userText.setText(chatMessage.getName());


                if (mFirebaseUser.getPhotoUrl() == null) {
                    firechatMsgViewHolder.userImage.setImageDrawable(
                            ContextCompat.getDrawable(ChatActivity.this, R.drawable.ic_account_circle_black_36dp));
                } else {
                    mUsername = mFirebaseUser.getDisplayName();
                    mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                    Glide.with(ChatActivity.this).
                            load(chatMessage.getPhotoUrl()).into(firechatMsgViewHolder.userImage);
                }
            }
        };

        mFBAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatMsgCount = mFBAdapter.getItemCount();
                int lastVisiblePosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (chatMsgCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMsgRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMsgRecyclerView.setLayoutManager(mLayoutManager);
        mMsgRecyclerView.setAdapter(mFBAdapter);

        mUsername = mFirebaseUser.getDisplayName().toString();
    }

    public static class FirechatMsgViewHolder extends RecyclerView.ViewHolder {
        public TextView msgText;
        public TextView userText;
        public CircleImageView userImage;

        public FirechatMsgViewHolder(View view) {
            super(view);
            msgText = (TextView) view.findViewById(R.id.msgTextView);
            userText = (TextView) view.findViewById(R.id.userTextView);
            userImage = (CircleImageView) view.findViewById(R.id.userImageView);
        }
    }

    private void setBackground() {
        RecyclerView view = (RecyclerView) findViewById(R.id.messageRecyclerView);
        int id = SingletonCM.getInstance().getBackgroundID();
        if (id != 0) {
            view.setBackgroundResource(id);
        }

    }


}
