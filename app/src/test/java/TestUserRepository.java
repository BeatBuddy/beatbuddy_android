import com.github.tomakehurst.wiremock.junit.WireMockRule;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.users.User;
import retrofit2.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

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
    private final static String CORRECT_PASSWORD = "maartenpassword";

    private UserRepository userRepository;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Before
    public void setup() {
        wireMockRule.stubFor(
                post(urlPathEqualTo("/api/users/login"))
                        .willReturn(aResponse()
                                .withStatus(403))
        );
        wireMockRule.stubFor(
                post(urlPathEqualTo("/api/users/login"))
                        .withQueryParam("email", equalTo("maarten.vangiel@email.com"))
                        .withQueryParam("password", equalTo(CORRECT_PASSWORD))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(MAARTEN_USER))
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
    public void testLogin() throws IOException {
        Response<User> user = userRepository.login("maarten.vangiel@email.com", CORRECT_PASSWORD)
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
        Response<User> user = userRepository.login("maarten.vangiel@email.com", "asdf")
                .execute();

        Assert.assertEquals(403, user.code());
    }

    @Test
    public void testIncorrectLogin() throws IOException {
        Response<User> user = userRepository.login("asdf@asdf.asdf", "asdf")
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

        userResponse = userRepository.login("maarten.vangiel@email.com", CORRECT_PASSWORD)
                .execute();

        Assert.assertEquals(200, userResponse.code());

        user = userResponse.body();
        Assert.assertEquals("Maarten", user.getFirstName());
        Assert.assertEquals("Van Giel", user.getLastName());
        Assert.assertEquals("Mavamaarten", user.getNickname());
        Assert.assertEquals("maarten.vangiel@email.com", user.getEmail());
        Assert.assertEquals("http://www.google.com", user.getImageUrl());
    }

}

