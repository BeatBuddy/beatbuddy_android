import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.annotations.SerializedName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;
import retrofit2.Response;
import wiremock.org.skyscreamer.jsonassert.JSONCompareMode;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.mockito.AdditionalMatchers.not;

@RunWith(JUnit4.class)
public class TestUserRepository {
    private final static String MAARTEN_USER = "{\n" +
            "\t\"id\":123456,\n" +
            "\t\"email\":\"maarten.vangiel@email.com\",\n" +
            "\t\"firstName\":\"Maarten\",\n" +
            "\t\"lastName\":\"Van Giel\",\n" +
            "\t\"nickname\":\"Mavamaarten\",\n" +
            "\t\"imageUrl\":\"http://www.google.com\"\n" +
            "}";
    private final static String CORRENT_USERNAME = "maarten.vangiel@email.com";
    private final static String CORRECT_PASSWORD = "maartenpassword";
    private static final String GRANT_TYPE = "password";

    private UserRepository userRepository;

    private User maartenTest;
    private AccessToken accessTokenTest;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Before
    public void setup() {
        // Create test objects
        accessTokenTest = new AccessToken();
        accessTokenTest.setAccessToken("azerty123");
        accessTokenTest.setExpiresIn("65595154");
        accessTokenTest.setTokenType("Bearer");

        maartenTest = new User();
        maartenTest.setId(123456);
        maartenTest.setNickname("Mavamaarten");
        maartenTest.setFirstName("Maarten");
        maartenTest.setLastName("Van Giel");
        maartenTest.setImageUrl("http://www.google.com");
        maartenTest.setEmail(CORRENT_USERNAME);

        // Token = login
        wireMockRule.stubFor(
                post(urlPathEqualTo("/api/token"))
                        .withRequestBody(equalToJson(Json.write(new AccessTokenRequest(GRANT_TYPE, CORRENT_USERNAME, CORRECT_PASSWORD))))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(Json.write(accessTokenTest)))
        );
        wireMockRule.stubFor(
                post(urlPathEqualTo("/api/token"))
                        .willReturn(aResponse()
                                .withStatus(403)
                                .withBody(Json.write(accessTokenTest)))
        );
        // User Info
        wireMockRule.stubFor(
                get(urlPathEqualTo("/api/users/maarten.vangiel@email.com/"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(Json.write(maartenTest)))
        );
        wireMockRule.stubFor(
                post(urlPathEqualTo("/api/users/register"))
                        .withQueryParam("firstName", equalTo("Maarten"))
                        .withQueryParam("lastName", equalTo("Van%20Giel"))
                        .withQueryParam("nickname", equalTo("Mavamaarten"))
                        .withQueryParam("email", equalTo("maarten.vangiel@email.com"))
                        .withQueryParam("password", equalTo(CORRECT_PASSWORD))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(MAARTEN_USER))
        );


        RepositoryFactory.setAPIEndpoint("http://localhost:8080/api/");
        userRepository = RepositoryFactory.getUserRepository();
    }

    @Test
    public void testGetUserInfo() throws IOException {
        Response<User> user = userRepository.userInfo("maarten.vangiel@email.com")
                .execute();

        Assert.assertEquals(200, user.code());
        Assert.assertEquals("Maarten", user.body().getFirstName());
        Assert.assertEquals("Van Giel", user.body().getLastName());
        Assert.assertEquals("Mavamaarten", user.body().getNickname());
        Assert.assertEquals("maarten.vangiel@email.com", user.body().getEmail());
        Assert.assertEquals("http://www.google.com", user.body().getImageUrl());
    }

    @Test
    public void testIncorrectPassword() throws IOException {
        Response<AccessToken> user = userRepository.login(GRANT_TYPE, "maarten.vangiel@email.com", "asdf")
                .execute();

        Assert.assertEquals(403, user.code());
    }

    @Test
    public void testIncorrectLogin() throws IOException {
        Response<AccessToken> user = userRepository.login(GRANT_TYPE, "zefezfzef", "asdf")
                .execute();

        Assert.assertEquals(403, user.code());
    }

    @Test
    public void testRegister() throws IOException {
        Response<User> userResponse = userRepository.register("Maarten", "Van Giel", "Mavamaarten", "maarten.vangiel@email.com", CORRECT_PASSWORD)
                .execute();

        Assert.assertEquals(200, userResponse.code());

        User user = userResponse.body();
        Assert.assertEquals("Maarten", user.getFirstName());
        Assert.assertEquals("Van Giel", user.getLastName());
        Assert.assertEquals("Mavamaarten", user.getNickname());
        Assert.assertEquals("maarten.vangiel@email.com", user.getEmail());

        Response<AccessToken> loginResponse = userRepository.login(GRANT_TYPE, "maarten.vangiel@email.com", CORRECT_PASSWORD)
                .execute();

        Assert.assertEquals(200, userResponse.code());

        user = userResponse.body();
        Assert.assertEquals("Maarten", user.getFirstName());
        Assert.assertEquals("Van Giel", user.getLastName());
        Assert.assertEquals("Mavamaarten", user.getNickname());
        Assert.assertEquals("maarten.vangiel@email.com", user.getEmail());
        Assert.assertEquals("http://www.google.com", user.getImageUrl());
    }

    private class AccessTokenRequest
    {
        @SerializedName("grant_type")
        private String grantType;
        private String username;
        private String password;

        public AccessTokenRequest(String grantType, String username, String password)
        {
            this.grantType = grantType;
            this.username = username;
            this.password = password;
        }

        public String getGrantType()
        {
            return grantType;
        }

        public void setGrantType(String grantType)
        {
            this.grantType = grantType;
        }

        public String getUsername()
        {
            return username;
        }

        public void setUsername(String username)
        {
            this.username = username;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }
    }
}

