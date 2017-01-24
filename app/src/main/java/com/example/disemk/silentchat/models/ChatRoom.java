package com.example.disemk.silentchat.models;

import java.util.List;

/**
 * Created by disemk on 15.01.17.
 */

public class ChatRoom {
    List<String> roomName;

    public ChatRoom(List<String> roomName) {
        this.roomName = roomName;
    }

    public List<String> getRoomName() {
        return roomName;
    }

    public void setRoomName(List<String> roomName) {
        this.roomName = roomName;
    }

}
