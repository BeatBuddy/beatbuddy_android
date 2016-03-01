package be.kdg.teamd.beatbuddy.presenter;

import java.util.List;

import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.organisations.Organisation;
import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPresenter {
    private PlaylistRepository playlistRepository;
    private UserRepository userRepository;
    private MainPresenterListener listener;

    public MainPresenter(MainPresenterListener listener, UserRepository userRepository, PlaylistRepository playlistRepository) {
        this.listener = listener;
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
    }

    public void loadRecommendedPlaylists(int count){
        playlistRepository.getRecommendedPlaylists(count).enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Response<List<Playlist>> response) {
                if(!response.isSuccess()){
                    listener.onException("Error fetching recommended playlists");
                    return;
                }

                listener.onRecommendedPlaylistsLoaded(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onException(t.getMessage());
            }
        });
    }

    public void loadUserPlaylists(){
        userRepository.getUserPlaylists().enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Response<List<Playlist>> response) {
                if(!response.isSuccess()){
                    listener.onException("Error fetching user playlists");
                    return;
                }
                listener.onUserPlaylistsLoaded(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onException(t.getMessage());
            }
        });
    }

    public void loadUserOrganisations(){
        userRepository.getUserOrganisations().enqueue(new Callback<List<Organisation>>() {
            @Override
            public void onResponse(Response<List<Organisation>> response) {
                if(!response.isSuccess()){
                    listener.onException("Error fetching user organisations");
                    return;
                }

                listener.onUserOrganisationsLoaded(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onException(t.getMessage());
            }
        });
    }

    public interface MainPresenterListener {
        void onRecommendedPlaylistsLoaded(List<Playlist> playlists);
        void onUserPlaylistsLoaded(List<Playlist> playlists);
        void onUserOrganisationsLoaded(List<Organisation> organisations);
        void onException(String message);
    }
}
