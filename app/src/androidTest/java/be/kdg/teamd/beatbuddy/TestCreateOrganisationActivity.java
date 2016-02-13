package be.kdg.teamd.beatbuddy;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.lang.reflect.Field;

import be.kdg.teamd.beatbuddy.activities.CreateOrganisationActivity;
import be.kdg.teamd.beatbuddy.dal.OrganisationRepository;
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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestCreateOrganisationActivity
{

    @Rule
    public ActivityTestRule<CreateOrganisationActivity> createOrganisationActivityTestRule = new ActivityTestRule<>(CreateOrganisationActivity.class);

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

        OrganisationRepository organisationRepository = retrofit.create(OrganisationRepository.class);
        createOrganisationActivityTestRule.getActivity().setOrganisationRepository(organisationRepository);
    }

    @Test
    public void testCreateOrganisation() throws NoSuchFieldException, IllegalAccessException {
        onView(withId(R.id.create_org_name))
                .perform(typeText("Ignace's home party"));
        onView(withId(R.id.create_org_banner_url))
                .perform(typeText("http://7-themes.com/data_images/out/41/6908980-party-time.jpg"));
        onView(withId(R.id.create_org_color_scheme))
                .perform(typeText("blauw"));
        onView(withId(R.id.create_org_key))
                .perform(typeText("ignace"));

        closeSoftKeyboard();

        onView(withId(R.id.create_org_create))
                .perform(click());

        Field f = Activity.class.getDeclaredField("mResultCode");
        f.setAccessible(true);
        int mResultCode = f.getInt(createOrganisationActivityTestRule.getActivity());

        assertTrue("The activity result is not RESULT_OK.", mResultCode == Activity.RESULT_OK);
    }

    @Test
    public void testCreateOrganisationWithEmptyFields() throws NoSuchFieldException, IllegalAccessException {
        onView(withId(R.id.create_org_create))
                .perform(click());

        Field f = Activity.class.getDeclaredField("mResultCode");
        f.setAccessible(true);
        int mResultCode = f.getInt(createOrganisationActivityTestRule.getActivity());

        assertTrue("The activity result should not be RESULT_OK with empty fields.", mResultCode != Activity.RESULT_OK);
    }

    public class FakeInterceptor implements Interceptor
    {
        private final static String ORGANISATION_IGNACE = "{\"id\":123456, \"name\":\"Ignace's home party\", \"bannerUrl\":\"http://7-themes.com/data_images/out/41/6908980-party-time.jpg\", \"colorScheme\":\"blauw\", \"key\":\"ignace\" }";

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException
        {
            final HttpUrl uri = chain.request().url();
            if(uri.queryParameter("name").equals("Ignace's home party") && uri.queryParameter("bannerUrl").equals("http://7-themes.com/data_images/out/41/6908980-party-time.jpg")
                    && uri.queryParameter("colorScheme").equals("blauw") && uri.queryParameter("key").equals("ignace")){
                return new Response.Builder()
                        .code(201)
                        .message(ORGANISATION_IGNACE)
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .body(ResponseBody.create(MediaType.parse("application/json"), ORGANISATION_IGNACE.getBytes()))
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
