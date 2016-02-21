package be.kdg.teamd.beatbuddy.dal;

import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserRepository {
    public static final String GRANT_TYPE = "password";

    @POST("token")
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    Call<AccessToken> login(@Field("grant_type") String grantType, @Field("username") String email, @Field("password") String password);

    @POST("users/register")
    Call<User> register(@Query("firstName") String firstName, @Query("lastName") String lastName, @Query("nickname") String nickname, @Query("email") String email, @Query("password") String password);
}
