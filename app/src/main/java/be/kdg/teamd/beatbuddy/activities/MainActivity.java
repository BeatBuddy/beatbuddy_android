package be.kdg.teamd.beatbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import be.kdg.teamd.beatbuddy.BeatBuddyApplication;
import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.adapters.OrganisationLinearLayoutAdapter;
import be.kdg.teamd.beatbuddy.adapters.PlaylistAdapter;
import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.organisations.Organisation;
import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import be.kdg.teamd.beatbuddy.model.users.User;
import be.kdg.teamd.beatbuddy.presenter.MainPresenter;
import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManager;
import be.kdg.teamd.beatbuddy.util.SizeUtil;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainPresenter.MainPresenterListener, OrganisationLinearLayoutAdapter.OrganisationClickListener, PlaylistAdapter.PlaylistClickedListener, SwipeRefreshLayout.OnRefreshListener {

    public static final int RESULT_LOGIN = 1;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.nav_view) NavigationView navigationView;
    @Bind(R.id.drawer_layout) DrawerLayout drawer;
    @Bind(R.id.main_fab_create_playlist) FloatingActionButton fab_create_playlist;
    @Bind(R.id.main_fab_create_organisation) FloatingActionButton fab_create_organisation;
    @Bind(R.id.list_main_yourorganisations) LinearLayout list_yourorganisations;
    @Bind(R.id.list_main_yourplaylists) RecyclerView list_yourplaylists;
    @Bind(R.id.list_main_recommendedplaylists) RecyclerView list_recommendedplaylists;
    @Bind(R.id.text_main_notloggedin1) TextView textNotLoggedInPlaylists1;
    @Bind(R.id.text_main_notloggedin2) TextView textNotLoggedInPlaylists2;
    @Bind(R.id.loading_yourplaylists) ProgressBar loadingYourPlaylists;
    @Bind(R.id.loading_yourorganisations) ProgressBar loadingYourOrganisations;
    @Bind(R.id.loading_recommendations) ProgressBar loadingRecommendations;
    @Bind(R.id.swiperefresh_main) SwipeRefreshLayout swipeRefreshLayout;

    private PlaylistRepository playlistRepository;
    private UserRepository userRepository;
    private MainPresenter presenter;
    private UserConfigurationManager userConfigurationManager;

    private List<Playlist> userPlaylists;
    private List<Playlist> recommendedPlaylists;
    private List<Organisation> userOrganisations;

    private OrganisationLinearLayoutAdapter organisationAdapter;
    private PlaylistAdapter yourPlaylistAdapter;
    private PlaylistAdapter recommendedPlaylistAdapter;

    public void setPlaylistRepository(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
        this.presenter = new MainPresenter(this, userRepository, playlistRepository);
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.presenter = new MainPresenter(this, userRepository, playlistRepository);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle("BeatBuddy");

        ((BeatBuddyApplication) getApplication()).initializeUserConfiguration();
        userConfigurationManager = ((BeatBuddyApplication) getApplication()).getUserConfigurationManager();
        playlistRepository = RepositoryFactory.getPlaylistRepository();
        userRepository = RepositoryFactory.getUserRepository();
        presenter = new MainPresenter(this, userRepository, playlistRepository);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        User user = userConfigurationManager.getUser();
        if(user != null){
            bindUserToNavigationView(user);
            changeVisibleStates(true);
            loadUserPlaylistsAndOrganisations();
        } else {
            presenter.loadRecommendedPlaylists(6);
            loadingRecommendations.setVisibility(View.VISIBLE);
        }

        userPlaylists = new ArrayList<>();
        recommendedPlaylists = new ArrayList<>();
        userOrganisations = new ArrayList<>();

        organisationAdapter = new OrganisationLinearLayoutAdapter(list_yourorganisations, userOrganisations, this, this);

        yourPlaylistAdapter = new PlaylistAdapter(this, userPlaylists, this);
        list_yourplaylists.setHasFixedSize(true);
        list_yourplaylists.setLayoutManager(new GridLayoutManager(this, 2));
        list_yourplaylists.setAdapter(yourPlaylistAdapter);
        list_yourplaylists.setNestedScrollingEnabled(false);

        recommendedPlaylistAdapter = new PlaylistAdapter(this, recommendedPlaylists, this);
        list_recommendedplaylists.setHasFixedSize(true);
        list_recommendedplaylists.setLayoutManager(new GridLayoutManager(this, 2));
        list_recommendedplaylists.setAdapter(recommendedPlaylistAdapter);
        list_recommendedplaylists.setNestedScrollingEnabled(false);

        navigationView.setNavigationItemSelectedListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RESULT_LOGIN && resultCode == RESULT_OK) // Login successful
        {
            User user = userConfigurationManager.getUser();
            bindUserToNavigationView(user);
            changeVisibleStates(true);

            presenter.setUserRepository(RepositoryFactory.getUserRepository());
            presenter.setPlaylistRepository(RepositoryFactory.getPlaylistRepository());

            loadUserPlaylistsAndOrganisations();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadUserPlaylistsAndOrganisations(){
        presenter.loadUserPlaylists();
        presenter.loadUserOrganisations();
        presenter.loadRecommendedPlaylists(6);

        loadingYourPlaylists.setVisibility(View.VISIBLE);
        loadingYourOrganisations.setVisibility(View.VISIBLE);
        loadingRecommendations.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.nav_login:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, RESULT_LOGIN);
                break;

            case R.id.nav_logout:
                userConfigurationManager.logout();
                RepositoryFactory.setAccessToken(userConfigurationManager.getAccessToken());
                User user = userConfigurationManager.getUser();
                bindUserToNavigationView(user);
                changeVisibleStates(false);
                clearUserPlaylistsAndOrganisations();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearUserPlaylistsAndOrganisations() {
        userOrganisations.clear();
        userPlaylists.clear();

        yourPlaylistAdapter.notifyDataSetChanged();
        organisationAdapter.notifyDataSetChanged();

        list_yourplaylists.getLayoutParams().height = 0;
        list_yourorganisations.getLayoutParams().height = 0;

        loadingRecommendations.setVisibility(View.GONE);
        loadingYourPlaylists.setVisibility(View.GONE);
        loadingYourOrganisations.setVisibility(View.GONE);
    }

    @OnClick(R.id.main_fab_create_playlist) void onClickCreatePlaylist()
    {
        startActivity(new Intent(this, CreatePlaylistActivity.class));
    }

    @OnClick(R.id.main_fab_join_playlist) void onClickJoinPlaylist()
    {
        new MaterialDialog.Builder(this)
            .title("Join an organisation")
            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
            .input("Key", null, new MaterialDialog.InputCallback() {
                @Override
                public void onInput(MaterialDialog dialog, CharSequence input) {
                    Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
                    intent.putExtra(PlaylistActivity.EXTRA_PLAYLIST_KEY, input.toString());
                    startActivity(intent);
                }
            }).show();
    }

    @OnClick(R.id.main_fab_create_organisation) public void onCreateOrganisationClick(){
        startActivity(new Intent(this, CreateOrganisationActivity.class));
    }

    private void changeVisibleStates(boolean loggedIn)
    {
        navigationView.getMenu().setGroupVisible(R.id.group_guest, !loggedIn);
        navigationView.getMenu().setGroupVisible(R.id.group_logged_in, loggedIn);
        fab_create_playlist.setEnabled(loggedIn);
        fab_create_organisation.setEnabled(loggedIn);

        textNotLoggedInPlaylists1.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        textNotLoggedInPlaylists2.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
    }

    private void bindUserToNavigationView(User user)
    {
        View headerView = navigationView.getHeaderView(0);
        CircleImageView userAvatar = ButterKnife.findById(headerView, R.id.header_profile_image);
        TextView username = ButterKnife.findById(headerView, R.id.header_name);
        TextView subname = ButterKnife.findById(headerView, R.id.header_subname);

        if (user == null)
        {
            userAvatar.setImageResource(R.mipmap.ic_launcher);
            username.setText(R.string.guest);
            subname.setText(R.string.not_logged_in);
        }
        else
        {
            if(!TextUtils.isEmpty(user.getImageUrl()))
                Picasso.with(this)
                        .load(user.getImageUrl())
                        .into(userAvatar);

            username.setText(user.getNickname());
            subname.setText(user.getFirstName() + " " + user.getLastName());
        }
    }

    @Override
    public void onRecommendedPlaylistsLoaded(List<Playlist> playlists) {
        this.recommendedPlaylists.clear();
        this.recommendedPlaylists.addAll(playlists);
        recommendedPlaylistAdapter.notifyDataSetChanged();

        int viewHeight = (int) (SizeUtil.convertDpToPixel(172, this));
        viewHeight = viewHeight * (((recommendedPlaylists.size() - (recommendedPlaylists.size() % 2)) / 2) + 1);
        list_recommendedplaylists.getLayoutParams().height = viewHeight;
        loadingRecommendations.setVisibility(View.GONE);
    }

    @Override
    public void onUserPlaylistsLoaded(List<Playlist> playlists) {
        this.userPlaylists.clear();
        this.userPlaylists.addAll(playlists);
        yourPlaylistAdapter.notifyDataSetChanged();

        int viewHeight = (int) (SizeUtil.convertDpToPixel(172, this));
        viewHeight = viewHeight * (((userPlaylists.size() - (userPlaylists.size() % 2)) / 2) + 1);
        list_yourplaylists.getLayoutParams().height = viewHeight;
        loadingYourPlaylists.setVisibility(View.GONE);
    }

    @Override
    public void onUserOrganisationsLoaded(List<Organisation> organisations) {
        this.userOrganisations.clear();
        this.userOrganisations.addAll(organisations);
        organisationAdapter.notifyDataSetChanged();
        loadingYourOrganisations.setVisibility(View.GONE);
    }

    @Override
    public void onRecommendedPlaylistsException(String message) {
        Snackbar.make(drawer, message, Snackbar.LENGTH_LONG).show();
        loadingYourPlaylists.setVisibility(View.GONE);
    }

    @Override
    public void onUserPlaylistsException(String message) {
        Snackbar.make(drawer, message, Snackbar.LENGTH_LONG).show();
        loadingYourPlaylists.setVisibility(View.GONE);
    }

    @Override
    public void onUserOrganisationsException(String message) {
        Snackbar.make(drawer, message, Snackbar.LENGTH_LONG).show();
        loadingYourOrganisations.setVisibility(View.GONE);
    }


    @Override
    public void onOrganisationClicked(Organisation organisation) {
        Snackbar.make(drawer, organisation.getName() + " clicked!" , Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPlaylistClicked(Playlist playlist) {
        Snackbar.make(drawer, playlist.getName() + " clicked!" , Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if(userConfigurationManager.isLoggedIn()) loadUserPlaylistsAndOrganisations();
    }
}
