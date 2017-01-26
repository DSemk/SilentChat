package com.example.disemk.silentchat;

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

    public int getBackgroundID() {
        return backgroundID;
    }

    public void setBackgroundID(int backgroundID) {
        this.backgroundID = backgroundID;
    }
}
