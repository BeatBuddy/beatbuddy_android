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
import be.kdg.teamd.beatbuddy.model.playlists.PlaylistTrack;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.util.DateUtil;
import butterknife.Bind;
import butterknife.ButterKnife;

public class PlaylistTrackAdapter extends RecyclerView.Adapter<PlaylistTrackAdapter.TrackHolder>
{
    private List<PlaylistTrack> tracks;

    public PlaylistTrackAdapter(List<PlaylistTrack> tracks)
    {
        this.tracks = tracks;
    }

    @Override
    public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new TrackHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackHolder holder, int position)
    {
        Track track = tracks.get(position).getTrack();
        Context context = holder.itemView.getContext();

        Picasso.with(context)
                .load(track.getCoverArtUrl())
                .into(holder.coverArt);

        holder.songTitle.setText(track.getTitle());
        holder.songArtist.setText(track.getArtist());
        holder.songDuration.setText(DateUtil.secondsToFormattedString(track.getDuration()));

        holder.itemView.setBackgroundColor(position % 2 == 0 ? Color.rgb(250, 250, 250) : Color.rgb(245, 245, 245));
    }

    @Override
    public int getItemCount()
    {
        return tracks.size();
    }

    public class TrackHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.track_coverart) ImageView coverArt;

        @Bind(R.id.track_song_title) TextView songTitle;
        @Bind(R.id.track_song_artist) TextView songArtist;
        @Bind(R.id.track_song_duration) TextView songDuration;

        @Bind(R.id.track_score) TextView score;
        @Bind(R.id.track_upvote) ImageView upvote;
        @Bind(R.id.track_downvote) ImageView downvote;

        public TrackHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
