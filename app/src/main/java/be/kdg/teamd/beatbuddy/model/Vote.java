package be.kdg.teamd.beatbuddy.model;

public class Vote {
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
