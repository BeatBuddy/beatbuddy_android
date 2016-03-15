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
import be.kdg.teamd.beatbuddy.model.playlists.PlaylistTrack;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.util.DateUtil;
import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryTrackAdapter extends RecyclerView.Adapter<HistoryTrackAdapter.TrackHolder>
{
    private List<PlaylistTrack> tracks;
    private TrackInteractionListener listener;

    public HistoryTrackAdapter(List<PlaylistTrack> tracks, TrackInteractionListener listener)
    {
        this.tracks = tracks;
        this.listener = listener;
    }

    @Override
    public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historytrack, parent, false);
        return new TrackHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackHolder holder, final int position)
    {
        final PlaylistTrack playlistTrack = tracks.get(position);
        final Track track = playlistTrack.getTrack();
        Context context = holder.itemView.getContext();

        Picasso.with(context)
                .load(track.getCoverArtUrl())
                .error(R.drawable.default_cover)
                .into(holder.coverArt);

        holder.songTitle.setText(track.getTitle());
        holder.songArtist.setText(track.getArtist());
        holder.songDuration.setText(DateUtil.secondsToFormattedString(track.getDuration()));
        holder.score.setText(playlistTrack.getScore() + " points");

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTrackClicked(track, position);
            }
        });

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
        @Bind(R.id.historytrack_item) RelativeLayout item;

        public TrackHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private Track track;

        public Track getTrack() {
            return track;
        }

        public void setTrack(Track track) {
            this.track = track;
        }
    }

    public interface TrackInteractionListener
    {
        void onTrackClicked(Track track, int position);
    }
}
