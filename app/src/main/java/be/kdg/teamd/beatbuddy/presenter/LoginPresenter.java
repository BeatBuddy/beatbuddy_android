package be.kdg.teamd.beatbuddy.presenter;

import be.kdg.teamd.beatbuddy.UserConfigurationManager;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.users.AccessToken;
import be.kdg.teamd.beatbuddy.model.users.User;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter {
    private UserRepository userRepository;
    private LoginPresenterListener listener;
    private UserConfigurationManager userConfigurationManager;

    public LoginPresenter(LoginPresenterListener listener, UserRepository userRepository, UserConfigurationManager userConfigurationManager) {
        this.listener = listener;
        this.userRepository = userRepository;
        this.userConfigurationManager = userConfigurationManager;
    }

    public void login(final String email, String password){
        userRepository.login(UserRepository.GRANT_TYPE, email, password).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Response<AccessToken> response) {
                if(response.isSuccess())
                {
                    RepositoryFactory.setAccessToken(response.body());
                    userRepository = RepositoryFactory.getUserRepository();
                    userConfigurationManager.setAccessToken(response.body());

                    userRepository.userInfo(email).enqueue(new Callback<User>()
                    {
                        @Override
                        public void onResponse(Response<User> response)
                        {
                            if (response.isSuccess())
                            {
                                userConfigurationManager.setUser(response.body());
                                listener.onLoggedIn(response.body());
                            }
                            else
                            {
                                listener.onException("No user found with this email");
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            listener.onException("User info fetch failed. " + t.getMessage());
                        }
                    });
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
