package be.kdg.teamd.beatbuddy.model.playlists;

import java.io.Serializable;
import java.util.Date;

import be.kdg.teamd.beatbuddy.model.users.User;

public class Comment implements Serializable {
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
