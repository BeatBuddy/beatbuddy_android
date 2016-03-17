package be.kdg.teamd.beatbuddy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.model.ChatViewModel;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.util.DateUtil;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageHolder>
{
    private List<ChatViewModel> messages;

    public ChatMessageAdapter(List<ChatViewModel> messages)
    {
        this.messages = messages;
    }

    @Override
    public ChatMessageHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatmessage, parent, false);
        return new ChatMessageHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatMessageHolder holder, final int position)
    {
        final ChatViewModel message = messages.get(position);
        Context context = holder.itemView.getContext();

        Picasso.with(context)
                .load(message.getAvatarUrl())
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .fit()
                .into(holder.avatar);

        holder.name.setText(message.getUsername());
        holder.message.setText(message.getMessage());
    }

    @Override
    public int getItemCount()
    {
        return messages.size();
    }

    public class ChatMessageHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.chatmessage_avatar) ImageView avatar;
        @Bind(R.id.chatmessage_name) TextView name;
        @Bind(R.id.chatmessage_message) TextView message;

        public ChatMessageHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
