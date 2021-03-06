package be.kdg.teamd.beatbuddy.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.dal.OrganisationRepository;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.model.organisations.Organisation;
import be.kdg.teamd.beatbuddy.presenter.CreateOrganisationPresenter;
import be.kdg.teamd.beatbuddy.util.ImageEncoder;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateOrganisationActivity extends AppCompatActivity implements CreateOrganisationPresenter.CreateOrganisationPresenterListener
{
    public static final int BANNER_REQUEST = 1;

    @Bind(R.id.create_org_toolbar) Toolbar toolbar;
    @Bind(R.id.create_org_name) EditText name;
    @Bind(R.id.create_org_description) EditText description;
    @Bind(R.id.create_org_banner) ImageView banner;
    @Bind(R.id.ic_createorg_loading) ProgressBar loadingProgressbar;

    private OrganisationRepository organisationRepository;
    private CreateOrganisationPresenter presenter;

    private Uri bannerUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_organisation);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        organisationRepository = RepositoryFactory.getOrganisationRepository();
        presenter = new CreateOrganisationPresenter(this, organisationRepository);
    }

    public void setOrganisationRepository(OrganisationRepository organisationRepository)
    {
        this.organisationRepository = organisationRepository;
        this.presenter = new CreateOrganisationPresenter(this, organisationRepository);
    }

    @OnClick(R.id.create_org_create) void onCreateOrganisation()
    {
        if(TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(description.getText())){
            Snackbar.make(toolbar, "Please fill in all fields.", Snackbar.LENGTH_LONG).show();
            return;
        }

        String bannerInBase64;
        try
        {
            bannerInBase64 = ImageEncoder.convertToBase64(bannerUri, getContentResolver());

        } catch (ImageEncoder.EncodingException e)
        {
            bannerInBase64 = null;
        }

        presenter.createOrganisation(name.getText().toString(),
                description.getText().toString(),
                Integer.toHexString(0),
                bannerInBase64);

        loadingProgressbar.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.create_org_banner_add) void onAddBannerImage()
    {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, BANNER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BANNER_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            bannerUri = data.getData();
            setBannerFromUri(bannerUri);
        }
    }

    private void setBannerFromUri(Uri uri)
    {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            banner.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreated(Organisation organisation)
    {
        loadingProgressbar.setVisibility(View.GONE);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onException(String message)
    {
        loadingProgressbar.setVisibility(View.GONE);
        Snackbar.make(name, message, Snackbar.LENGTH_LONG).show();
    }
}
