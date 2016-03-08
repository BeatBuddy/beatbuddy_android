package be.kdg.teamd.beatbuddy.model.playlists;

/**
 * Created by Ignace on 8/03/2016.
 */
public class CurrentPlayingViewModel
{
    private String TrackId;
    private String Title;
    private String Artist;
    private int NextTracks;
    private String  CoverArtUrl;

    public String getTrackId()
    {
        return TrackId;
    }

    public void setTrackId(String trackId)
    {
        TrackId = trackId;
    }

    public String getTitle()
    {
        return Title;
    }

    public void setTitle(String title)
    {
        Title = title;
    }

    public String getArtist()
    {
        return Artist;
    }

    public void setArtist(String artist)
    {
        Artist = artist;
    }

    public int getNextTracks()
    {
        return NextTracks;
    }

    public void setNextTracks(int nextTracks)
    {
        NextTracks = nextTracks;
    }

    public String getCoverArtUrl()
    {
        return CoverArtUrl;
    }

    public void setCoverArtUrl(String coverArtUrl)
    {
        CoverArtUrl = coverArtUrl;
    }
}