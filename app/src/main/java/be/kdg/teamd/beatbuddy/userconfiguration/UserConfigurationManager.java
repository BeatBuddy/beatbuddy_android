package be.kdg.teamd.beatbuddy.userconfiguration;

import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;

/**
 * Created by Ignace on 22/02/2016.
 */
public interface UserConfigurationManager
{
    AccessToken getAccessToken();

    User getUser();

    boolean isLoggedIn();

    void logout();

    void login(AccessToken accessToken, User user, boolean storeUser);

    void restore();
}
