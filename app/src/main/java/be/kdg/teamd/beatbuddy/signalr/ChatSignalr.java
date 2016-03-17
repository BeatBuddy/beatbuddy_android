package be.kdg.teamd.beatbuddy.signalr;

import android.util.Log;

import java.util.concurrent.ExecutionException;

import be.kdg.teamd.beatbuddy.model.ChatViewModel;
import be.kdg.teamd.beatbuddy.model.playlists.CurrentPlayingViewModel;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.util.ImageEncoder;
import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler3;

/**
 * Created by Ignace on 8/03/2016.
 */
public class ChatSignalr
{
    public static final String SERVER = "https://teamd.azurewebsites.net/";
    private ChatSignalrListener listener;
    private HubProxy proxy;
    private String groupName;

    public ChatSignalr(ChatSignalrListener listener)
    {
        this.listener = listener;
    }

    public void connect(String groupNameId)
    {
        this.groupName = groupNameId;
        Log.d("SignalR", "Connecting to Chat Signalr with group " + groupName);

        HubConnection connection = new HubConnection(SERVER);
        proxy = connection.createHubProxy("chatHub");

        SignalRFuture<Void> awaitConnection = connection.start();
        awaitConnection.done(new Action<Void>()
        {
            @Override
            public void run(Void aVoid) throws Exception
            { joinGroup();
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
            e.printStackTrace();
        }

        proxy.on("BroadcastMessage", new SubscriptionHandler3<String, String, String>()
        {
            @Override
            public void run(String user, String message, String avatar)
            {
                String fullAvatarUrl = avatar;
                if (!fullAvatarUrl.startsWith(SERVER))
                    fullAvatarUrl = SERVER + fullAvatarUrl;

                ChatViewModel chatViewModel = new ChatViewModel();
                chatViewModel.setUsername(user);
                chatViewModel.setMessage(message);
                chatViewModel.setAvatarUrl(fullAvatarUrl);

                listener.onMessageReceived(chatViewModel);
            }
        }, String.class, String.class, String.class);
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

    public void send(String username, String message, String image)
    {
        SignalRFuture<Void> sendMessage = proxy.invoke("Send", username, message, image, groupName);
        sendMessage.done(new Action<Void>()
        {
            @Override
            public void run(Void aVoid) throws Exception
            {
                Log.d("SignalR", "Send message to: " + groupName);
            }
        });
        sendMessage.onError(new ErrorCallback()
        {
            @Override
            public void onError(Throwable throwable)
            {
                Log.d("SignalR", "Error sending message: " + throwable.getMessage());
            }
        });
        try {
            sendMessage.get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public interface ChatSignalrListener
    {
        void onMessageReceived(ChatViewModel chatViewModel);
    }
}
