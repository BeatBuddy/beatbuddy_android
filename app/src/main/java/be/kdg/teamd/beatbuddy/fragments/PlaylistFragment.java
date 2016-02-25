package be.kdg.teamd.beatbuddy.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.adapters.TrackAdapter;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.model.playlists.TrackSource;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlaylistFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlaylistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistFragment extends Fragment
{
    private static final String PARAM_PLAYLIST_KEY = "key";

    @Bind(R.id.playlist_recyclerview) RecyclerView recyclerView;

    private OnFragmentInteractionListener mListener;
    private String playlistKey;

    private TrackAdapter trackAdapter;

    public PlaylistFragment()
    {
        // Required empty public constructor
    }

    public static PlaylistFragment newInstance(String playlistKey)
    {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_PLAYLIST_KEY, playlistKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            playlistKey = getArguments().getString(PARAM_PLAYLIST_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View playlist = inflater.inflate(R.layout.fragment_playlist, container, false);
        ButterKnife.bind(this, playlist);

        initializeRecyclerView();

        return playlist;
    }

    private void initializeRecyclerView()
    {
        //Set up RecyclerView
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        List<Track> tracks = new LinkedList<>();
        Track testTrack = new Track();
        testTrack.setCoverArtUrl("https://upload.wikimedia.org/wikipedia/en/5/56/1999_Joey_Badass.jpg");
        testTrack.setTitle("Waves");
        testTrack.setArtist("Joey Bada$$");

        tracks.add(testTrack);
        tracks.add(testTrack);
        tracks.add(testTrack);
        tracks.add(testTrack);
        tracks.add(testTrack);
        tracks.add(testTrack);
        tracks.add(testTrack);
        tracks.add(testTrack);
        tracks.add(testTrack);
        tracks.add(testTrack);
        tracks.add(testTrack);
        tracks.add(testTrack);
        tracks.add(testTrack);
        tracks.add(testTrack);

        trackAdapter = new TrackAdapter(tracks);
        recyclerView.setAdapter(trackAdapter);
    }

    //All below is auto-generated, but useful for later
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            /*throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }
}
