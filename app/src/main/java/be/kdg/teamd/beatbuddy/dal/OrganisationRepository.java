package be.kdg.teamd.beatbuddy.dal;

import be.kdg.teamd.beatbuddy.model.organisations.Organisation;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Ignace on 13/02/2016.
 */
public interface OrganisationRepository
{
    @POST("organisations")
    Call<Organisation> createOrganisation(@Query("name") String name, @Query("bannerUrl") String bannerUrl, @Query("colorScheme") String colorScheme, @Query("key") String key);
}
