package be.kdg.teamd.beatbuddy.model.playlists;

import java.util.Date;

import be.kdg.teamd.beatbuddy.model.users.User;

public class Comment {
    private User user;
    private String text;
    private Date timeStamp;

    public String getText() {
        return text;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public User getUser() {
        return user;
    }
}
