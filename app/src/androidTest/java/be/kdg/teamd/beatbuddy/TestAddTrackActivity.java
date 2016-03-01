package be.kdg.teamd.beatbuddy;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.lang.reflect.Field;

import be.kdg.teamd.beatbuddy.activities.AddTrackActivity;
import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
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
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
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
public class TestAddTrackActivity
{
    @Rule
    public ActivityTestRule<AddTrackActivity> addTrackActivityActivityTestRule = new ActivityTestRule<>(AddTrackActivity.class);

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
        addTrackActivityActivityTestRule.getActivity().setPlaylistRepository(playlistRepository);
    }

    @Test
    public void testSearchTrack()
    {
        onView(withText("Never Gonna Give You Up"))
                .check(doesNotExist());

        onView(withId(R.id.search_track_query))
                .perform(typeText("rick astley never gonna give you up"));
        closeSoftKeyboard();
        onView(withId(R.id.search_track_fab))
                .perform(click());

        onView(withText("Never Gonna Give You Up"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSearchWrongTrack()
    {
        onView(withText("Never Gonna Give You Up"))
                .check(doesNotExist());

        onView(withId(R.id.search_track_query))
                .perform(typeText("wrong search"));
        closeSoftKeyboard();
        onView(withId(R.id.search_track_fab))
                .perform(click());

        onView(withText("Never Gonna Give You Up"))
                .check(doesNotExist());
    }

    @Test
    public void testAddTrack() throws NoSuchFieldException, IllegalAccessException {
        onView(withText("Never Gonna Give You Up"))
                .check(doesNotExist());

        onView(withId(R.id.search_track_query))
                .perform(typeText("rick astley never gonna give you up"));
        closeSoftKeyboard();
        onView(withId(R.id.search_track_fab))
                .perform(click());

        onView(withText("Never Gonna Give You Up"))
                .perform(click());

        Field f = Activity.class.getDeclaredField("mResultCode");
        f.setAccessible(true);
        int mResultCode = f.getInt(addTrackActivityActivityTestRule.getActivity());

        assertTrue("The activity result is not RESULT_OK.", mResultCode == Activity.RESULT_OK);
    }

    public class FakeInterceptor implements Interceptor
    {
        private final static String RICKROLLED_TRACK = "{\n" +
                "  \"id\": 63,\n" +
                "  \"artist\": \"Rick Astley\",\n" +
                "  \"title\": \"Never Gonna Give You Up\",\n" +
                "  \"url\": null,\n" +
                "  \"duration\": 213,\n" +
                "  \"trackSource\": {\n" +
                "    \"id\": 63,\n" +
                "    \"sourceType\": 0,\n" +
                "    \"url\": \"https://www.youtube.com/watch?v=dQw4w9WgXcQ\",\n" +
                "    \"trackId\": \"dQw4w9WgXcQ\"\n" +
                "  },\n" +
                "  \"coverArtUrl\": \"https://i.ytimg.com/vi/dQw4w9WgXcQ/maxresdefault.jpg\"\n" +
                "}";

        private final static String RICKROLLED = "[\n" +
                "  {\n" +
                "    \"id\": 0,\n" +
                "    \"artist\": \"Rick Astley\",\n" +
                "    \"title\": \"Never Gonna Give You Up\",\n" +
                "    \"url\": null,\n" +
                "    \"duration\": 0,\n" +
                "    \"trackSource\": {\n" +
                "      \"id\": 0,\n" +
                "      \"sourceType\": 0,\n" +
                "      \"url\": \"https://www.youtube.com/watch?v=dQw4w9WgXcQ\",\n" +
                "      \"trackId\": \"dQw4w9WgXcQ\"\n" +
                "    },\n" +
                "    \"coverArtUrl\": \"https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 0,\n" +
                "    \"artist\": \"Rick Astley\",\n" +
                "    \"title\": \"Never gonna give you up LIVE\",\n" +
                "    \"url\": null,\n" +
                "    \"duration\": 0,\n" +
                "    \"trackSource\": {\n" +
                "      \"id\": 0,\n" +
                "      \"sourceType\": 0,\n" +
                "      \"url\": \"https://www.youtube.com/watch?v=Rqz--Rf6IIo\",\n" +
                "      \"trackId\": \"Rqz--Rf6IIo\"\n" +
                "    },\n" +
                "    \"coverArtUrl\": \"https://i.ytimg.com/vi/Rqz--Rf6IIo/hqdefault.jpg\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 0,\n" +
                "    \"artist\": \"Rick Astley\",\n" +
                "    \"title\": \"Never Gonna Give You Up (Festival de Viña del Mar 2016)\",\n" +
                "    \"url\": null,\n" +
                "    \"duration\": 0,\n" +
                "    \"trackSource\": {\n" +
                "      \"id\": 0,\n" +
                "      \"sourceType\": 0,\n" +
                "      \"url\": \"https://www.youtube.com/watch?v=9Xak3KHH8zo\",\n" +
                "      \"trackId\": \"9Xak3KHH8zo\"\n" +
                "    },\n" +
                "    \"coverArtUrl\": \"https://i.ytimg.com/vi/9Xak3KHH8zo/hqdefault.jpg\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 0,\n" +
                "    \"artist\": \"Rick Astley\",\n" +
                "    \"title\": \"Never Gonna Give You Up [HQ]\",\n" +
                "    \"url\": null,\n" +
                "    \"duration\": 0,\n" +
                "    \"trackSource\": {\n" +
                "      \"id\": 0,\n" +
                "      \"sourceType\": 0,\n" +
                "      \"url\": \"https://www.youtube.com/watch?v=DLzxrzFCyOs\",\n" +
                "      \"trackId\": \"DLzxrzFCyOs\"\n" +
                "    },\n" +
                "    \"coverArtUrl\": \"https://i.ytimg.com/vi/DLzxrzFCyOs/hqdefault.jpg\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 0,\n" +
                "    \"artist\": \"Rick Astley\",\n" +
                "    \"title\": \"Never Gonna Give You Up [Live\",\n" +
                "    \"url\": null,\n" +
                "    \"duration\": 0,\n" +
                "    \"trackSource\": {\n" +
                "      \"id\": 0,\n" +
                "      \"sourceType\": 0,\n" +
                "      \"url\": \"https://www.youtube.com/watch?v=MvqBFno1KtA\",\n" +
                "      \"trackId\": \"MvqBFno1KtA\"\n" +
                "    },\n" +
                "    \"coverArtUrl\": \"https://i.ytimg.com/vi/MvqBFno1KtA/hqdefault.jpg\"\n" +
                "  }\n" +
                "]";

        @Override
        public Response intercept(Chain chain) throws IOException
        {
            final HttpUrl uri = chain.request().url();
            if(uri.queryParameter("query") != null && uri.queryParameter("query").equals("rick astley never gonna give you up")){
                return new Response.Builder()
                        .code(201)
                        .message(RICKROLLED)
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .body(ResponseBody.create(MediaType.parse("application/json"), RICKROLLED.getBytes()))
                        .addHeader("content-type", "application/json")
                        .build();
            }
            if(uri.queryParameter("trackId") != null && uri.queryParameter("trackId").equals("dQw4w9WgXcQ")){
                return new Response.Builder()
                        .code(201)
                        .message(RICKROLLED_TRACK)
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .body(ResponseBody.create(MediaType.parse("application/json"), RICKROLLED_TRACK.getBytes()))
                        .addHeader("content-type", "application/json")
                        .build();
            }

            return new Response.Builder()
                    .code(409)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .build();
        }
    }
}

