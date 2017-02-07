package com.example.disemk.silentchat.fragments;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.disemk.silentchat.R;
import com.example.disemk.silentchat.activitys.MainActivity;
import com.example.disemk.silentchat.engine.BaseDataMaster;
import com.example.disemk.silentchat.engine.SingletonCM;
import com.example.disemk.silentchat.models.ChatRoom;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomsFragment extends android.app.Fragment {
    private Context context;
    private static final String CHILD_THREE = "all rooms";
    private String userFilterText;
    private ChatFragment chatFragment;
    private FragmentTransaction fragmentTransaction;
    private BaseDataMaster dataMaster;
    public String userRoomName;
    public String roomKey;
    private static RoomsFragment roomsInstanse = new RoomsFragment();
    private ArrayList<String> favoriteRooms;

    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<ChatRoom, FireChatRoomViewHolder> mFirebaseRecyclerAdapter;
    private RecyclerView mRoomRecyclerView;
    private LinearLayoutManager mLinerLayoutManager;
    private ProgressBar mProgressBar;
    private FloatingActionButton mAddRoom;
    private View rfView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_rooms, container, false);
        rfView = view;
        context = SingletonCM.getInstance().getMainContext();
        initialize(view);
        setBackground(view);
        return view;
    }

    /**
     * @param container
     */
    private void initialize(final View container) {
        dataMaster = BaseDataMaster.getDataMaster(context);
        synchronizeRoomListAndDB();
        // it's use when user input name for found room
        try {
            userFilterText = "";
            if (!SingletonCM.getInstance().getUserFilterRoom().isEmpty()) {
                userFilterText = SingletonCM.getInstance().getUserFilterRoom();
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        favoriteRooms = SingletonCM.getInstance().getFavoriteRoomList();
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
            protected void populateViewHolder(final FireChatRoomViewHolder viewHolder, final ChatRoom model, final int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.favoriteStar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToFavorite(model.getRoomKey(), viewHolder);
                    }
                });
                viewHolder.roomNameText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickCustom(viewHolder);
                    }
                });
                if (userFilterText.isEmpty()) {
                    viewHolder.roomNameText.setText(model.getRoomName());
                    roomFavoriteOrNo(model.getRoomKey(), viewHolder);
                    viewHolder.isFilterName(true);
                } else if (model.getRoomName().contains(userFilterText)) {
                    viewHolder.roomNameText.setText(model.getRoomName());
                    viewHolder.isFilterName(true);
                    roomFavoriteOrNo(model.getRoomKey(), viewHolder);
                } else {
                    viewHolder.isFilterName(false);
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

    /**
     * @param key        - fireBase room ID
     * @param viewHolder - FireBase viewHolder
     */
    private void addToFavorite(String key, FireChatRoomViewHolder viewHolder) {
        String foundKey = "";
        for (String temp : favoriteRooms) {
            if (temp.equals(key)) {
                foundKey = temp;
            }
        }
        if (!foundKey.equals("")) {
            dataMaster.deleteItem(key);
            Toast.makeText(context, "Удалено", Toast.LENGTH_SHORT).show();
            viewHolder.isFavoriteRomm(false);
        } else {
            dataMaster.insertKey(key);
            Toast.makeText(context, "Добавлено", Toast.LENGTH_SHORT).show();
            viewHolder.isFavoriteRomm(true);
        }
        synchronizeRoomListAndDB();
    }

    private void synchronizeRoomListAndDB() {
        favoriteRooms = dataMaster.getKeys();
        SingletonCM.getInstance().setFavoriteRoomList(favoriteRooms);
    }

    private void roomFavoriteOrNo(String fbKey, FireChatRoomViewHolder viewHolder) {
        boolean state = false;
        if (favoriteRooms != null && favoriteRooms.size() != 0) {
            for (String s : favoriteRooms) {
                if (s.equals(fbKey)) {
                    state = true;
                }
            }
        }
        viewHolder.isFavoriteRomm(state);
    }

    /**
     * its show only favorite user rooms in FireBaseAdapter
     */
    public void showFavorite(final Context mContext) {
        favoriteRooms = SingletonCM.getInstance().getFavoriteRoomList();
        dataMaster = BaseDataMaster.getDataMaster(mContext);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (favoriteRooms.size() == 0) {
            Toast.makeText(mContext, "Пока здесь пусто", Toast.LENGTH_SHORT).show();
        } else {
            mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatRoom, FireChatRoomViewHolder>(
                    ChatRoom.class,
                    R.layout.room_list,
                    FireChatRoomViewHolder.class,
                    mDatabaseReference.child(CHILD_THREE)
            ) {
                @Override
                protected void populateViewHolder(final FireChatRoomViewHolder viewHolder, final ChatRoom model, final int position) {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    String listRoomKey = model.getRoomKey();
                    Iterator iterator = favoriteRooms.iterator();
                    while (iterator.hasNext()) {
                        Object e = iterator.next();
                        if (e.equals(listRoomKey)) {
                            viewHolder.roomNameText.setText(model.getRoomName());
                        }
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
                    addNewRoom(mContext);
                }
            });
        }

    }

    /**
     * @param viewHolder - you know ;)
     */
    private void onItemClickCustom(FireChatRoomViewHolder viewHolder) {
        SingletonCM.getInstance().setUserRoom(viewHolder.roomNameText.getText().toString());

        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.ma_container, chatFragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * this metod is used when user touch on "add float btn",
     * and then we are add new chat room
     *
     * @param context - it's our MainActivity.getAppContext()
     */
    public void addNewRoom(final Context context) {
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

                            roomKey = mDatabaseReference.child(CHILD_THREE).push().getKey();
                            ChatRoom newRoom = new ChatRoom(userRoomName, roomKey);
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
        public CircleImageView circleImageView;
        private final LinearLayout mRoom;
        private ImageView favoriteStar;
        private TextView favoriteText;

        public FireChatRoomViewHolder(View view) {
            super(view);
            circleImageView = (CircleImageView) view.findViewById(R.id.ar_roomIcon);
            roomNameText = (TextView) view.findViewById(R.id.ar_roomName);
            mRoom = (LinearLayout) itemView.findViewById(R.id.ar_room_layout_container);
            favoriteStar = (ImageView) view.findViewById(R.id.ar_room_star_iv);
            favoriteText = (TextView) view.findViewById(R.id.ar_room_star_text);
        }

        public void isFilterName(boolean state) {
            if (state) {
                mRoom.setVisibility(View.VISIBLE);
            } else {
                mRoom.setVisibility(View.GONE);
            }
        }

        public void isFavoriteRomm(boolean state) {
            if (state) {
                favoriteStar.setBackgroundResource(R.drawable.ic_star_black_24dp);
                favoriteText.setText(R.string.room_list_star_text_done_ru);
            } else {
                favoriteStar.setBackgroundResource(R.drawable.ic_star_border_black_24dp);
                favoriteText.setText(R.string.room_list_star_text_add_ru);
            }
        }
    }


}
