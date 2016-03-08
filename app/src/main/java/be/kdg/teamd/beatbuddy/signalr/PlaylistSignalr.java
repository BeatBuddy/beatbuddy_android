package be.kdg.teamd.beatbuddy.signalr;

import android.util.Log;

import java.util.concurrent.ExecutionException;

import be.kdg.teamd.beatbuddy.model.playlists.CurrentPlayingViewModel;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.model.playlists.TrackSource;
import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;

/**
 * Created by Ignace on 8/03/2016.
 */
public class PlaylistSignalr
{
    public static final String SERVER = "https://teamd.azurewebsites.net/";
    private PlaylistSignalrListener listener;

    public PlaylistSignalr(PlaylistSignalrListener listener)
    {
        this.listener = listener;
    }

    public void connect(final String groupName)
    {
        Log.d("SignalR", "Connecting to Signalr with group " + groupName);

        HubConnection connection = new HubConnection(SERVER);
        final HubProxy proxy = connection.createHubProxy("playlistHub");

        SignalRFuture<Void> awaitConnection = connection.start();
        awaitConnection.done(new Action<Void>()
        {
            @Override
            public void run(Void aVoid) throws Exception
            {
                proxy.invoke("JoinGroup", groupName);
                Log.d("SignalR", "joining group with id: " + groupName);
            }
        });
        awaitConnection.onError(new ErrorCallback()
        {
            @Override
            public void onError(Throwable throwable)
            {
                Log.d("SignalR", "Error signalr: " + throwable.getMessage());
            }
        });
        try {
            awaitConnection.get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        proxy.on("startMusicPlaying", new SubscriptionHandler1<CurrentPlayingViewModel>() {
            @Override
            public void run(CurrentPlayingViewModel currentPlayingViewModel) {
                Track track = new Track();
                track.setCoverArtUrl(currentPlayingViewModel.getCoverArtUrl());
                track.setTitle(currentPlayingViewModel.getTitle());
                track.setArtist(currentPlayingViewModel.getArtist());

                listener.onNewTrackPlaying(track);
            }
        }, CurrentPlayingViewModel.class);
        proxy.on("addNewMessageToPage", new SubscriptionHandler()
        {
            @Override
            public void run()
            {
                listener.onTrackAdded();
            }
        });
        proxy.on("onPlaylinkGenerated", new SubscriptionHandler1<String>()
        {
            @Override
            public void run(String playlink)
            {
                listener.onPlaylinkFetched(playlink);
            }
        }, String.class);
    }

    public interface PlaylistSignalrListener
    {
        void onTrackAdded();
        void onNewTrackPlaying(Track track);
        void onPlaylinkFetched(String playlink);
    }
}
