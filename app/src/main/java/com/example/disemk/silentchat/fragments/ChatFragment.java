package com.example.disemk.silentchat.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.disemk.silentchat.R;
import com.example.disemk.silentchat.activitys.MainActivity;
import com.example.disemk.silentchat.engine.SingletonCM;
import com.example.disemk.silentchat.models.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends android.app.Fragment implements SoundPool.OnLoadCompleteListener {
    private static final String CHILD_TREE = "massages";
    public static final String APP_PREFERENCES = "mysettings_silent";
    public static final String APP_PREFERENCES_BACKGROUND_ID = "backgroundId";
    final int MAX_STREAMS = 5;

    private SoundPool sp;
    private int soundIdShot;
    private int soundIdExplosion;

    private Context context;
    private String romName;
    private int msgLayout;
    private boolean canPlaySound;

    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<ChatMessage, FirechatMsgViewHolder> mFBAdapter;
    private RecyclerView mMsgRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;
    private String mUid;

    private Button mSendButtn;
    private EditText mMsgEText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat, container, false);

        romName = SingletonCM.getInstance().getUserRoom();
        context = SingletonCM.getInstance().getMainContext();
        setBackground(view);
        initialize(view);

        return view;
    }


    // init all components
    private void initialize(View container) {

        msgLayout = R.layout.chat_message;

        mMsgEText = (EditText) container.findViewById(R.id.msgEditText);
        mSendButtn = (Button) container.findViewById(R.id.sendButton);

        mProgressBar = (ProgressBar) container.findViewById(R.id.progressBar);

        mMsgRecyclerView = (RecyclerView) container.findViewById(R.id.messageRecyclerView);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUid = mFirebaseUser.getUid();
        mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();

        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setStackFromEnd(true);
        mMsgRecyclerView.setLayoutManager(mLayoutManager);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        sp = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        sp.setOnLoadCompleteListener(this);

        soundIdShot = sp.load(context, R.raw.new_msg_sound, 1);

        try {
            soundIdExplosion = sp.load(context.getAssets().openFd("explosion.ogg"), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SingletonCM.getInstance();
        setmFBAdapterUn();
        setBackground(container);

        mSendButtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMsgEText.getText().toString().isEmpty()) {
                    ChatMessage frendlyMsg = new ChatMessage(
                            mMsgEText.getText().toString(), mUsername, mPhotoUrl, romName, mUid);
                    mDatabaseReference.child(romName).push().setValue(frendlyMsg);
                    mMsgEText.setText("");
                } else {
                    Toast.makeText(context, "Enter msg first!", Toast.LENGTH_SHORT).show();
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

    private void setmFBAdapterUn() {
        canPlaySound = false;// разрешено ли воспроизводить звук
        mFBAdapter = new FirebaseRecyclerAdapter<ChatMessage, FirechatMsgViewHolder>(
                ChatMessage.class,
                R.layout.message,
                FirechatMsgViewHolder.class,
                mDatabaseReference.child(romName)
        ) {
            @Override
            protected void populateViewHolder(FirechatMsgViewHolder firechatMsgViewHolder, ChatMessage chatMessage, int i) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (chatMessage.getUid().equals(mUid)) {
                    canPlaySound = false;
                    firechatMsgViewHolder.setIsSender(true);
                } else {
                    canPlaySound = true;
                    firechatMsgViewHolder.setIsSender(false);
                }
                firechatMsgViewHolder.msgText.setText(chatMessage.getText());
                firechatMsgViewHolder.userText.setText(chatMessage.getName());

                mUsername = mFirebaseUser.getDisplayName();
                if (mPhotoUrl.equals("")) {
                    mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                }
                Glide.with(ChatFragment.this).
                        load(chatMessage.getPhotoUrl()).into(firechatMsgViewHolder.userImage);

            }

        };

        mFBAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            int mCurrentItemsCount = 0;
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatMsgCount = mFBAdapter.getItemCount();
                int lastVisiblePosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (chatMsgCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMsgRecyclerView.scrollToPosition(positionStart);
                }
                if (mCurrentItemsCount < chatMsgCount && canPlaySound) {
                    //проигрываем звук
                    sp.play(soundIdShot, 1, 1, 0, 0, 1);
                    sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
                }
                mCurrentItemsCount = chatMsgCount;
            }

            @Override
            public void onChanged() {
                super.onChanged();

                if (mCurrentItemsCount < mFBAdapter.getItemCount()) {
                    //проигрываем звук
                    sp.play(soundIdShot, 1, 1, 0, 0, 1);
//                    sp.play(soundIdExplosion, 1, 1, 0, 0, 1);
                }
                mCurrentItemsCount = mFBAdapter.getItemCount();
            }
        });

        mMsgRecyclerView.setLayoutManager(mLayoutManager);
        mMsgRecyclerView.setAdapter(mFBAdapter);

        mUsername = mFirebaseUser.getDisplayName().toString();
        canPlaySound = true;
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

    }

    public static class FirechatMsgViewHolder extends RecyclerView.ViewHolder {
        public TextView msgText;
        public TextView userText;
        public CircleImageView userImage;
        private final FrameLayout mLeftArrow;
        private final FrameLayout mRightArrow;
        private final RelativeLayout mMessageContainer;
        private final LinearLayout mMessage;
        private final int mGreen300;
        private final int mGray300;

        public FirechatMsgViewHolder(View view) {
            super(view);
            userImage = (CircleImageView) view.findViewById(R.id.user_msg_icon);
            userText = (TextView) itemView.findViewById(R.id.name_text);
            msgText = (TextView) itemView.findViewById(R.id.message_text);
            mLeftArrow = (FrameLayout) itemView.findViewById(R.id.left_arrow);
            mRightArrow = (FrameLayout) itemView.findViewById(R.id.right_arrow);
            mMessageContainer = (RelativeLayout) itemView.findViewById(R.id.message_container);
            mMessage = (LinearLayout) itemView.findViewById(R.id.message);
            mGreen300 = ContextCompat.getColor(itemView.getContext(), R.color.material_blue_300);
            mGray300 = ContextCompat.getColor(itemView.getContext(), R.color.material_gray_300);
        }

        public void setIsSender(boolean isSender) {
            final int color;
            if (isSender) {
                color = mGreen300;
                msgText.setTextColor(Color.WHITE);
                mLeftArrow.setVisibility(View.GONE);
                mRightArrow.setVisibility(View.VISIBLE);
                mMessageContainer.setGravity(Gravity.END);
            } else {
                color = mGray300;
                mLeftArrow.setVisibility(View.VISIBLE);
                mRightArrow.setVisibility(View.GONE);
                mMessageContainer.setGravity(Gravity.START);
            }

            ((GradientDrawable) mMessage.getBackground()).setColor(color);
            ((RotateDrawable) mLeftArrow.getBackground()).getDrawable()
                    .setColorFilter(color, PorterDuff.Mode.SRC);
            ((RotateDrawable) mRightArrow.getBackground()).getDrawable()
                    .setColorFilter(color, PorterDuff.Mode.SRC);
        }

    }


    private void setBackground(View container) {
        RecyclerView view = (RecyclerView) container.findViewById(R.id.messageRecyclerView);
        int id = SingletonCM.getInstance().getBackgroundID();
        if (id != 0) {
            view.setBackgroundResource(id);
        }

    }
}
