package be.kdg.teamd.beatbuddy.presenter;

import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.model.playlists.Vote;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ignace on 7/03/2016.
 */
public class QueuePresenter
{
    private QueuePresenterListener listener;
    private PlaylistRepository playlistRepository;

    public QueuePresenter(QueuePresenterListener listener, PlaylistRepository playlistRepository)
    {
        this.listener = listener;
        this.playlistRepository = playlistRepository;
    }

    public void upvoteTrack(long playlistId, long trackId)
    {
        playlistRepository.upvoteTrack(playlistId, trackId).enqueue(new Callback<Vote>()
        {
            @Override
            public void onResponse(Response<Vote> response)
            {
                if (response.isSuccess())
                {
                    listener.onTrackUpvoted(response.body());
                }
                else
                {
                    listener.onException("Error upvoting track");
                }
            }

            public void onFailure(Throwable t)
            {
                listener.onException("Error: " + t.getMessage());
            }
        });
    }

    public void downvoteTrack(long playlistId, long trackId)
    {
        playlistRepository.downvoteTrack(playlistId, trackId).enqueue(new Callback<Vote>()
        {
            @Override
            public void onResponse(Response<Vote> response)
            {
                if (response.isSuccess())
                {
                    listener.onTrackDownvoted(response.body());
                }
                else
                {
                    listener.onException("Error downvoting track");
                }
            }

            public void onFailure(Throwable t)
            {
                listener.onException("Error: " + t.getMessage());
            }
        });
    }

    public interface QueuePresenterListener
    {
        void onTrackUpvoted(Vote vote);
        void onTrackDownvoted(Vote vote);
        void onException(String message);
    }
}
