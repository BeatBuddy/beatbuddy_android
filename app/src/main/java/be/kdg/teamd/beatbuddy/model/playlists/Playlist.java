package be.kdg.teamd.beatbuddy.model.playlists;

import java.io.Serializable;
import java.util.List;

import be.kdg.teamd.beatbuddy.model.users.User;

public class Playlist implements Serializable {
    private long id;
    private String name,
                   imageUrl;
    private int maximumVotesPerUser;
    private boolean active;
    private List<PlaylistTrack> playlistTracks;
    private List<Comment> comments,
                      chatComments;
    private User playlistMaster;
    private String key;

    public boolean isActive() {
        return active;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<Comment> getChatComments() {
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

    public List<PlaylistTrack> getPlaylistTracks() {
        return playlistTracks;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }
}
