package be.kdg.teamd.beatbuddy.model;

public class PlaylistTrack {
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
