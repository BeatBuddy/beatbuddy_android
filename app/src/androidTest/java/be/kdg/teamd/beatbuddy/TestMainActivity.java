package be.kdg.teamd.beatbuddy;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import be.kdg.teamd.beatbuddy.activities.LoginActivity;
import be.kdg.teamd.beatbuddy.activities.MainActivity;
import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestMainActivity {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testDrawerLayoutVisible(){
        onView(withId(R.id.drawer_layout))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testFabShownWhenLoggedIn(){
        onView(withId(R.id.main_fab))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        openDrawer(R.id.drawer_layout);

        Intents.init();

        User user = new User();
        user.setId(1234);
        user.setEmail("maarten.vangiel@hotmail.com");
        user.setFirstName("Maarten");
        user.setLastName("Van Giel");
        user.setNickname("Mavamaarten");

        UserConfigurationManager userConfigurationManager = (UserConfigurationManager)mainActivityActivityTestRule.getActivity().getApplication();
        userConfigurationManager.setUser(user);
        userConfigurationManager.setAccessToken(new AccessToken());

        Intent resultIntent = new Intent();
        resultIntent.putExtra("user", user);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, new Intent());

        intending(hasComponent(LoginActivity.class.getName()))
                .respondWith(result);

        onView(withText("Login"))
                .perform(click());

        onView(withId(R.id.main_fab))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
}
