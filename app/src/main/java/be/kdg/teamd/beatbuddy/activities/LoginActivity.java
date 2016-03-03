package be.kdg.teamd.beatbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import be.kdg.teamd.beatbuddy.BeatBuddyApplication;
import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManager;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.users.User;
import be.kdg.teamd.beatbuddy.presenter.LoginPresenter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginPresenter.LoginPresenterListener{
    @Bind(R.id.edit_login_email) EditText login_email;
    @Bind(R.id.edit_login_password) EditText login_password;
    @Bind(R.id.ic_login_loading) ProgressBar progress_loading;
    @Bind(R.id.login_remember) CheckBox loginRemember;

    private final static int REQ_REGISTER = 4;

    private UserRepository userRepository;
    private LoginPresenter presenter;
    private UserConfigurationManager userConfigurationManager;

    public void setTestImplementation(UserRepository userRepository, UserConfigurationManager userConfigurationManager) {
        this.userRepository = userRepository;
        this.presenter = new LoginPresenter(this, userRepository, userConfigurationManager);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        userConfigurationManager = ((BeatBuddyApplication) getApplication()).getUserConfigurationManager();
        userRepository = RepositoryFactory.getUserRepository();
        presenter = new LoginPresenter(this, userRepository, userConfigurationManager);
    }

    @OnClick(R.id.btn_login) public void onLoginClicked(){
        presenter.login(login_email.getText().toString(), login_password.getText().toString(), loginRemember.isChecked());
        progress_loading.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_register) public void onRegisterClicked(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.putExtra("email", login_email.getText().toString());
        intent.putExtra("password", login_password.getText().toString());
        startActivityForResult(intent, REQ_REGISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) return;

        switch(requestCode){
            case REQ_REGISTER:
                User user = (User) data.getSerializableExtra("user");
                String password = data.getStringExtra("password");
                login_email.setText(user.getEmail());
                login_password.setText(password);
                onLoginClicked();
        }
    }

    @Override
    public void onLoggedIn(User user) {
        setResult(RESULT_OK);
        progress_loading.setVisibility(View.GONE);
        finish();
    }

    @Override
    public void onException(String message) {
        progress_loading.setVisibility(View.GONE);
        Snackbar.make(login_email, message, Snackbar.LENGTH_LONG).show();
    }
}
