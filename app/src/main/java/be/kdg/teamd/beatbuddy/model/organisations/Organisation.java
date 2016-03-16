package be.kdg.teamd.beatbuddy.model.organisations;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import be.kdg.teamd.beatbuddy.model.users.Role;
import be.kdg.teamd.beatbuddy.model.users.User;

public class Organisation implements Serializable {
    private long id;
    private String name,
                    bannerUrl,
                    colorScheme,
                    key;
    private List<Playlist> playlists;
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

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public Map<User, Role> getUsers() {
        return users;
    }
}
