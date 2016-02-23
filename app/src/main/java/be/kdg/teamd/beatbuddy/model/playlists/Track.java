package be.kdg.teamd.beatbuddy.model.playlists;

import java.io.Serializable;

public class Track implements Serializable {
    private long id;
    private String artist,
                   title,
                   url,
                   coverArtUrl;
    private TrackSource trackSource;

    public String getArtist() {
        return artist;
    }

    public String getCoverArtUrl() {
        return coverArtUrl;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TrackSource getTrackSource() {
        return trackSource;
    }

    public String getUrl() {
        return url;
    }
}

