package be.kdg.teamd.beatbuddy.dal;

import java.io.IOException;

import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class RepositoryFactory {
    private static Retrofit.Builder builder;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static UserRepository userRepository;
    private static OrganisationRepository organisationRepository;
    private static PlaylistRepository playlistRepository;

    public static UserRepository getUserRepository() {
        if(userRepository == null) userRepository = createServiceAuthenticated(UserRepository.class, null);
        return userRepository;
    }

    public static OrganisationRepository getOrganisationRepository() {
        if(organisationRepository == null) organisationRepository = createServiceAuthenticated(OrganisationRepository.class, null);
        return organisationRepository;
    }

    public static PlaylistRepository getPlaylistRepository() {
        if(playlistRepository == null) playlistRepository = createServiceAuthenticated(PlaylistRepository.class, null);
        return playlistRepository;
    }

    public static void setAPIEndpoint(String APIEndpoint) {
        if (builder == null) builder = new Retrofit.Builder()
                .baseUrl(APIEndpoint)
                .addConverterFactory(GsonConverterFactory.create());
    }

    public static void setAccessToken(AccessToken token)
    {
        //TODO: refactor, should recreate all current repo's with authenticated versions.
        userRepository = createServiceAuthenticated(UserRepository.class, token);
        organisationRepository = createServiceAuthenticated(OrganisationRepository.class, token);
        playlistRepository = createServiceAuthenticated(PlaylistRepository.class, token);
    }

    private static <S> S createServiceAuthenticated(Class<S> serviceClass, final AccessToken token) {
        if (token != null) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException
                {
                    Request original = chain.request();

                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", token.getTokenType() + " " + token.getAccessToken())
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}
