package be.kdg.teamd.beatbuddy.activities;

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
import be.kdg.teamd.beatbuddy.presenter.RegisterPresenter;
import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements RegisterPresenter.RegisterPresenterListener {
    @Bind(R.id.btn_register) Button btn_register;
    @Bind(R.id.edit_register_email) EditText register_email;
    @Bind(R.id.edit_register_password) EditText register_password;
    @Bind(R.id.edit_register_firstname) EditText register_firstName;
    @Bind(R.id.edit_register_lastname) EditText register_lastName;
    @Bind(R.id.edit_register_nickname) EditText register_nickname;

    private UserRepository userRepository;
    private RegisterPresenter presenter;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.presenter = new RegisterPresenter(this, userRepository);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        userRepository = RepositoryFactory.getUserRepository();
        presenter = new RegisterPresenter(this, userRepository);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            register_email.setText(bundle.getString("email"));
            register_password.setText(bundle.getString("password"));
        }

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.register(
                        register_firstName.getText().toString(),
                        register_lastName.getText().toString(),
                        register_nickname.getText().toString(),
                        register_email.getText().toString(),
                        register_password.getText().toString()
                );
            }
        });
    }

    @Override
    public void onRegistered(User user) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onException(String message) {
        Snackbar.make(register_email, message, Snackbar.LENGTH_LONG).show();
    }
}
