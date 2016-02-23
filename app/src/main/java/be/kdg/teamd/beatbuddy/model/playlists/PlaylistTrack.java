package be.kdg.teamd.beatbuddy.model.playlists;

import java.io.Serializable;

public class PlaylistTrack implements Serializable {
    private Track track;
    private boolean alreadyPlayed;
    private Vote[] votes; // NOTE: keep score only?

    public boolean isAlreadyPlayed() {
        return alreadyPlayed;
    }

    public Track getTrack() {
        return track;
    }

    public Vote[] getVotes() {
        return votes;
    }
}
