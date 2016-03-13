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

    public void setPlaylistRepository(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void loadRecommendedPlaylists(int count){
        playlistRepository.getRecommendedPlaylists(count).enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Response<List<Playlist>> response) {
                if(!response.isSuccess()){
                    listener.onRecommendedPlaylistsException("Error fetching recommended playlists");
                    return;
                }

                listener.onRecommendedPlaylistsLoaded(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onRecommendedPlaylistsException("Error fetching recommended playlists: " + t.getMessage());
            }
        });
    }

    public void loadUserPlaylists(){
        userRepository.getUserPlaylists().enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Response<List<Playlist>> response) {
                if(!response.isSuccess()){
                    listener.onUserPlaylistsException("Error fetching user playlists");
                    return;
                }
                listener.onUserPlaylistsLoaded(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onUserPlaylistsException("Error fetching user playlists: " + t.getMessage());
            }
        });
    }

    public void loadUserOrganisations(){
        userRepository.getUserOrganisations().enqueue(new Callback<List<Organisation>>() {
            @Override
            public void onResponse(Response<List<Organisation>> response) {
                if(!response.isSuccess()){
                    listener.onUserOrganisationsException("Error fetching user organisations");
                    return;
                }

                listener.onUserOrganisationsLoaded(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onUserOrganisationsException("Error fetching user organisations: " + t.getMessage());
            }
        });
    }

    public interface MainPresenterListener {
        void onRecommendedPlaylistsLoaded(List<Playlist> playlists);
        void onUserPlaylistsLoaded(List<Playlist> playlists);
        void onUserOrganisationsLoaded(List<Organisation> organisations);
        void onRecommendedPlaylistsException(String message);
        void onUserPlaylistsException(String message);
        void onUserOrganisationsException(String message);
    }
}
