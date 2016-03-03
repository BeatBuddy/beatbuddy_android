package be.kdg.teamd.beatbuddy;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.lang.reflect.Field;

import be.kdg.teamd.beatbuddy.activities.CreatePlaylistActivity;
import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
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
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestCreatePlaylistActivity
{
    private Instrumentation.ActivityResult result;

    @Rule
    public ActivityTestRule<CreatePlaylistActivity> createPlaylistActivityTestRule = new ActivityTestRule<>(CreatePlaylistActivity.class);

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

        PlaylistRepository playlistRepository = retrofit.create(PlaylistRepository.class);
        UserRepository userRepository = retrofit.create(UserRepository.class);
        createPlaylistActivityTestRule.getActivity().setPlaylistRepository(playlistRepository);
        createPlaylistActivityTestRule.getActivity().setUserRepository(userRepository);
    }

    @Before
    public void setupIntentResult()
    {
        Resources resources = InstrumentationRegistry.getTargetContext().getResources();
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(R.mipmap.ic_launcher) + '/' +
                resources.getResourceTypeName(R.mipmap.ic_launcher) + '/' +
                resources.getResourceEntryName(R.mipmap.ic_launcher));

        Intent resultData = new Intent();
        resultData.setData(imageUri);
        result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
    }

    @Test
    public void testSelectImage()
    {
        onView(withId(R.id.text_playlist_image)).check(matches(withText("Image")));

        Intents.init();
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_PICK), hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        intending(expectedIntent).respondWith(result);

        onView(withId(R.id.btn_playlist_pick_img)).perform(click());
        intended(expectedIntent);
        Intents.release();

        onView(withId(R.id.text_playlist_image)).check(matches(not(withText("Image"))));
    }

    @Test
    public void testCreateOrganisation() throws NoSuchFieldException, IllegalAccessException {
        onView(withId(R.id.edit_playlist_name))
                .perform(typeText("Android playlist"));

        closeSoftKeyboard();

        onView(withId(R.id.edit_playlist_key))
                .perform(typeText("key"));

        closeSoftKeyboard();

        onView(withId(R.id.edit_playlist_description))
                .perform(typeText("Playlist gemaakt op android"));

        closeSoftKeyboard();

        Intents.init();
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_PICK), hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        intending(expectedIntent).respondWith(result);

        onView(withId(R.id.btn_playlist_pick_img))
                .perform(click());

        intended(expectedIntent);
        Intents.release();

        onView(withId(R.id.btn_create_playlist))
                .perform(click());

        Field f = Activity.class.getDeclaredField("mResultCode");
        f.setAccessible(true);
        int mResultCode = f.getInt(createPlaylistActivityTestRule.getActivity());

        assertTrue("The activity result is not RESULT_OK.", mResultCode == Activity.RESULT_OK);
    }

    @Test
    public void testCreateEmpty() throws NoSuchFieldException, IllegalAccessException {
        closeSoftKeyboard();

        onView(withId(R.id.btn_create_playlist))
            .perform(click());

        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("Please fill in all fields")))
                .check(matches(isDisplayed()));
    }

        //from here:https://github.com/googlesamples/android-testing/blob/master/ui/espresso/IntentsAdvancedSample/app/src/androidTest/java/com/example/android/testing/espresso/intents/AdvancedSample/ImageViewHasDrawableMatcher.java
    public static BoundedMatcher<View, ImageView> hasDrawable() {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has drawable");
            }

            @Override
            public boolean matchesSafely(ImageView imageView) {
                return imageView.getDrawable() != null;
            }
        };
    }

    public class FakeInterceptor implements Interceptor
    {
        private final static String PLAYLIST_SUCCESS = "{ \"id\": 6, \"name\": \"Android playlist\", \"key\": \"key\", \"maximumVotesPerUser\": 1, \"active\": false, \"imageUrl\": \"playlist.jpg\", \"playlistMasterId\": null, \"createdById\": 1, \"description\": \"Playlist gemaakt op android\", \"playlistTracks\": [], \"comments\": [], \"chatComments\": []  }";

        @Override
        public Response intercept(Chain chain) throws IOException
        {
            return new Response.Builder()
                    .code(200)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .body(ResponseBody.create(MediaType.parse("application/json"), PLAYLIST_SUCCESS.getBytes()))
                    .addHeader("content-type", "application/json")
                    .build();
        }
    }
}

