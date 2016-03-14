package be.kdg.teamd.beatbuddy.presenter;

import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManager;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistPresenter {
    private PlaylistPresenterListener listener;
    private PlaylistRepository playlistRepository;
    private UserConfigurationManager userConfigurationManager;

    public PlaylistPresenter(PlaylistPresenterListener listener, PlaylistRepository playlistRepository, UserConfigurationManager userConfigurationManager) {
        this.listener = listener;
        this.playlistRepository = playlistRepository;
        this.userConfigurationManager = userConfigurationManager;
    }

    public void loadPlaylist(long playlistId){
        if (userConfigurationManager.isLoggedIn())
        {
            playlistRepository.getLivePlaylist(playlistId).enqueue(new Callback<Playlist>()
            {
                @Override
                public void onResponse(Response<Playlist> response)
                {
                    if(!response.isSuccess()){
                        listener.onException("Error retrieving live playlist data");
                        return;
                    }

                    listener.onPlaylistLoaded(response.body());
                }

                @Override
                public void onFailure(Throwable t) {
                    listener.onException("Error: " + t.getMessage());
                }
            });
        }
        else
        {
            playlistRepository.getPlaylist(playlistId).enqueue(new Callback<Playlist>() {
                @Override
                public void onResponse(Response<Playlist> response) {
                    if(!response.isSuccess()){
                        listener.onException("Error retrieving playlist data");
                        return;
                    }

                    listener.onPlaylistLoaded(response.body());
                }

                @Override
                public void onFailure(Throwable t) {
                    listener.onException("Error: " + t.getMessage());
                }
            });
        }
    }

    public void playNextSong(long playlistId)
    {
        playlistRepository.getNextTrack(playlistId).enqueue(new Callback<Track>()
        {
            @Override
            public void onResponse(Response<Track> response)
            {
                if (response.isSuccess())
                {
                    listener.onPlaySong(response.body());
                }
                else
                {
                    listener.onException("Error retrieving next song");
                }
            }

            @Override
            public void onFailure(Throwable t)
            {
                listener.onException("Error: " + t.getMessage());
            }
        });
    }

    public void getPlaybackTrack(long trackId){
        playlistRepository.getPlaybackTrack(trackId).enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Response<Track> response) {
                if(!response.isSuccess()){
                    listener.onException("Error retrieving track");
                    return;
                }

                listener.onPlaybackTrackLoaded(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onException("Error retrieving track: " + t.getMessage());
            }
        });
    }

    public interface PlaylistPresenterListener{
        void onPlaybackTrackLoaded(Track track);
        void onPlaylistLoaded(Playlist playlist);
        void onPlaySong(Track track);
        void onException(String message);
    }
}
