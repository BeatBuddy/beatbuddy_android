package be.kdg.teamd.beatbuddy.presenter;

import java.util.List;

import be.kdg.teamd.beatbuddy.dal.OrganisationRepository;
import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.model.organisations.Organisation;
import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTrackPresenter
{
    private PlaylistRepository playlistRepository;
    private AddTrackListener listener;

    public AddTrackPresenter(AddTrackListener listener, PlaylistRepository playlistRepository) {
        this.listener = listener;
        this.playlistRepository = playlistRepository;
    }

    public void searchTrack(String query)
    {
        playlistRepository.getTracks(query).enqueue(new Callback<List<Track>>()
        {
            @Override
            public void onResponse(Response<List<Track>> response)
            {
                if (response.isSuccess())
                {
                    listener.onSearchResult(response.body());
                }
                else
                {
                    listener.onException("Invalid search");
                }
            }

            @Override
            public void onFailure(Throwable t)
            {
                listener.onException("Couldn't search for tracks: " + t.getMessage());
            }
        });
    }

    public void addTrack(long playlistId, String trackId)
    {
        playlistRepository.addTrack(playlistId, trackId).enqueue(new Callback<Track>()
        {
            @Override
            public void onResponse(Response<Track> response)
            {
                if (response.isSuccess())
                {
                    listener.onTrackAdded(response.body());
                }
                else
                {
                    listener.onException("Invalid add track");
                }
            }

            @Override
            public void onFailure(Throwable t)
            {
                listener.onException("Couldn't add track: " + t.getMessage());
            }
        });
    }

    public interface AddTrackListener {
        void onSearchResult(List<Track> tracks);
        void onTrackAdded(Track track);
        void onException(String message);
    }
}
