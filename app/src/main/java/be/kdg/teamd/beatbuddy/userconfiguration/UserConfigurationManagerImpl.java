package be.kdg.teamd.beatbuddy.userconfiguration;

import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;

/**
 * Created by Ignace on 3/03/2016.
 */
public class UserConfigurationManagerImpl implements UserConfigurationManager
{
    private User user;
    private AccessToken accessToken;
    private UserStore userStore;

    public UserConfigurationManagerImpl(UserStore userStore)
    {
        this.userStore = userStore;
    }

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
        user = null;
        accessToken = null;

        userStore.forget();
    }

    @Override
    public void login(AccessToken accessToken, User user, boolean storeUser)
    {
        this.user = user;
        this.accessToken = accessToken;

        if (storeUser)
        {
            userStore.store(user, accessToken);
        }
    }

    @Override
    public void restore()
    {
        this.user = userStore.restoreUser();
        this.accessToken = userStore.restoreAccessToken();
    }
}
