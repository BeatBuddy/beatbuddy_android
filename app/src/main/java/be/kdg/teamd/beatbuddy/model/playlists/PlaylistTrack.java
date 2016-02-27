package be.kdg.teamd.beatbuddy.model.playlists;

import java.io.Serializable;
import java.util.List;

public class PlaylistTrack implements Serializable {
    private Track track;
    private boolean alreadyPlayed;
    private List<Vote> votes; // NOTE: keep score only?

    public boolean isAlreadyPlayed() {
        return alreadyPlayed;
    }

    public Track getTrack() {
        return track;
    }

    public List<Vote> getVotes() {
        return votes;
    }
}
