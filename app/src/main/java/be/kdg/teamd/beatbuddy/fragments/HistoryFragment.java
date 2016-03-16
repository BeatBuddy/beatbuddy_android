package be.kdg.teamd.beatbuddy.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import be.kdg.teamd.beatbuddy.BeatBuddyApplication;
import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.adapters.HistoryTrackAdapter;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.model.playlists.PlaylistTrack;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.presenter.HistoryPresenter;
import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManager;
import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, HistoryPresenter.HistoryPresenterListener, HistoryTrackAdapter.TrackInteractionListener {
    public static final int RESULT_ADD_TRACK = 1;
    public static final String ARG_PLAYLIST_ID = "playlistid";

    @Bind(R.id.swiperefresh_history) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.history_recyclerview) RecyclerView recyclerView;

    private long playlistId;
    private List<PlaylistTrack> tracks = new ArrayList<>();
    private HistoryTrackAdapter trackAdapter;
    private HistoryPresenter presenter;
    private HistoryInteractionListener listener;

    @Override
    public void setArguments(Bundle args)
    {
        super.setArguments(args);
        playlistId = (long) args.get(ARG_PLAYLIST_ID);
    }

    public void setTracks(List<PlaylistTrack> tracks) {
        if (tracks == null) return;

        this.tracks.clear();
        this.tracks.addAll(tracks);
        trackAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    public List<PlaylistTrack> getHistory() {
        return tracks;
    }

    public void setListener(HistoryInteractionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        presenter = new HistoryPresenter(this, RepositoryFactory.getPlaylistRepository());

        playlistId = getArguments().getLong(ARG_PLAYLIST_ID);

        initializeRecyclerView();
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        presenter.refreshHistory(playlistId);

        return view;
    }

    private void initializeRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        tracks = new LinkedList<>();
        trackAdapter = new HistoryTrackAdapter(tracks, this);
        recyclerView.setAdapter(trackAdapter);

        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onRefresh() {
        presenter.refreshHistory(playlistId);
    }

    @Override
    public void onHistoryLoaded(List<PlaylistTrack> history) {
        setTracks(history);
    }

    @Override
    public void onException(String message)
    {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onTrackClicked(Track track, int position) {
        listener.onHistoryTrackClicked(track, position);
    }

    public interface HistoryInteractionListener{
        void onHistoryTrackClicked(Track track, int position);
    }
}
