package be.kdg.teamd.beatbuddy.userconfiguration;

import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;

/**
 * Created by Ignace on 3/03/2016.
 */
public interface UserStore
{
    void store(User user, AccessToken accessToken);
    User restoreUser();
    AccessToken restoreAccessToken();
    void forget();
}
