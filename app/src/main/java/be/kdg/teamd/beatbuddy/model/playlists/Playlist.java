package be.kdg.teamd.beatbuddy.model.playlists;

import java.io.Serializable;

import be.kdg.teamd.beatbuddy.model.users.User;

public class Playlist implements Serializable {
    private long id;
    private String name,
                   imageUrl;
    private int maximumVotesPerUser;
    private boolean active;
    private PlaylistTrack[] playlistTracks;
    private Comment[] comments,
                      chatComments;
    private User playlistMaster;

    public boolean isActive() {
        return active;
    }

    public Comment[] getComments() {
        return comments;
    }

    public Comment[] getChatComments() {
        return chatComments;
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
