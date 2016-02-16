package be.kdg.teamd.beatbuddy;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import org.hamcrest.TypeSafeMatcher;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestCreateOrganisationActivity
{
    private Instrumentation.ActivityResult result;

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
        //Check the image is not displayed
        onView(withId(R.id.create_org_banner)).check(matches(withDrawable(R.drawable.header_default)));

        //Setup the intent
        Intents.init();
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_PICK), hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        intending(expectedIntent).respondWith(result);

        //Click the select button
        onView(withId(R.id.create_org_banner_add)).perform(click());
        intended(expectedIntent);
        Intents.release();

        //Check the image is displayed
        onView(withId(R.id.create_org_banner)).check(matches(not(withDrawable(R.drawable.header_default))));
    }

    @Test
    public void testCreateOrganisation() throws NoSuchFieldException, IllegalAccessException {
        onView(withId(R.id.create_org_name))
                .perform(typeText("Chiro"));
        onView(withId(R.id.create_org_description))
                .perform(typeText("Dit is de chiro van wijnegem"));
        onView(withId(R.id.create_org_color_picker))
                .perform(click());

        closeSoftKeyboard();

        //Select banner image intent
        Intents.init();
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_PICK), hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        intending(expectedIntent).respondWith(result);

        //Click the select button
        onView(withId(R.id.create_org_banner_add)).perform(click());
        intended(expectedIntent);
        Intents.release();

        //Send create
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

    public static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    public static Matcher<View> noDrawable() {
        return new DrawableMatcher(-1);
    }

    public static class DrawableMatcher extends TypeSafeMatcher<View>
    {

        private final int expectedId;
        String resourceName;

        public DrawableMatcher(int expectedId) {
            super(View.class);
            this.expectedId = expectedId;
        }

        @Override
        protected boolean matchesSafely(View target) {
            if (!(target instanceof ImageView)){
                return false;
            }
            ImageView imageView = (ImageView) target;
            if (expectedId < 0){
                return imageView.getDrawable() == null;
            }
            Resources resources = target.getContext().getResources();
            Drawable expectedDrawable = resources.getDrawable(expectedId);
            resourceName = resources.getResourceEntryName(expectedId);

            if (expectedDrawable == null) {
                return false;
            }

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            Bitmap otherBitmap = ((BitmapDrawable) expectedDrawable).getBitmap();
            return bitmap.sameAs(otherBitmap);
        }


        @Override
        public void describeTo(Description description) {
            description.appendText("with drawable from resource id: ");
            description.appendValue(expectedId);
            if (resourceName != null) {
                description.appendText("[");
                description.appendText(resourceName);
                description.appendText("]");
            }
        }
    }

    public class FakeInterceptor implements Interceptor
    {
        private final static String ORGANISATION_IGNACE = "{\"id\":123456, \"name\":\"Chiro\", \"description\":\"Dit is de chiro van wijnegem\", \"color\":\"#00000000\" }";

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException
        {
            final HttpUrl uri = chain.request().url();
            if(uri.queryParameter("name").equals("Chiro") && uri.queryParameter("description").equals("Dit is de chiro van wijnegem")){
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

