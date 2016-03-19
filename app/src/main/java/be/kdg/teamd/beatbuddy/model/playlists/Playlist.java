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

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setChatComments(List<Comment> chatComments) {
        this.chatComments = chatComments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMaximumVotesPerUser(int maximumVotesPerUser) {
        this.maximumVotesPerUser = maximumVotesPerUser;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlaylistMaster(User playlistMaster) {
        this.playlistMaster = playlistMaster;
    }

    public void setPlaylistTracks(List<PlaylistTrack> playlistTracks) {
        this.playlistTracks = playlistTracks;
    }

    public void setId(long id) {

        this.id = id;
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
