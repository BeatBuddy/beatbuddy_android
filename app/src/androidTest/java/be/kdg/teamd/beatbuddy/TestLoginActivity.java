package be.kdg.teamd.beatbuddy;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.lang.reflect.Field;

import be.kdg.teamd.beatbuddy.activities.LoginActivity;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestLoginActivity {

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    @Before
    public void setup() {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new FakeInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        UserRepository userRepository = retrofit.create(UserRepository.class);
        UserConfigurationManager userConfigurationManager = new TestUserConfigurationManager();

        loginActivityActivityTestRule.getActivity().setTestImplementation(userRepository, userConfigurationManager);
    }

    @Test
    public void testLogin() throws NoSuchFieldException, IllegalAccessException {
        onView(withId(R.id.edit_login_email))
                .perform(typeText("maarten.vangiel@email.com"));

        onView(withId(R.id.edit_login_password))
                .perform(typeText("maartenpassword"));

        closeSoftKeyboard();

        onView(withId(R.id.btn_login))
                .perform(click());

        Field f = Activity.class.getDeclaredField("mResultCode");
        f.setAccessible(true);
        int mResultCode = f.getInt(loginActivityActivityTestRule.getActivity());

        assertTrue("The activity result is not RESULT_OK.", mResultCode == Activity.RESULT_OK);
    }

    @Test
    public void testFailedLogin() throws NoSuchFieldException, IllegalAccessException {
        onView(withId(R.id.edit_login_email))
                .perform(typeText("maarten.vangiel@hotmail.com"));

        onView(withId(R.id.edit_login_password))
                .perform(typeText("maartenwrongpassword"));

        closeSoftKeyboard();

        onView(withId(R.id.btn_login))
                .perform(click());

        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(startsWith("Login failed."))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterCopiesFields() throws NoSuchFieldException, IllegalAccessException {
        onView(withId(R.id.edit_login_email))
                .perform(typeText("maarten.vangiel@hotmail.com"));

        onView(withId(R.id.edit_login_password))
                .perform(typeText("maartenpassword"));

        closeSoftKeyboard();

        onView(withId(R.id.btn_register))
                .perform(click());

        onView(withId(R.id.edit_register_email))
                .check(matches(withText("maarten.vangiel@hotmail.com")));

        onView(withId(R.id.edit_register_password))
                .check(matches(withText("maartenpassword")));
    }

    public class FakeInterceptor implements Interceptor {
        private final static String USER_MAARTEN = "{\"id\":123456, \"email\":\"maarten.vangiel@email.com\", \"firstName\":\"Maarten\", \"lastName\":\"Van Giel\", \"nickname\":\"Mavamaarten\", \"imageUrl\":\"http://www.google.com\" }";

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            // TODO: test verandere naar field ipv query parameter
            final HttpUrl uri = chain.request().url();
            if(uri.queryParameter("email").equals("maarten.vangiel@email.com") && uri.queryParameter("password").equals("maartenpassword")){
                return new Response.Builder()
                        .code(200)
                        .message(USER_MAARTEN)
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_0)
                        .body(ResponseBody.create(MediaType.parse("application/json"), USER_MAARTEN.getBytes()))
                        .addHeader("content-type", "application/json")
                        .build();
            }

            return new Response.Builder()
                    .code(403)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_0)
                    .build();
        }
    }

}