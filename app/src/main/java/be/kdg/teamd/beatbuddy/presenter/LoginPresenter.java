package be.kdg.teamd.beatbuddy.presenter;

import be.kdg.teamd.beatbuddy.dal.UserRepository;
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
        userRepository.login(email, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                if(response.isSuccess())
                    listener.onLoggedIn(response.body());
                else
                    listener.onException("Login failed. Try again later.");
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
