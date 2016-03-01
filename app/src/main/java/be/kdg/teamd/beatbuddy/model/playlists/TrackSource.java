package be.kdg.teamd.beatbuddy.model.playlists;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TrackSource implements Serializable
{
    private long id;
    private SourceType sourceType;
    private String url;
    private String trackId;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public SourceType getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType)
    {
        this.sourceType = sourceType;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getTrackId()
    {
        return trackId;
    }

    public void setTrackId(String trackId)
    {
        this.trackId = trackId;
    }
}
