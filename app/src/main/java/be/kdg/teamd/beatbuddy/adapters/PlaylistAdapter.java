package be.kdg.teamd.beatbuddy.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import be.kdg.teamd.beatbuddy.util.ImageEncoder;
import butterknife.Bind;
import butterknife.ButterKnife;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder>
{
    private Context context;
    private List<Playlist> playlists;
    private PlaylistClickedListener listener;

    public PlaylistAdapter(Context context, List<Playlist> playlists, PlaylistClickedListener listener)
    {
        this.context = context;
        this.playlists = playlists;
        this.listener = listener;
    }

    @Override
    public PlaylistHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistHolder holder, int position)
    {
        final Playlist playlist = playlists.get(position);
        Context context = holder.itemView.getContext();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPlaylistClicked(playlist);
            }
        });

        if(!TextUtils.isEmpty(playlist.getImageUrl()))
            Picasso.with(context)
                .load(context.getString(R.string.playlistImageLocation) + ImageEncoder.encodeImageUrl(playlist.getImageUrl()))
                    .fit()
                    .centerCrop()
                    .into(holder.playlistImage);
        else
            holder.playlistImage = null;

        holder.playlistName.setText(playlist.getName());
    }

    @Override
    public int getItemCount()
    {
        return playlists.size();
    }

    public class PlaylistHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.item_playlist_image) ImageView playlistImage;
        @Bind(R.id.item_playlist_name) TextView playlistName;

        public PlaylistHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface PlaylistClickedListener{
        void onPlaylistClicked(Playlist playlist);
    }
}
