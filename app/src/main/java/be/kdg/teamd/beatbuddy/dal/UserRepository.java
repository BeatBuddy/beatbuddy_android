package be.kdg.teamd.beatbuddy.dal;

import be.kdg.teamd.beatbuddy.model.users.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserRepository {
    @GET("users/login")
    Call<User> login(@Query("email") String email, @Query("password") String password);
}
