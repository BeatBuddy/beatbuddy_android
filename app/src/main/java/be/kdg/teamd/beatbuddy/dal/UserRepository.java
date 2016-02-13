package be.kdg.teamd.beatbuddy.dal;

import be.kdg.teamd.beatbuddy.model.users.User;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserRepository {
    @POST("users/login")
    Call<User> login(@Query("email") String email, @Query("password") String password);

    @POST("users/register")
    Call<User> register(@Query("firstName") String firstName, @Query("lastName") String lastName, @Query("nickname") String nickname, @Query("email") String email, @Query("password") String password);
}
