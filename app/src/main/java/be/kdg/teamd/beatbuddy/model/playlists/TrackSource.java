package be.kdg.teamd.beatbuddy.model.playlists;

import java.io.Serializable;

public class TrackSource implements Serializable {
    private String url;
    private SourceType sourceType;

    public SourceType getSourceType() {
        return sourceType;
    }

    public String getUrl() {
        return url;
    }
}
