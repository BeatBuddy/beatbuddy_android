package be.kdg.teamd.beatbuddy.dal;

import be.kdg.teamd.beatbuddy.model.organisations.Organisation;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Ignace on 13/02/2016.
 */
public interface OrganisationRepository
{
    @POST("organisations")
    @FormUrlEncoded
    Call<Organisation> createOrganisation(@Query("name") String name, @Query("description") String description, @Query("color") String colorScheme, @Field("banner") String bannerBase64);
}
