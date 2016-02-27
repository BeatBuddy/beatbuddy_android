package be.kdg.teamd.beatbuddy.model.playlists;

import java.io.Serializable;

public class Track implements Serializable {
    private long id;
    private String artist,
                   title,
                   url,
                   coverArtUrl;
    private int duration;
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

    public void setId(long id)
    {
        this.id = id;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setCoverArtUrl(String coverArtUrl)
    {
        this.coverArtUrl = coverArtUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setTrackSource(TrackSource trackSource)
    {
        this.trackSource = trackSource;
    }
}

