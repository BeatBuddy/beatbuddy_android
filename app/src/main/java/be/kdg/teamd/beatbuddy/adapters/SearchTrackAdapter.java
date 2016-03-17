package be.kdg.teamd.beatbuddy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.util.DateUtil;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchTrackAdapter extends RecyclerView.Adapter<SearchTrackAdapter.TrackHolder>
{
    private List<Track> tracks;
    private SearchTrackListener listener;

    public SearchTrackAdapter(List<Track> tracks, SearchTrackListener listener)
    {
        this.tracks = tracks;
        this.listener = listener;
    }

    @Override
    public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track_search, parent, false);
        return new TrackHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackHolder holder, int position)
    {
        final Track track = tracks.get(position);
        Context context = holder.itemView.getContext();

        Picasso.with(context)
                .load(track.getCoverArtUrl())
                .placeholder(R.drawable.default_cover)
                .error(R.drawable.default_cover)
                .fit()
                .centerCrop()
                .into(holder.coverArt);

        holder.songTitle.setText(track.getTitle());
        holder.songArtist.setText(track.getArtist());

        holder.itemView.setBackgroundColor(position % 2 == 0 ? Color.rgb(250, 250, 250) : Color.rgb(245, 245, 245));
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onClickTrack(track);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return tracks.size();
    }

    public void updateTracks(List<Track> tracks)
    {
        this.tracks = tracks;
        notifyDataSetChanged();
    }

    public class TrackHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.track_coverart) ImageView coverArt;

        @Bind(R.id.track_song_title) TextView songTitle;
        @Bind(R.id.track_song_artist) TextView songArtist;

        public TrackHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface SearchTrackListener
    {
        void onClickTrack(Track track);
    }
}
