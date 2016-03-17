package be.kdg.teamd.beatbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.adapters.PlaylistAdapter;
import be.kdg.teamd.beatbuddy.dal.OrganisationRepository;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.model.organisations.Organisation;
import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import be.kdg.teamd.beatbuddy.presenter.OrganisationPresenter;
import be.kdg.teamd.beatbuddy.util.ImageEncoder;
import be.kdg.teamd.beatbuddy.util.SizeUtil;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrganisationActivity extends AppCompatActivity implements OrganisationPresenter.OrganisationPresenterListener, PlaylistAdapter.PlaylistClickedListener, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.list_org_playlists) RecyclerView recyclerView;
    @Bind(R.id.swiperefresh_org) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.organisation_banner) ImageView organisationBanner;
    @Bind(R.id.text_org_no_playlists) TextView textNoPlaylists;
    @Bind(R.id.loading_playlists) ProgressBar loading_playlists;

    private Organisation organisation;
    private List<Playlist> playlists;
    private PlaylistAdapter playlistAdapter;

    private OrganisationRepository organisationRepository;
    private OrganisationPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organisation);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Organisation");

        organisationRepository = RepositoryFactory.getOrganisationRepository();
        presenter = new OrganisationPresenter(this, organisationRepository);
        playlists = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(this, playlists, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(playlistAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        Bundle bundle = getIntent().getExtras();
        if(bundle.containsKey("Organisation")){
            this.organisation = (Organisation) bundle.getSerializable("Organisation");
            presenter.loadOrganisation(organisation.getId());
            showOrganisation();
        } else {
            long organisationId = bundle.getLong("OrganisationId");
            presenter.loadOrganisation(organisationId);
        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
    }

    private void showOrganisation(){
        setTitle(organisation.getName());

        if(!TextUtils.isEmpty(organisation.getBannerUrl()))
            Picasso.with(this)
                    .load(getString(R.string.organisationImageLocation) + ImageEncoder.encodeImageUrl(organisation.getBannerUrl()))
                    .fit()
                    .centerCrop()
                    .into(organisationBanner);
    }

    @Override
    public void onOrganisationLoaded(Organisation organisation) {
        this.organisation = organisation;
        this.playlists.clear();
        showOrganisation();
        if(organisation.getPlaylists() == null || organisation.getPlaylists().size() == 0)
            textNoPlaylists.setVisibility(View.VISIBLE);
        else{
            textNoPlaylists.setVisibility(View.GONE);
            int viewHeight = (int) (SizeUtil.convertDpToPixel(172, this));
            viewHeight = viewHeight * ((playlists.size() + 1) / 2);
            recyclerView.getLayoutParams().height = viewHeight;
            this.playlists.addAll(organisation.getPlaylists());
            playlistAdapter.notifyDataSetChanged();
        }

        swipeRefreshLayout.setRefreshing(false);
        loading_playlists.setVisibility(View.GONE);
    }

    @Override
    public void onException(String message) {
        Snackbar.make(swipeRefreshLayout, message, Snackbar.LENGTH_LONG).show();
        swipeRefreshLayout.setRefreshing(false);
        loading_playlists.setVisibility(View.GONE);
    }

    @Override
    public void onPlaylistClicked(Playlist playlist) {
        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra(PlaylistActivity.EXTRA_PLAYLIST_KEY, playlist.getId() + "");
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        Bundle bundle = getIntent().getExtras();
        if(bundle.containsKey("Organisation")){
            this.organisation = (Organisation) bundle.getSerializable("Organisation");
            presenter.loadOrganisation(organisation.getId());
            showOrganisation();
        } else {
            long organisationId = bundle.getLong("OrganisationId");
            presenter.loadOrganisation(organisationId);
        }
        swipeRefreshLayout.setRefreshing(true);
        loading_playlists.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.main_fab_create_playlist) void onClickCreatePlaylist()
    {
        startActivity(new Intent(this, CreatePlaylistActivity.class));
    }
}
