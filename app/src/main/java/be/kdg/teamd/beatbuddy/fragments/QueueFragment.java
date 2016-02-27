package be.kdg.teamd.beatbuddy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.adapters.PlaylistTrackAdapter;
import be.kdg.teamd.beatbuddy.model.playlists.PlaylistTrack;
import butterknife.Bind;
import butterknife.ButterKnife;

public class QueueFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.swiperefresh_queue) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.playlist_recyclerview) RecyclerView recyclerView;

    private String playlistKey;
    private List<PlaylistTrack> tracks;
    private PlaylistTrackAdapter trackAdapter;
    private QueueFragmentListener listener;

    public String getPlaylistKey() {
        return playlistKey;
    }

    public void setPlaylistKey(String playlistKey) {
        this.playlistKey = playlistKey;
    }

    public void setTracks(List<PlaylistTrack> tracks) {
        this.tracks.clear();
        this.tracks.addAll(tracks);
        trackAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void setListener(QueueFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_queue, container, false);
        ButterKnife.bind(this, view);

        initializeRecyclerView();
        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    private void initializeRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        tracks = new LinkedList<>();
        trackAdapter = new PlaylistTrackAdapter(tracks);
        recyclerView.setAdapter(trackAdapter);

        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onRefresh() {
        listener.onQueueRefreshRequested();
    }

    public interface QueueFragmentListener{
        void onQueueRefreshRequested();
    }
}
