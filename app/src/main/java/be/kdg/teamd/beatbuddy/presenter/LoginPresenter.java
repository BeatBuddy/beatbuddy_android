package be.kdg.teamd.beatbuddy.presenter;

import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter {
    private UserRepository userRepository;
    private LoginPresenterListener listener;

    public LoginPresenter(LoginPresenterListener listener, UserRepository userRepository) {
        this.listener = listener;
        this.userRepository = userRepository;
    }

    public void login(String email, String password){
        userRepository.login(UserRepository.GRANT_TYPE, email, password).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Response<AccessToken> response) {
                if(response.isSuccess())
                {
                    RepositoryFactory.setAccessToken(response.body());
                    listener.onLoggedIn(new User());// TODO: user info opvragen
                }
                else
                {
                    listener.onException("Invalid username or password");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onException("Login failed. " + t.getMessage());
            }
        });
    }

    public interface LoginPresenterListener{
        void onLoggedIn(User user);
        void onException(String message);
    }
}
