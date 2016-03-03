package be.kdg.teamd.beatbuddy.userconfiguration;

import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;
import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManager;

/**
 * Created by Ignace on 22/02/2016.
 */
public class FakeUserConfigurationManager implements UserConfigurationManager
{
    private AccessToken accessToken;
    private User user;

    @Override
    public AccessToken getAccessToken()
    {
        return accessToken;
    }

    @Override
    public User getUser()
    {
        return user;
    }

    @Override
    public boolean isLoggedIn()
    {
        return accessToken != null;
    }

    @Override
    public void logout()
    {
        accessToken = null;
        user = null;
    }

    @Override
    public void login(AccessToken accessToken, User user, boolean storeUser)
    {
        this.accessToken = accessToken;
        this.user = user;
    }

    @Override
    public void restore()
    {

    }
}
