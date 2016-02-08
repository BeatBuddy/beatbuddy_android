package be.kdg.teamd.beatbuddy.model;

public class Playlist {
    private long id;
    private String name,
                   imageUrl;
    private int maximumVotesPerUser;
    private boolean active;
    private PlaylistTrack[] playlistTracks;
    private Comment[] comments;
    private User playlistMaster;

    public boolean isActive() {
        return active;
    }

    public Comment[] getComments() {
        return comments;
    }

    public long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getMaximumVotesPerUser() {
        return maximumVotesPerUser;
    }

    public String getName() {
        return name;
    }

    public User getPlaylistMaster() {
        return playlistMaster;
    }

    public PlaylistTrack[] getPlaylistTracks() {
        return playlistTracks;
    }
}
