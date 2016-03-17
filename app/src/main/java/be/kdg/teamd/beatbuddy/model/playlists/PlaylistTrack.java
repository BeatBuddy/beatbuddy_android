package be.kdg.teamd.beatbuddy.model.playlists;

import java.io.Serializable;

public class PlaylistTrack implements Serializable {
    private Track track;
    private int score;
    private int personalScore;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Track getTrack() {
        return track;
    }

    public int getScore()
    {
        return score;
    }

    public int getPersonalScore()
    {
        return personalScore;
    }

    public void setTrack(Track track)
    {
        this.track = track;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public void setPersonalScore(int personalScore)
    {
        this.personalScore = personalScore;
    }
}
