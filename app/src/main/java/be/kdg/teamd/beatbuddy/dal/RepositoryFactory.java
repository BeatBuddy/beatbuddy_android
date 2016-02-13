package be.kdg.teamd.beatbuddy.dal;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class RepositoryFactory {
    private static Retrofit retrofit;
    private static UserRepository userRepository;
    private static OrganisationRepository organisationRepository;

    public static UserRepository getUserRepository() {
        if(userRepository == null) userRepository = retrofit.create(UserRepository.class);
        return userRepository;
    }

    public static OrganisationRepository getOrganisationRepository() {
        if(organisationRepository == null) organisationRepository = retrofit.create(OrganisationRepository.class);
        return organisationRepository;
    }

    public static void setAPIEndpoint(String APIEndpoint) {
        if(retrofit == null) retrofit = new Retrofit.Builder()
                .baseUrl(APIEndpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
