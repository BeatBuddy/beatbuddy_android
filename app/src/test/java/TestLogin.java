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
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@RunWith(JUnit4.class)
public class TestLogin {
    private UserRepository userRepository;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Before
    public void setup() {
        wireMockRule.stubFor(
                get(urlPathEqualTo("/api/users/login"))
                        .willReturn(aResponse()
                                .withStatus(403))
        );
        wireMockRule.stubFor(
                get(urlPathEqualTo("/api/users/login"))
                        .withQueryParam("username", equalTo("maarten"))
                        .withQueryParam("password", equalTo("maartenpassword"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("{\n" +
                                        "\t\"id\":123456,\n" +
                                        "\t\"email\":\"maarten.vangiel@hotmail.com\",\n" +
                                        "\t\"firstName\":\"Maarten\",\n" +
                                        "\t\"lastName\":\"Van Giel\",\n" +
                                        "\t\"nickname\":\"Mavamaarten\",\n" +
                                        "\t\"imageUrl\":\"http://www.google.com\"\n" +
                                        "}"))
        );


        RepositoryFactory.setAPIEndpoint("http://localhost:8080/api/");
        userRepository = RepositoryFactory.getUserRepository();
    }

    @Test
    public void testLogin() throws IOException {
        Response<User> user = userRepository.login("maarten", "maartenpassword")
                .execute();

        Assert.assertEquals(200, user.code());
        Assert.assertEquals("Maarten", user.body().getFirstName());
        Assert.assertEquals("Van Giel", user.body().getLastName());
    }

    @Test
    public void testIncorrectPassword() throws IOException {
        Response<User> user = userRepository.login("maarten", "asdf")
                .execute();

        Assert.assertEquals(403, user.code());
    }

    @Test
    public void testIncorrectLogin() throws IOException {
        Response<User> user = userRepository.login("asdf", "asdf")
                .execute();

        Assert.assertEquals(403, user.code());
    }

}

