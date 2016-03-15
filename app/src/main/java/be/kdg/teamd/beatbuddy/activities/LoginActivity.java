package be.kdg.teamd.beatbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import be.kdg.teamd.beatbuddy.BeatBuddyApplication;
import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.users.User;
import be.kdg.teamd.beatbuddy.presenter.LoginPresenter;
import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginPresenter.LoginPresenterListener, GoogleApiClient.OnConnectionFailedListener {
    @Bind(R.id.edit_login_email) EditText login_email;
    @Bind(R.id.edit_login_password) EditText login_password;
    @Bind(R.id.ic_login_loading) ProgressBar progress_loading;
    @Bind(R.id.login_remember) CheckBox loginRemember;

    private final static int REQ_REGISTER = 4;
    private final static int RC_SIGN_IN = 5;

    private UserRepository userRepository;
    private LoginPresenter presenter;
    private UserConfigurationManager userConfigurationManager;
    private GoogleApiClient mGoogleApiClient;

    public void setTestImplementation(UserRepository userRepository, UserConfigurationManager userConfigurationManager) {
        this.userRepository = userRepository;
        this.presenter = new LoginPresenter(this, userRepository, userConfigurationManager);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
                break;

            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                break;
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String firstName = acct.getDisplayName().substring(0, acct.getDisplayName().indexOf(" "));
            String lastName = acct.getDisplayName().substring(acct.getDisplayName().indexOf(" ") + 1);
            String nickname = acct.getEmail().split("@")[0];

            System.out.println("Google ID: " + acct.getId());
            presenter.loginGplus(firstName, lastName, nickname, acct.getEmail(), "Gplus" + acct.getId(), loginRemember.isChecked(), acct.getPhotoUrl().toString());
            progress_loading.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(login_email.getContext(),"Google login failed", Toast.LENGTH_LONG).show();
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @OnClick(R.id.gplus_button)
    public void onGoogleplusButtonClick(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
