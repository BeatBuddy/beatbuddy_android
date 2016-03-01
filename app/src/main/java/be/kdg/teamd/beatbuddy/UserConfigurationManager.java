package be.kdg.teamd.beatbuddy;

import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;

/**
 * Created by Ignace on 22/02/2016.
 */
public interface UserConfigurationManager
{
    AccessToken getAccessToken();

    void setAccessToken(AccessToken accessToken);

    User getUser();

    void setUser(User user);

    boolean isLoggedIn();
}
