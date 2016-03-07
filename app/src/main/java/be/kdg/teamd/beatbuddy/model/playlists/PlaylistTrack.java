package be.kdg.teamd.beatbuddy.model.playlists;

import java.io.Serializable;
import java.util.List;

public class PlaylistTrack implements Serializable {
    private Track track;
    private int score;
    private int personalScore;

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
