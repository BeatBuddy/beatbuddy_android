package be.kdg.teamd.beatbuddy.signalr;

import android.util.Log;

import java.util.concurrent.ExecutionException;

import be.kdg.teamd.beatbuddy.model.playlists.CurrentPlayingViewModel;
import be.kdg.teamd.beatbuddy.model.playlists.PlaylistTrack;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
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
                joinGroup();
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
            e.printStackTrace();
        }

        proxy.on("playLive", new SubscriptionHandler2<CurrentPlayingViewModel, Integer>() {
            @Override
            public void run(CurrentPlayingViewModel currentPlayingViewModel, Integer songProgressTime) {
                Track track = new Track();
                track.setCoverArtUrl(currentPlayingViewModel.getCoverArtUrl());
                track.setTitle(currentPlayingViewModel.getTitle());
                track.setArtist(currentPlayingViewModel.getArtist());

                listener.onPlay(track);
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
        proxy.on("pauseMusicPlaying", new SubscriptionHandler()
        {
            @Override
            public void run()
            {
                listener.onPauseMusic();
            }
        });
        proxy.on("resumeMusicPlaying", new SubscriptionHandler1<Integer>() {
            @Override
            public void run(Integer position) {
                listener.onResumeMusic(position);
            }
        }, Integer.class);
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
        proxy.on("onPlaylinkGeneratedSync", new SubscriptionHandler2<String, Integer>() {
            @Override
            public void run(String playlink, Integer songProgressTime) {
                listener.onPlaylinkFetchedSync(playlink, songProgressTime);
            }
        }, String.class, Integer.class);
        proxy.on("scoreUpdated", new SubscriptionHandler2<Long,PlaylistTrack>() {
            @Override
            public void run(Long playlistTrackId, PlaylistTrack playlistTrack) {
                Log.d("SignalR", "Track with Id " + playlistTrackId + " has its score updated to " + playlistTrack.getScore());
                listener.onScoreUpdated(playlistTrackId, playlistTrack);
            }
        }, Long.class, PlaylistTrack.class);
        proxy.on("syncLive", new SubscriptionHandler()
        {
            @Override
            public void run()
            {
                listener.syncLive();
            }
        });
    }

    private void joinGroup()
    {
        SignalRFuture<Void> joinGroup = proxy.invoke("JoinGroup", groupName);
        joinGroup.done(new Action<Void>()
        {
            @Override
            public void run(Void aVoid) throws Exception
            {
                Log.d("SignalR", "Joined group: " + groupName);
            }
        });
        joinGroup.onError(new ErrorCallback()
        {
            @Override
            public void onError(Throwable throwable)
            {
                Log.d("SignalR", "Error joining group: " + throwable.getMessage());
            }
        });
        try {
            joinGroup.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void playLive()
    {
        SignalRFuture<Void> playLive = proxy.invoke("PlayLive", groupName);
        playLive.done(new Action<Void>()
        {
            @Override
            public void run(Void aVoid) throws Exception
            {
                Log.d("SignalR", "Requested to play live: " + groupName);
            }
        });
        playLive.onError(new ErrorCallback()
        {
            @Override
            public void onError(Throwable throwable)
            {
                Log.d("SignalR", "Error request to play live: " + throwable.getMessage());
            }
        });
        try {
            playLive.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
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
            e.printStackTrace();
        }
    }

    public void resumePlaying(final long durationAt)
    {
        SignalRFuture<Void> resumePlaying = proxy.invoke("ResumePlaying", durationAt, groupName);
        resumePlaying.done(new Action<Void>()
        {
            @Override
            public void run(Void aVoid) throws Exception
            {
                Log.d("SignalR", "Resumed playing: " + durationAt + " group: " + groupName);
            }
        });
        resumePlaying.onError(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                Log.d("SignalR", "Error resuming: " + throwable.getMessage());
            }
        });
        try {
            resumePlaying.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void syncLive(float position, String artist, String coverArtUrl, int nextTracks, String title, String trackId)
    {
        CurrentPlayingViewModel currentPlayingViewModel = new CurrentPlayingViewModel();
        currentPlayingViewModel.setArtist(artist);
        currentPlayingViewModel.setCoverArtUrl(coverArtUrl);
        currentPlayingViewModel.setNextTracks(nextTracks);
        currentPlayingViewModel.setTitle(title);
        currentPlayingViewModel.setTrackId(trackId);

        SignalRFuture<Void> syncLive = proxy.invoke("SyncLive", groupName, currentPlayingViewModel, position);
        syncLive.done(new Action<Void>()
        {
            @Override
            public void run(Void aVoid) throws Exception
            {
                Log.d("SignalR", "Send sync live: " + groupName);
            }
        });
        syncLive.onError(new ErrorCallback()
        {
            @Override
            public void onError(Throwable throwable)
            {
                Log.d("SignalR", "Error sending sync live: " + throwable.getMessage());
            }
        });
        try {
            syncLive.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public interface PlaylistSignalrListener
    {
        void onTrackAdded();
        void onStopMusic();
        void onPauseMusic();
        void onResumeMusic(int position);
        void onPlay(Track track);
        void onErrorConnecting(String errorMessage);
        void onPlaylinkFetched(String playlink);
        void onPlaylinkFetchedSync(String playlink, int position);
        void onScoreUpdated(long playlistTrackId, PlaylistTrack playlistTrack);
        void syncLive();
    }
}
