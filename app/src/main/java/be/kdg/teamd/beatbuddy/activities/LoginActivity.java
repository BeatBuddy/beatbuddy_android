package be.kdg.teamd.beatbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.users.User;
import be.kdg.teamd.beatbuddy.presenter.LoginPresenter;
import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements LoginPresenter.LoginPresenterListener{
    @Bind(R.id.btn_login) Button btn_login;
    @Bind(R.id.btn_register) Button btn_register;
    @Bind(R.id.edit_login_email) EditText login_email;
    @Bind(R.id.edit_login_password) EditText login_password;

    UserRepository userRepository;
    LoginPresenter presenter;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.presenter = new LoginPresenter(this, userRepository);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        userRepository = RepositoryFactory.getUserRepository();
        presenter = new LoginPresenter(this, userRepository);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.login(login_email.getText().toString(), login_password.getText().toString());
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("email", login_email.getText().toString());
                intent.putExtra("password", login_password.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onLoggedIn(User user) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onException(String message) {
        Snackbar.make(login_email, message, Snackbar.LENGTH_LONG).show();
    }
}
