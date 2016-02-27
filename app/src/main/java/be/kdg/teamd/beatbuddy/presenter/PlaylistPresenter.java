package be.kdg.teamd.beatbuddy.presenter;

import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistPresenter {
    private PlaylistPresenterListener listener;
    private PlaylistRepository playlistRepository;

    public PlaylistPresenter(PlaylistPresenterListener listener, PlaylistRepository playlistRepository) {
        this.listener = listener;
        this.playlistRepository = playlistRepository;
    }

    public void loadPlaylist(long playlistId){
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

    public interface PlaylistPresenterListener{
        void onPlaylistLoaded(Playlist playlist);
        void onException(String message);
    }

}
