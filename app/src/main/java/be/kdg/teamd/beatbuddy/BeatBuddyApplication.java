package be.kdg.teamd.beatbuddy;

import android.app.Application;

import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;

public class BeatBuddyApplication extends Application {
    public BeatBuddyApplication() {
        super();
        RepositoryFactory.setAPIEndpoint("http://teamd.azurewebsites.net/api/");
    }
}
