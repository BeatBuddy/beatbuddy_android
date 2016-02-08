package be.kdg.teamd.beatbuddy.model;

import java.util.Map;

public class Organisation {
    private long id;
    private String name,
                    bannerUrl,
                    colorScheme,
                    key;
    private Playlist[] playlists;
    private Map<User, Role> users;

    public String getBannerUrl() {
        return bannerUrl;
    }

    public String getColorScheme() {
        return colorScheme;
    }

    public long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Playlist[] getPlaylists() {
        return playlists;
    }

    public Map<User, Role> getUsers() {
        return users;
    }
}
