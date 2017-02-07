package com.example.disemk.silentchat.engine;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by icoper on 26.01.17.
 */

public class SingletonCM {

    private static SingletonCM ourInstance = new SingletonCM();

    public static SingletonCM getInstance() {
        return ourInstance;
    }

    public SingletonCM() {
    }

    private int backgroundID;
    private Context mainContext;
    private String userRoom;
    private String userName;
    private String userIcon;
    private String userFilterRoom;
    private ArrayList<String> favoriteRoomList;

    public ArrayList<String> getFavoriteRoomList() {
        return favoriteRoomList;
    }

    public void setFavoriteRoomList(ArrayList<String> favoriteRoomList) {
        this.favoriteRoomList = favoriteRoomList;
    }

    public String getUserFilterRoom() {
        return userFilterRoom;
    }

    public void setUserFilterRoom(String userFilterRoom) {
        this.userFilterRoom = userFilterRoom;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserRoom() {
        return userRoom;
    }

    public void setUserRoom(String userRoom) {
        this.userRoom = userRoom;
    }

    public Context getMainContext() {
        return mainContext;
    }

    public void setMainContext(Context mainContext) {
        this.mainContext = mainContext;
    }

    public int getBackgroundID() {
        return backgroundID;
    }

    public void setBackgroundID(int backgroundID) {
        this.backgroundID = backgroundID;
    }


}
