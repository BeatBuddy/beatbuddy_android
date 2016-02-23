package be.kdg.teamd.beatbuddy.presenter;

import android.text.TextUtils;

import java.util.List;

import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.dal.UserRepository;
import be.kdg.teamd.beatbuddy.model.organisations.Organisation;
import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePlaylistPresenter {
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final CreatePlaylistPresenterListener listener;

    public CreatePlaylistPresenter(PlaylistRepository playlistRepository, UserRepository userRepository, CreatePlaylistPresenterListener listener) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.listener  = listener;
    }

    public void fetchOrganisations(long currentUserId) {
        userRepository.getOrganisations(currentUserId).enqueue(new Callback<List<Organisation>>() {
            @Override
            public void onResponse(Response<List<Organisation>> response) {
                if(!response.isSuccess()){
                    listener.onException("Error fetching user organisations");
                    return;
                }

                listener.onReceivedOrganisations(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onException(t.getMessage());
            }
        });
    }

    public void createPlaylistAsUser(String name, String key, String description, String imageBase64){
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(key)){
            listener.onException("Please fill in all fields");
            return;
        }

        playlistRepository.createPlaylist(name, key, description, imageBase64).enqueue(new Callback<Playlist>(){

            @Override
            public void onResponse(Response<Playlist> response) {
                if(!response.isSuccess()){
                    listener.onException("Error creating playlist");
                    return;
                }

                listener.onPlaylistCreated(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onException(t.getMessage());
            }
        });
    }

    public void createPlaylistAsOrganisation(long organisationId, String name, String key, String description, String imageBase64){
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(key)){
            listener.onException("Please fill in all fields");
            return;
        }

        playlistRepository.createPlaylist(organisationId, name, key, description, imageBase64).enqueue(new Callback<Playlist>(){

            @Override
            public void onResponse(Response<Playlist> response) {
                if(!response.isSuccess()){
                    listener.onException("Error creating playlist");
                    return;
                }

                listener.onPlaylistCreated(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onException(t.getMessage());
            }
        });
    }

    public interface CreatePlaylistPresenterListener {
        void onReceivedOrganisations(List<Organisation> organisations);
        void onPlaylistCreated(Playlist playlist);
        void onException(String message);
    }
}
