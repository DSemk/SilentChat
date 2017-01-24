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
//
//
//    {
//// Chats contains only meta info about each conversation
//// stored under the chats's unique ID
//        "chats": {
//        "one": {
//            "title": "Historical Tech Pioneers",
//                    "lastMessage": "ghopper: Relay malfunction found. Cause: moth.",
//                    "timestamp": 1459361875666
//        },
//        "two": { ... },
//        "three": { ... }
//    },
//
//// Conversation members are easily accessible
//// and stored by chat conversation ID
//        "members": {
//// we'll talk about indices like this below
//        "one": {
//            "ghopper": true,
//                    "alovelace": true,
//                    "eclarke": true
//        },
//        "two": { ... },
//        "three": { ... }
//    },
//
//// Messages are separate from data we may want to iterate quickly
//// but still easily paginated and queried, and organized by chat
//// conversation ID
//        "messages": {
//        "one": {
//            "m1": {
//                "name": "eclarke",
//                        "message": "The relay seems to be malfunctioning.",
//                        "timestamp": 1459361875337
//            },
//            "m2": { ... },
//            "m3": { ... }
//        },
//        "two": { ... },
//        "three": { ... }
//    }
//    }

}
