package be.kdg.teamd.beatbuddy.model.playlists;

import java.io.Serializable;

import be.kdg.teamd.beatbuddy.model.users.User;

public class Vote implements Serializable {
    private long id;
    private int score;
    private User user; // NOTE: only keep userId?

    public long getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public User getUser() {
        return user;
    }
}
