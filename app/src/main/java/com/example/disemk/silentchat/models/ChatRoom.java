package com.example.disemk.silentchat.models;

import java.util.List;

/**
 * Created by disemk on 15.01.17.
 */

public class ChatRoom {
    String roomName;

    public ChatRoom() {
    }

    public ChatRoom(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
