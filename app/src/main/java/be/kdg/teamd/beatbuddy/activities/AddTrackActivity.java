package be.kdg.teamd.beatbuddy.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.adapters.SearchTrackAdapter;
import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.presenter.AddTrackPresenter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class AddTrackActivity extends AppCompatActivity implements AddTrackPresenter.AddTrackListener, SearchTrackAdapter.SearchTrackListener
{
    public static final String EXTRA_PLAYLIST_ID = "playlist_id";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.search_track_query) EditText searchQuery;
    @Bind(R.id.search_track_results) RecyclerView searchResults;

    private AddTrackPresenter presenter;
    private PlaylistRepository repository;
    private SearchTrackAdapter adapter;

    private long playlistId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        playlistId = getIntent().getLongExtra(EXTRA_PLAYLIST_ID, 0);

        repository = RepositoryFactory.getPlaylistRepository();
        presenter = new AddTrackPresenter(this, repository);

        setupRecyclerView();
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

    public void setPlaylistRepository(PlaylistRepository playlistRepository)
    {
        this.repository = playlistRepository;
        this.presenter = new AddTrackPresenter(this, playlistRepository);
    }

    private void setupRecyclerView()
    {
        adapter = new SearchTrackAdapter(new LinkedList<Track>(), this);
        searchResults.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        searchResults.setLayoutManager(manager);
    }

    @Override
    public void onClickTrack(Track track)
    {
        presenter.addTrack(playlistId, track.getTrackSource().getTrackId());
    }

    @OnTextChanged(R.id.search_track_query) void onTypedQuery(CharSequence text)
    {
        if (!text.toString().isEmpty())
            presenter.searchTrack(text.toString());
    }

    @OnClick(R.id.search_track_fab) void onSearchTrackClick()
    {
        presenter.searchTrack(searchQuery.getText().toString());

        //Close keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onSearchResult(List<Track> tracks)
    {
        adapter.updateTracks(tracks);
    }

    @Override
    public void onTrackAdded(Track track)
    {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onException(String message)
    {
        Snackbar.make(searchResults, message, Snackbar.LENGTH_LONG).show();
    }
}
