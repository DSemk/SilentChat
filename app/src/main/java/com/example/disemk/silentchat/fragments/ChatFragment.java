package com.example.disemk.silentchat.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
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
import com.example.disemk.silentchat.SingletonCM;
import com.example.disemk.silentchat.models.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends android.app.Fragment {
    private static final String CHILD_TREE = "massages";
    public static final String APP_PREFERENCES = "mysettings_silent";
    public static final String APP_PREFERENCES_BACKGROUND_ID = "backgroundId";

    private Context context;
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
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        mMsgRecyclerView = (RecyclerView) container.findViewById(R.id.messageRecyclerView);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setStackFromEnd(true);
        mMsgRecyclerView.setLayoutManager(mLayoutManager);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        setmFBAdapterUn();
        setBackground(container);

        mSendButtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMsgEText.getText().toString().isEmpty()) {
                    ChatMessage frendlyMsg = new ChatMessage(
                            mMsgEText.getText().toString(), mUsername, mPhotoUrl, romName);
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

    //TODO : If it's this user msg, use chat_message_my layout, else chat_message

    private void setmFBAdapterUn() {

        mFBAdapter = new FirebaseRecyclerAdapter<ChatMessage, FirechatMsgViewHolder>(
                ChatMessage.class,
                R.layout.message,
                FirechatMsgViewHolder.class,
                mDatabaseReference.child(romName)
        ) {

            @Override
            protected void populateViewHolder(FirechatMsgViewHolder firechatMsgViewHolder, ChatMessage chatMessage, int i) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (chatMessage.getName().equals(mUsername)) {
                    firechatMsgViewHolder.setIsSender(true);
                } else {
                    firechatMsgViewHolder.setIsSender(false);
                }
                firechatMsgViewHolder.msgText.setText(chatMessage.getText());
                firechatMsgViewHolder.userText.setText(chatMessage.getName());


                if (mFirebaseUser.getPhotoUrl() == null) {
                    firechatMsgViewHolder.userImage.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_account_circle_black_36dp));
                } else {
                    mUsername = mFirebaseUser.getDisplayName();
                    mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                    Glide.with(ChatFragment.this).
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
            mGreen300 = ContextCompat.getColor(itemView.getContext(), R.color.material_green_300);
            mGray300 = ContextCompat.getColor(itemView.getContext(), R.color.material_gray_300);
        }

        public void setIsSender(boolean isSender) {
            final int color;
            if (isSender) {
                color = mGreen300;
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
