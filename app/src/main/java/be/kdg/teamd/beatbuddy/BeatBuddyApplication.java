package be.kdg.teamd.beatbuddy;

import android.app.Application;
import android.content.Context;

import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;
import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManager;
import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManagerImpl;
import be.kdg.teamd.beatbuddy.userconfiguration.UserStore;
import be.kdg.teamd.beatbuddy.userconfiguration.UserStoreSharedPref;

public class BeatBuddyApplication extends Application
{
    private UserConfigurationManager userConfigurationManager;

    public BeatBuddyApplication() {
        super();
        RepositoryFactory.setAPIEndpoint("https://teamd.azurewebsites.net/api/");
    }

    public void initializeUserConfiguration()
    {
        UserStore userStore = new UserStoreSharedPref(this);
        userConfigurationManager = new UserConfigurationManagerImpl(userStore);
        userConfigurationManager.restore();

        if (userConfigurationManager.isLoggedIn())
            RepositoryFactory.setAccessToken(userConfigurationManager.getAccessToken());
    }

    public UserConfigurationManager getUserConfigurationManager()
    {
        return userConfigurationManager;
    }
}
