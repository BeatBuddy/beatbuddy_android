package be.kdg.teamd.beatbuddy.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.dal.OrganisationRepository;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.model.organisations.Organisation;
import be.kdg.teamd.beatbuddy.presenter.CreateOrganisationPresenter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateOrganisationActivity extends AppCompatActivity implements CreateOrganisationPresenter.CreateOrganisationPresenterListener
{
    @Bind(R.id.create_org_toolbar) Toolbar toolbar;
    @Bind(R.id.create_org_name) EditText name;
    @Bind(R.id.create_org_banner_url) EditText bannerUrl;
    @Bind(R.id.create_org_color_scheme) EditText colorScheme;
    @Bind(R.id.create_org_key) EditText key;

    private OrganisationRepository organisationRepository;
    private CreateOrganisationPresenter presenter;

    public void setOrganisationRepository(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
        this.presenter = new CreateOrganisationPresenter(this, organisationRepository);
    }

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

    @OnClick(R.id.create_org_create) void onCreateOrganisation()
    {
        presenter.createOrganisation(name.getText().toString(),
                                        bannerUrl.getText().toString(),
                                        colorScheme.getText().toString(),
                                        key.getText().toString());
    }

    @Override
    public void onCreated(Organisation organisation)
    {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onException(String message)
    {
        Snackbar.make(name, message, Snackbar.LENGTH_LONG).show();
    }
}
