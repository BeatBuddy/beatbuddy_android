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
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;

/**
 * Created by Ignace on 8/03/2016.
 */
public class PlaylistSignalr
{
    public static final String SERVER = "https://teamd.azurewebsites.net/";
    private PlaylistSignalrListener listener;
    private HubProxy proxy;
    private String groupName;

    public PlaylistSignalr(PlaylistSignalrListener listener)
    {
        this.listener = listener;
    }

    public void connect(String groupNameId)
    {
        this.groupName = groupNameId;
        Log.d("SignalR", "Connecting to Signalr with group " + groupName);

        HubConnection connection = new HubConnection(SERVER);
        proxy = connection.createHubProxy("playlistHub");

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
                String message = "Error SignalR";
                if (throwable != null)
                    message = throwable.getMessage();

                listener.onErrorConnecting(message);
                Log.d("SignalR", "Error signalr: " + message);
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
        proxy.on("playLive", new SubscriptionHandler2<CurrentPlayingViewModel, Integer>() {
            @Override
            public void run(CurrentPlayingViewModel currentPlayingViewModel, Integer songProgressTime) {
                Track track = new Track();
                track.setCoverArtUrl(currentPlayingViewModel.getCoverArtUrl());
                track.setTitle(currentPlayingViewModel.getTitle());
                track.setArtist(currentPlayingViewModel.getArtist());

                listener.onPlayLive(track, songProgressTime);
            }
        }, CurrentPlayingViewModel.class, Integer.class);
        proxy.on("stopMusicPlaying", new SubscriptionHandler()
        {
            @Override
            public void run()
            {
                listener.onStopMusic();
            }
        });
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

    public void addTrack()
    {
        SignalRFuture<Void> addTrack = proxy.invoke("StartPlaying", groupName);
        addTrack.done(new Action<Void>()
        {
            @Override
            public void run(Void aVoid) throws Exception
            {
                Log.d("SignalR", "added track: " + groupName);
            }
        });
        addTrack.onError(new ErrorCallback()
        {
            @Override
            public void onError(Throwable throwable)
            {
                Log.d("SignalR", "Error adding track: " + throwable.getMessage());
            }
        });
        try {
            addTrack.get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void startPlaying(String artist, String coverArtUrl, int nextTracks, String title, String trackId)
    {
        CurrentPlayingViewModel currentPlayingViewModel = new CurrentPlayingViewModel();
        currentPlayingViewModel.setArtist(artist);
        currentPlayingViewModel.setCoverArtUrl(coverArtUrl);
        currentPlayingViewModel.setNextTracks(nextTracks);
        currentPlayingViewModel.setTitle(title);
        currentPlayingViewModel.setTrackId(trackId);

        SignalRFuture<Void> addTrack = proxy.invoke("StartPlaying", currentPlayingViewModel, groupName);
        addTrack.done(new Action<Void>()
        {
            @Override
            public void run(Void aVoid) throws Exception
            {
                Log.d("SignalR", "Started playing: " + groupName);
            }
        });
        addTrack.onError(new ErrorCallback()
        {
            @Override
            public void onError(Throwable throwable)
            {
                Log.d("SignalR", "Error playing: " + throwable.getMessage());
            }
        });
        try {
            addTrack.get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void pausePlaying()
    {
        SignalRFuture<Void> addTrack = proxy.invoke("PausePlaying", groupName);
        addTrack.done(new Action<Void>()
        {
            @Override
            public void run(Void aVoid) throws Exception
            {
                Log.d("SignalR", "Paused playing: " + groupName);
            }
        });
        addTrack.onError(new ErrorCallback()
        {
            @Override
            public void onError(Throwable throwable)
            {
                Log.d("SignalR", "Error playing: " + throwable.getMessage());
            }
        });
        try {
            addTrack.get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void resumePlaying(long durationAt)
    {
        proxy.invoke("ResumePlaying", durationAt, groupName);
    }

    public interface PlaylistSignalrListener
    {
        void onTrackAdded();
        void onStopMusic();
        void onPlayLive(Track track, int currentTrackProgress);
        void onNewTrackPlaying(Track track);
        void onPlaylinkFetched(String playlink);
        void onErrorConnecting(String errorMessage);
    }
}
