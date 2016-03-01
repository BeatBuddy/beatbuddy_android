package be.kdg.teamd.beatbuddy;

import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;

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
    public void setAccessToken(AccessToken accessToken)
    {
        this.accessToken = accessToken;
    }

    @Override
    public User getUser()
    {
        return user;
    }

    @Override
    public void setUser(User user)
    {
        this.user = user;
    }

    @Override
    public boolean isLoggedIn()
    {
        return accessToken != null;
    }
}
