package be.kdg.teamd.beatbuddy.model;

import java.util.Date;

public class Comment {
    private User user;
    private String text;
    private Date timestamp;

    public String getText() {
        return text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public User getUser() {
        return user;
    }
}
