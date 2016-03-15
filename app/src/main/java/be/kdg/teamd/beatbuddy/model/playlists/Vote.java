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

    public void setId(long id)
    {
        this.id = id;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}
