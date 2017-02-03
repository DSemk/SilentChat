package com.example.disemk.silentchat.models;

import java.util.List;

/**
 * Created by disemk on 15.01.17.
 */

public class ChatRoom {
    String roomName;
    String roomKey;

    public ChatRoom() {
    }

    public ChatRoom(String roomName, String roomKey) {
        this.roomName = roomName;
        this.roomKey = roomKey;
    }

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
