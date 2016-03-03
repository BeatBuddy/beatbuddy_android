package be.kdg.teamd.beatbuddy.userconfiguration;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;

/**
 * Created by Ignace on 3/03/2016.
 */
public class UserStoreSharedPref implements UserStore
{
    public static final String PREFS_NAME = "USERSTORE";
    public static final String PREF_USER = "USER";
    public static final String PREF_ACCESS_TOKEN = "ACCESS_TOKEN";

    private SharedPreferences pref;
    private Gson gson;

    public UserStoreSharedPref(Context context)
    {
        pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    @Override
    public void store(User user, AccessToken accessToken)
    {
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(PREF_USER, gson.toJson(user));
        editor.putString(PREF_ACCESS_TOKEN, gson.toJson(accessToken));

        editor.apply();
    }

    @Override
    public User restoreUser()
    {
        return gson.fromJson(pref.getString(PREF_USER, ""), User.class);
    }

    @Override
    public AccessToken restoreAccessToken()
    {
        return gson.fromJson(pref.getString(PREF_ACCESS_TOKEN, ""), AccessToken.class);
    }

    @Override
    public void forget()
    {
        store(null, null);
    }
}
