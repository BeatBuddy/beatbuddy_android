package be.kdg.teamd.beatbuddy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

import be.kdg.teamd.beatbuddy.BeatBuddyApplication;
import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.adapters.ChatMessageAdapter;
import be.kdg.teamd.beatbuddy.model.ChatViewModel;
import be.kdg.teamd.beatbuddy.signalr.ChatSignalr;
import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManager;
import be.kdg.teamd.beatbuddy.util.ImageEncoder;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatFragment extends Fragment implements ChatSignalr.ChatSignalrListener
{
    public static final String ARG_GROUP = "GROUP";

    @Bind(R.id.playlist_chat) RecyclerView chat;
    @Bind(R.id.chat_message) EditText message;
    @Bind(R.id.chat_send) ImageView chatSendButton;

    private ChatSignalr signalr;
    private String group;
    private String username;
    private String avatarUrl;

    private List<ChatViewModel> messages;
    private ChatMessageAdapter adapter;

    public static ChatFragment newInstance(String group)
    {
        ChatFragment chat = new ChatFragment();

        Bundle args = new Bundle();
        args.putString(ARG_GROUP, group);
        chat.setArguments(args);

        return chat;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);

        UserConfigurationManager config = ((BeatBuddyApplication) getActivity().getApplication()).getUserConfigurationManager();
        if(config.isLoggedIn()){
            this.username = config.getUser().getNickname();
            this.avatarUrl = getString(R.string.avatarImageLocation) + config.getUser().getImageUrl();
            this.group = getArguments().getString(ARG_GROUP);
        } else {
            message.setEnabled(false);
            message.setHint(R.string.login_chat);
            chatSendButton.setEnabled(false);
        }

        initializeRecyclerView();

        signalr = new ChatSignalr(this);
        signalr.connect(group);

        return view;
    }

    private void initializeRecyclerView()
    {
        chat.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        chat.setLayoutManager(manager);

        messages = new LinkedList<>();
        adapter = new ChatMessageAdapter(messages);
        chat.setAdapter(adapter);
    }

    @OnClick(R.id.chat_send) void onSendMessage()
    {
        signalr.send(username, message.getText().toString(), avatarUrl);
        message.getText().clear();
    }

    @Override
    public void onMessageReceived(ChatViewModel chatViewModel)
    {
        chatViewModel.setAvatarUrl(ImageEncoder.encodeFullImageUrl(chatViewModel.getAvatarUrl(), getString(R.string.avatarImageLocation)));
        messages.add(chatViewModel);
        adapter.notifyItemInserted(adapter.getItemCount());
        chat.smoothScrollToPosition(adapter.getItemCount() - 1);
    }
}
