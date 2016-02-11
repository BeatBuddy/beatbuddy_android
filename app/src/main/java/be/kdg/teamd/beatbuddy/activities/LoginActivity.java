package be.kdg.teamd.beatbuddy.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.users.User;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.btn_login) Button btn_login;
    @Bind(R.id.btn_register) Button btn_register;
    @Bind(R.id.edit_login_email) EditText login_email;
    @Bind(R.id.edit_login_password) EditText login_password;

    UserRepository userRepository;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRepository.login(login_email.getText().toString(), login_password.getText().toString()).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Response<User> response) {
                        if (response.isSuccess()) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Snackbar.make(login_email, "Error logging in: " + response.code(), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Snackbar.make(login_email, "Error logging in: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
