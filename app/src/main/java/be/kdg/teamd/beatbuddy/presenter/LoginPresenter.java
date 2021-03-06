package be.kdg.teamd.beatbuddy.presenter;

import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManager;
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

    public void loginGplus(String firstName, String lastName, String nickName, final String email, final String password, final boolean rememberMe, String imageUrl) {
        userRepository.registerGplus(firstName, lastName, nickName, email, password, imageUrl).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                if(!response.isSuccess()){
                    listener.onException("Failed to register using Google plus");
                    return;
                }

                userRepository.login(UserRepository.GRANT_TYPE, email, password).enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Response<AccessToken> response) {
                        if(!response.isSuccess()){
                            listener.onException("Failed to log in using Google plus");
                            return;
                        }

                        final AccessToken accessToken = response.body();
                        RepositoryFactory.setAccessToken(accessToken);
                        userRepository = RepositoryFactory.getUserRepository();

                        userRepository.userInfo(email).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Response<User> response) {
                                if (response.isSuccess()) {
                                    userConfigurationManager.login(accessToken, response.body(), rememberMe);
                                    listener.onLoggedIn(response.body());
                                } else {
                                    listener.onException("No user found with this email");
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                listener.onException("User info fetch failed. " + t.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        listener.onException("Invalid username or password");
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onException("Failed to register using Google plus");
            }
        });
    }

    public void login(final String email, String password, final boolean rememberMe) {
        userRepository.login(UserRepository.GRANT_TYPE, email, password).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Response<AccessToken> response) {
                if (response.isSuccess()) {
                    final AccessToken accessToken = response.body();
                    RepositoryFactory.setAccessToken(accessToken);
                    userRepository = RepositoryFactory.getUserRepository();

                    userRepository.userInfo(email).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Response<User> response) {
                            if (response.isSuccess()) {
                                userConfigurationManager.login(accessToken, response.body(), rememberMe);
                                listener.onLoggedIn(response.body());
                            } else {
                                listener.onException("No user found with this email");
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            listener.onException("User info fetch failed. " + t.getMessage());
                        }
                    });
                } else {
                    listener.onException("Invalid username or password");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onException("Login failed. " + t.getMessage());
            }
        });
    }

    public interface LoginPresenterListener {
        void onLoggedIn(User user);

        void onException(String message);
    }
}
