package be.kdg.teamd.beatbuddy.model.playlists;

import java.io.Serializable;

import be.kdg.teamd.beatbuddy.model.users.User;

public class Comment implements Serializable {
    private User user;
    private String text;
    private String timeStamp;

    public String getText() {
        return text;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public User getUser() {
        return user;
    }
}
