package be.kdg.teamd.beatbuddy;

import android.app.Application;

import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;

public class BeatBuddyApplication extends Application implements UserConfigurationManager
{
    private AccessToken accessToken;
    private User user;

    public BeatBuddyApplication() {
        super();
        RepositoryFactory.setAPIEndpoint("https://teamd.azurewebsites.net/api/");
    }

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
