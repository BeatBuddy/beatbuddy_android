package be.kdg.teamd.beatbuddy.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import be.kdg.teamd.beatbuddy.BeatBuddyApplication;
import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.model.playlists.Vote;
import be.kdg.teamd.beatbuddy.presenter.QueuePresenter;
import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManager;
import be.kdg.teamd.beatbuddy.activities.AddTrackActivity;
import be.kdg.teamd.beatbuddy.adapters.PlaylistTrackAdapter;
import be.kdg.teamd.beatbuddy.model.playlists.PlaylistTrack;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QueueFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, PlaylistTrackAdapter.TrackInteractionListener, QueuePresenter.QueuePresenterListener
{
    public static final int RESULT_ADD_TRACK = 1;
    public static final String ARG_PLAYLIST_ID = "playlistid";

    @Bind(R.id.swiperefresh_queue) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.playlist_recyclerview) RecyclerView recyclerView;
    @Bind(R.id.queue_add_track_fab) FloatingActionButton addTrackFab;

    private long playlistId;
    private List<PlaylistTrack> tracks;
    private PlaylistTrackAdapter trackAdapter;
    private QueueFragmentListener listener;
    private UserConfigurationManager userConfigurationManager;
    private QueuePresenter presenter;

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

        presenter = new QueuePresenter(this, RepositoryFactory.getPlaylistRepository());

        playlistId = getArguments().getLong(ARG_PLAYLIST_ID);

        initializeRecyclerView();
        swipeRefreshLayout.setOnRefreshListener(this);

        userConfigurationManager = ((BeatBuddyApplication) getActivity().getApplication()).getUserConfigurationManager();
        addTrackFab.setVisibility(userConfigurationManager.isLoggedIn() ? View.VISIBLE : View.GONE);

        return view;
    }

    private void initializeRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        tracks = new LinkedList<>();
        trackAdapter = new PlaylistTrackAdapter(tracks, this);
        recyclerView.setAdapter(trackAdapter);

        swipeRefreshLayout.setRefreshing(true);
    }

    @OnClick(R.id.queue_add_track_fab) void addTrack()
    {
        Intent intent = new Intent(getContext(), AddTrackActivity.class);
        intent.putExtra(AddTrackActivity.EXTRA_PLAYLIST_ID, playlistId);

        startActivityForResult(intent, RESULT_ADD_TRACK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RESULT_ADD_TRACK && resultCode == Activity.RESULT_OK)
            listener.onTrackAddedCallback();

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        listener.onQueueRefreshRequested();
    }

    @Override
    public void onUpvoteTrackClicked(long trackId)
    {
        Vote tempVote = new Vote();
        tempVote.setScore(1);
        updateTrackScoreLocal(tempVote, trackId);

        presenter.upvoteTrack(playlistId, trackId);
    }

    @Override
    public void onDownvoteTrackClicked(long trackId)
    {
        Vote tempVote = new Vote();
        tempVote.setScore(-1);
        updateTrackScoreLocal(tempVote, trackId);

        presenter.downvoteTrack(playlistId, trackId);
    }

    @Override
    public void onTrackUpvoted(Vote vote, long trackId)
    {
        updateTrackScoreLocal(vote, trackId);
    }

    @Override
    public void onTrackDownvoted(Vote vote, long trackId)
    {
        updateTrackScoreLocal(vote, trackId);
    }

    @Override
    public void onException(String message)
    {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show();
        listener.onQueueRefreshRequested();
    }

    private void updateTrackScoreLocal(Vote vote, long trackId)
    {
        for (PlaylistTrack track : tracks)
        {
            if (track.getTrack().getId() == trackId)
            {
                track.setScore(track.getScore() + vote.getScore() - track.getPersonalScore());
                track.setPersonalScore(vote.getScore());
                trackAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public interface QueueFragmentListener{
        void onQueueRefreshRequested();
        void onTrackAddedCallback();
    }
}
