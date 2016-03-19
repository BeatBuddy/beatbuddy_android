package be.kdg.teamd.beatbuddy;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import be.kdg.teamd.beatbuddy.activities.PlaylistActivity;
import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import be.kdg.teamd.beatbuddy.userconfiguration.FakeUserConfigurationManager;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestPlaylistActivity {

    @Rule
    public ActivityTestRule<PlaylistActivity> playlistActivityTestRule = new ActivityTestRule<PlaylistActivity>(PlaylistActivity.class, true, false);

    private PlaylistRepository playlistRepository;

    @Before
    public void setup() {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new FakeInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost/api/Playlist/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        playlistRepository = retrofit.create(PlaylistRepository.class);
    }

    @Test
    public void testPlaylistShown() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Playlist playlist = new Playlist();
        playlist.setKey("chirowafels");
        playlist.setId(4);

        Intent intent = new Intent(targetContext, PlaylistActivity.class);
        intent.putExtra(PlaylistActivity.EXTRA_PLAYLIST, playlist);
        intent.putExtra(PlaylistActivity.EXTRA_PLAYLIST_KEY, "1");
        intent.putExtra(PlaylistActivity.EXTRA_PLAYLIST_TEST, true);

        playlistActivityTestRule.launchActivity(intent);
        playlistActivityTestRule.getActivity().setPlaylistRepository(playlistRepository, new FakeUserConfigurationManager());

        wait(500);

        playlistActivityTestRule.getActivity().onQueueRefreshRequested();

        onView(withChild(withText("Hideaway (Official Video)")))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    public class FakeInterceptor implements Interceptor {
        private final static String PLAYLIST_DATA = "{\n" +
                "  \"chatComments\": [],\n" +
                "  \"comments\": [],\n" +
                "  \"playlistTracks\": [\n" +
                "    {\n" +
                "      \"votes\": [],\n" +
                "      \"id\": 18,\n" +
                "      \"alreadyPlayed\": false,\n" +
                "      \"track\": {\n" +
                "        \"id\": 18,\n" +
                "        \"artist\": \"Kiesza\",\n" +
                "        \"title\": \"Hideaway (Official Video)\",\n" +
                "        \"url\": null,\n" +
                "        \"duration\": 275,\n" +
                "        \"trackSource\": {\n" +
                "          \"id\": 18,\n" +
                "          \"sourceType\": 0,\n" +
                "          \"url\": \"https://www.youtube.com/watch?v=ESXgJ9-H-2U\",\n" +
                "          \"trackId\": \"ESXgJ9-H-2U\"\n" +
                "        },\n" +
                "        \"coverArtUrl\": \"https://i.ytimg.com/vi/ESXgJ9-H-2U/maxresdefault.jpg\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"votes\": [],\n" +
                "      \"id\": 19,\n" +
                "      \"alreadyPlayed\": false,\n" +
                "      \"track\": {\n" +
                "        \"id\": 19,\n" +
                "        \"artist\": \"Gentlemen's Club\",\n" +
                "        \"title\": \"New Presidents (feat. Watson) (Hedex Remix)\",\n" +
                "        \"url\": null,\n" +
                "        \"duration\": 309,\n" +
                "        \"trackSource\": {\n" +
                "          \"id\": 19,\n" +
                "          \"sourceType\": 0,\n" +
                "          \"url\": \"https://www.youtube.com/watch?v=ddPs-vJiFvw\",\n" +
                "          \"trackId\": \"ddPs-vJiFvw\"\n" +
                "        },\n" +
                "        \"coverArtUrl\": \"https://i.ytimg.com/vi/ddPs-vJiFvw/maxresdefault.jpg\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"votes\": [],\n" +
                "      \"id\": 20,\n" +
                "      \"alreadyPlayed\": false,\n" +
                "      \"track\": {\n" +
                "        \"id\": 20,\n" +
                "        \"artist\": \"Major Lazer\",\n" +
                "        \"title\": \"Light It Up (feat. Nyla & Fuse ODG) [Remix]\",\n" +
                "        \"url\": null,\n" +
                "        \"duration\": 167,\n" +
                "        \"trackSource\": {\n" +
                "          \"id\": 20,\n" +
                "          \"sourceType\": 0,\n" +
                "          \"url\": \"https://www.youtube.com/watch?v=qDcFryDXQ7U\",\n" +
                "          \"trackId\": \"qDcFryDXQ7U\"\n" +
                "        },\n" +
                "        \"coverArtUrl\": \"https://i.ytimg.com/vi/qDcFryDXQ7U/maxresdefault.jpg\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"votes\": [],\n" +
                "      \"id\": 21,\n" +
                "      \"alreadyPlayed\": false,\n" +
                "      \"track\": {\n" +
                "        \"id\": 21,\n" +
                "        \"artist\": \"Fytch\",\n" +
                "        \"title\": \"Just Like Gold (ft. Naika)\",\n" +
                "        \"url\": null,\n" +
                "        \"duration\": 259,\n" +
                "        \"trackSource\": {\n" +
                "          \"id\": 21,\n" +
                "          \"sourceType\": 0,\n" +
                "          \"url\": \"https://www.youtube.com/watch?v=n9p5u7GSXGU\",\n" +
                "          \"trackId\": \"n9p5u7GSXGU\"\n" +
                "        },\n" +
                "        \"coverArtUrl\": \"https://i.ytimg.com/vi/n9p5u7GSXGU/maxresdefault.jpg\"\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Maarten's #swaggy playlist\",\n" +
                "  \"key\": \"key\",\n" +
                "  \"maximumVotesPerUser\": 1,\n" +
                "  \"active\": true,\n" +
                "  \"imageUrl\": null,\n" +
                "  \"playlistMasterId\": null,\n" +
                "  \"createdById\": 1,\n" +
                "  \"description\": \"Boenke boenke\"\n" +
                "}";

        @Override
        public Response intercept(Chain chain) throws IOException {
                return new Response.Builder()
                        .code(200)
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_0)
                        .body(ResponseBody.create(MediaType.parse("application/json"), PLAYLIST_DATA.getBytes()))
                        .addHeader("content-type", "application/json")
                        .build();

        }
    }

}