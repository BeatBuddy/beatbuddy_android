package be.kdg.teamd.beatbuddy.presenter;

import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.users.User;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPresenter {
    private UserRepository userRepository;
    private RegisterPresenterListener listener;

    public RegisterPresenter(RegisterPresenterListener listener, UserRepository userRepository) {
        this.listener = listener;
        this.userRepository = userRepository;
    }

    public void register(String firstName, String lastName, String nickname, String email, String password){
        userRepository.register(firstName, lastName, nickname, email, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                if(response.isSuccess())
                    listener.onRegistered(response.body());
                else
                    listener.onException("Register failed. Make sure all information is valid.");
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onException("Register failed. " + t.getMessage());
            }
        });
    }

    public interface RegisterPresenterListener{
        void onRegistered(User user);
        void onException(String message);
    }
}
