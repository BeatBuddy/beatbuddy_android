package be.kdg.teamd.beatbuddy.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.adapters.PlaylistTabAdapter;
import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.fragments.ChatFragment;
import be.kdg.teamd.beatbuddy.fragments.QueueFragment;
import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import be.kdg.teamd.beatbuddy.presenter.PlaylistPresenter;
import butterknife.Bind;
import butterknife.ButterKnife;

public class PlaylistActivity extends AppCompatActivity implements PlaylistPresenter.PlaylistPresenterListener, QueueFragment.QueueFragmentListener {
    public static final String EXTRA_PLAYLIST_KEY = "KEY";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tablayout) TabLayout tabLayout;
    @Bind(R.id.playlist_viewpager) ViewPager viewPager;

    private PlaylistRepository playlistRepository;
    private PlaylistPresenter presenter;

    private QueueFragment queueFragment;
    private ChatFragment chatFragment;

    private long playlistId;
    private Playlist playlist;

    public void setPlaylistRepository(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
        this.presenter = new PlaylistPresenter(this, playlistRepository);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        ButterKnife.bind(this);

        playlistRepository = RepositoryFactory.getPlaylistRepository();
        presenter = new PlaylistPresenter(this, playlistRepository);

        String playlistKey = getIntent().getStringExtra(EXTRA_PLAYLIST_KEY);
        playlistId = Long.parseLong(playlistKey);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(playlistKey);

        setupViewPager(viewPager);
        presenter.loadPlaylist(playlistId); // TODO: join by key, not by ID
    }

    private void setupViewPager(final ViewPager viewPager)
    {
        queueFragment = new QueueFragment();
        queueFragment.setListener(this);

        chatFragment = new ChatFragment();

        PlaylistTabAdapter adapter = new PlaylistTabAdapter(getSupportFragmentManager());
        adapter.addFrag(queueFragment, "Queue");
        adapter.addFrag(chatFragment, "Chat");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlaylistLoaded(Playlist playlist) {
        this.playlist = playlist;
        queueFragment.setTracks(playlist.getPlaylistTracks());
        getSupportActionBar().setTitle(playlist.getName());
    }

    @Override
    public void onException(String message) {
        Snackbar.make(viewPager, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onQueueRefreshRequested() {
        presenter.loadPlaylist(playlistId);
    }
}
