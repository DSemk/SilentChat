package com.example.disemk.silentchat.models;

/**
 * Created by disemk on 11.01.17.
 */

public class ChatMessage {
    private String text;
    private String name;
    private String photoUrl;
    private String room;

    public ChatMessage() {
    }

    public ChatMessage(String text, String name, String photoUrl, String room) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.room = room;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
