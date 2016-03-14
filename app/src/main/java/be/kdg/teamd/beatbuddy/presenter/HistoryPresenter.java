package be.kdg.teamd.beatbuddy.presenter;

import java.util.List;

import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.model.playlists.PlaylistTrack;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ignace on 7/03/2016.
 */
public class HistoryPresenter
{
    private HistoryPresenterListener listener;
    private PlaylistRepository playlistRepository;

    public HistoryPresenter(HistoryPresenterListener listener, PlaylistRepository playlistRepository)
    {
        this.listener = listener;
        this.playlistRepository = playlistRepository;
    }

    public void refreshHistory(long playlistId) {
        playlistRepository.getHistory(playlistId).enqueue(new Callback<List<PlaylistTrack>>() {
            @Override
            public void onResponse(Response<List<PlaylistTrack>> response) {
                if(!response.isSuccess()){
                    listener.onException("Error loading playlist history.");
                    return;
                }

                listener.onHistoryLoaded(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onException("Error loading history: " + t.getMessage());
            }
        });
    }

    public interface HistoryPresenterListener
    {
        void onHistoryLoaded(List<PlaylistTrack> history);
        void onException(String message);
    }
}
