package be.kdg.teamd.beatbuddy.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.kdg.teamd.beatbuddy.BeatBuddyApplication;
import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManager;
import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.organisations.Organisation;
import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import be.kdg.teamd.beatbuddy.model.users.User;
import be.kdg.teamd.beatbuddy.presenter.CreatePlaylistPresenter;
import be.kdg.teamd.beatbuddy.util.ImageEncoder;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatePlaylistActivity extends AppCompatActivity implements CreatePlaylistPresenter.CreatePlaylistPresenterListener {
    @Bind(R.id.create_playlist_toolbar) Toolbar toolbar;
    @Bind(R.id.spinner_playlist_organisation) Spinner spinner_organisations;
    @Bind(R.id.edit_playlist_name) EditText edit_playlistName;
    @Bind(R.id.edit_playlist_key) EditText edit_playlistKey;
    @Bind(R.id.edit_playlist_description) EditText edit_playlistDescription;
    @Bind(R.id.text_playlist_image) TextView text_playlistImage;
    @Bind(R.id.radio_playlist_individual) RadioButton radio_individual;
    @Bind(R.id.radio_playlist_organisation) RadioButton radio_organisation;
    @Bind(R.id.ic_createplaylist_loading) ProgressBar loadingIndicator;

    private final static int PICK_IMAGE = 2;

    private PlaylistRepository playlistRepository;
    private UserRepository userRepository;
    private CreatePlaylistPresenter presenter;
    private UserConfigurationManager userConfigurationManager;

    private List<Organisation> organisations;
    private List<String> organisationNames;
    private ArrayAdapter<String> organisationAdapter;

    private Uri imageUri;

    public void setPlaylistRepository(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
        this.presenter = new CreatePlaylistPresenter(playlistRepository, userRepository, this);
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.presenter = new CreatePlaylistPresenter(playlistRepository, userRepository, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userConfigurationManager = ((BeatBuddyApplication) getApplication()).getUserConfigurationManager();
        if (userConfigurationManager == null)
        {
            //Kan enkel bij testen, maar onCreate komt voor andere methode dus dit is fix
            ((BeatBuddyApplication) getApplication()).initializeUserConfiguration();
            userConfigurationManager = ((BeatBuddyApplication) getApplication()).getUserConfigurationManager();
        }
        playlistRepository = RepositoryFactory.getPlaylistRepository();
        userRepository = RepositoryFactory.getUserRepository();
        presenter = new CreatePlaylistPresenter(playlistRepository, userRepository, this);

        organisations = new ArrayList<>();
        organisationNames = new ArrayList<>();

        organisationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, organisationNames);
        organisationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_organisations.setAdapter(organisationAdapter);

        if(userConfigurationManager.isLoggedIn()){
            presenter.fetchUserOrganisations();
            loadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_playlist_pick_img) public void onBtnPickImageClick(){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            text_playlistImage.setText(imageUri.getLastPathSegment());
        }
    }

    @OnClick(R.id.btn_create_playlist) public void onBtnCreatePlaylistClick(){
        String base64Image = "";
        try{
            if(imageUri != null) base64Image = ImageEncoder.convertToBase64(imageUri, getContentResolver());
        } catch(Exception ex){
            Snackbar.make(edit_playlistName, "Error decoding image", Snackbar.LENGTH_LONG).show();
        }

        if(radio_individual.isChecked()){
            presenter.createPlaylistAsUser(
                    edit_playlistName.getText().toString(),
                    edit_playlistKey.getText().toString(),
                    edit_playlistDescription.getText().toString(),
                    base64Image
            );
        } else {
            long organidationId = -1;
            for(Organisation organisation : organisations){
                if(organisation.getName().equals(spinner_organisations.getSelectedItem().toString())) organidationId = organisation.getId();
            }

            presenter.createPlaylistAsOrganisation(
                    organidationId,
                    edit_playlistName.getText().toString(),
                    edit_playlistKey.getText().toString(),
                    edit_playlistDescription.getText().toString(),
                    base64Image
            );
        }

        loadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReceivedOrganisations(List<Organisation> organisations) {
        this.organisations.clear();
        this.organisations.addAll(organisations);

        this.organisationNames.clear();
        for(Organisation org : organisations){
            this.organisationNames.add(org.getName());
        }
        organisationAdapter.notifyDataSetChanged();

        if(organisations.size() > 0){
            radio_organisation.setEnabled(true);
            spinner_organisations.setClickable(true);
        } else {
            radio_organisation.setEnabled(false);
            spinner_organisations.setClickable(false);
        }

        loadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onPlaylistCreated(Playlist playlist) {
        loadingIndicator.setVisibility(View.GONE);
        Intent intent = new Intent();
        intent.putExtra("playlist", playlist);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onException(String message) {
        loadingIndicator.setVisibility(View.GONE);
        Snackbar.make(edit_playlistName, message, Snackbar.LENGTH_LONG).show();
    }
}
