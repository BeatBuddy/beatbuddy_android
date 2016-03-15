package be.kdg.teamd.beatbuddy.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import java.util.List;

import be.kdg.teamd.beatbuddy.BeatBuddyApplication;
import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.adapters.PlaylistTabAdapter;
import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.fragments.ChatFragment;
import be.kdg.teamd.beatbuddy.fragments.HistoryFragment;
import be.kdg.teamd.beatbuddy.fragments.QueueFragment;
import be.kdg.teamd.beatbuddy.model.playlists.PlaybackType;
import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import be.kdg.teamd.beatbuddy.model.playlists.PlaylistTrack;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.presenter.PlaylistPresenter;
import be.kdg.teamd.beatbuddy.signalr.PlaylistSignalr;
import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManager;
import be.kdg.teamd.beatbuddy.util.DateUtil;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaylistActivity extends AppCompatActivity implements PlaylistPresenter.PlaylistPresenterListener, QueueFragment.QueueFragmentListener, PlaylistSignalr.PlaylistSignalrListener, HistoryFragment.HistoryInteractionListener, MediaPlayer.OnCompletionListener {
    public static final String EXTRA_PLAYLIST_KEY = "KEY";
    public static final String EXTRA_PLAYLIST_TEST = "TEST";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tablayout) TabLayout tabLayout;
    @Bind(R.id.playlist_viewpager) ViewPager viewPager;
    @Bind(R.id.musicplayer_play_pause) ImageView playPauseButton;
    @Bind(R.id.musicplayer_videoview) VideoView videoView;
    @Bind(R.id.musicplayer_cover_art) ImageView coverArt;
    @Bind(R.id.musicplayer_song_title) TextView songTitle;
    @Bind(R.id.musicplayer_song_artist) TextView songArtist;
    @Bind(R.id.musicplayer_song_timeleft) TextView songTimeLeft;
    @Bind(R.id.musicplayer_progress) ProgressBar songProgress;
    @Bind(R.id.musicplayer_loading) ProgressBar songLoading;

    private UserConfigurationManager userConfigurationManager;
    private PlaylistRepository playlistRepository;
    private PlaylistPresenter presenter;
    private PlaylistSignalr signalr;
    private Track track;

    private QueueFragment queueFragment;
    private ChatFragment chatFragment;
    private HistoryFragment historyFragment;

    private long playlistId;
    private Playlist playlist;
    private boolean isPlaylistMaster;

    private PlaybackType lastPlaybackType = PlaybackType.PLAYLIST;
    private int historyPosition = 0;

    public void setPlaylistRepository(PlaylistRepository playlistRepository, UserConfigurationManager userConfigurationManager) {
        this.playlistRepository = playlistRepository;
        this.userConfigurationManager = userConfigurationManager;
        this.presenter = new PlaylistPresenter(this, playlistRepository, userConfigurationManager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        ButterKnife.bind(this);

        playlistRepository = RepositoryFactory.getPlaylistRepository();
        userConfigurationManager = ((BeatBuddyApplication) getApplication()).getUserConfigurationManager();
        presenter = new PlaylistPresenter(this, playlistRepository, userConfigurationManager);

        String playlistKey = getIntent().getStringExtra(EXTRA_PLAYLIST_KEY);
        boolean isTesting = getIntent().getBooleanExtra(EXTRA_PLAYLIST_TEST, false);
        if(playlistKey != null) playlistId = Long.parseLong(playlistKey);

        setupViewPager(viewPager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(playlistKey);

        setupSignalR();

        if(!isTesting){
            presenter.loadPlaylist(playlistId); // TODO: join by key, not by ID
        }
    }

    private void setupSignalR()
    {
        signalr = new PlaylistSignalr(this);
        signalr.connect(playlistId + "");
    }

    private void setupViewPager(final ViewPager viewPager)
    {
        queueFragment = new QueueFragment();
        queueFragment.setListener(this);
        Bundle arguments = new Bundle();
        arguments.putLong(QueueFragment.ARG_PLAYLIST_ID, playlistId);
        queueFragment.setArguments(arguments);

        historyFragment = new HistoryFragment();
        Bundle historyArguments = new Bundle();
        historyArguments.putLong(HistoryFragment.ARG_PLAYLIST_ID, playlistId);
        historyFragment.setArguments(arguments);
        historyFragment.setListener(this);

        chatFragment = new ChatFragment();

        PlaylistTabAdapter adapter = new PlaylistTabAdapter(getSupportFragmentManager());
        adapter.addFrag(queueFragment, getString(R.string.queue));
        adapter.addFrag(chatFragment, getString(R.string.chat));
        adapter.addFrag(historyFragment, getString(R.string.history));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlaylistLoaded(Playlist playlist) {
        this.playlist = playlist;
        queueFragment.setTracks(playlist.getPlaylistTracks());
        getSupportActionBar().setTitle(playlist.getName());
    }

    @Override
    public void onPlaySong(Track track)
    {
        playSong(track, 0);
    }

    private void playSong(Track track, int trackTimeToStartAt)
    {
        this.track = track;

        if (track.getUrl() != null)
        {
            String link = track.getUrl();
            playSongFromUrl(link, trackTimeToStartAt);
        }
        else
        {
            isPlaylistMaster = false;
            playPauseButton.setVisibility(View.GONE);
        }

        lastPlaybackType = PlaybackType.PLAYLIST;

        Picasso.with(this)
                .load(track.getCoverArtUrl())
                .error(R.drawable.default_cover)
                .into(coverArt, PicassoPalette.with(track.getCoverArtUrl(), coverArt)
                                .intoCallBack(
                                        new PicassoPalette.CallBack()
                                        {
                                            @Override
                                            public void onPaletteLoaded(android.support.v7.graphics.Palette palette)
                                            {
                                                songProgress.getProgressDrawable().setColorFilter(palette.getVibrantColor(Color.BLACK), PorterDuff.Mode.SRC_IN);
                                            }
                                        })
                );
        songTitle.setText(track.getTitle());
        songArtist.setText(track.getArtist());
        songTimeLeft.setText("-" + DateUtil.secondsToFormattedString(track.getDuration() / 1000));
        songProgress.setMax(track.getDuration());

        onQueueRefreshRequested();
    }

    @Override
    public void onException(String message) {
        Snackbar.make(viewPager, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onQueueRefreshRequested() {
        presenter.loadPlaylist(playlistId);
    }

    @Override
    public void onTrackAddedCallback()
    {
        //Sends the add track event to everyone through SignalR
        signalr.addTrack();
        onQueueRefreshRequested();
    }

    @OnClick(R.id.musicplayer_play_pause) void onClickPlayPause()
    {
        if (videoView.isPlaying())
        {
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_24dp);
            videoView.pause();
            if(lastPlaybackType == PlaybackType.PLAYLIST) signalr.pausePlaying();
        }
        else
        {
            playPauseButton.setImageResource(R.drawable.ic_pause_24dp);

            if(lastPlaybackType == PlaybackType.PLAYLIST){
                songLoading.setVisibility(View.VISIBLE);
                if (!isPlaylistMaster)
                {
                    // First time playing, fetching song, this guy will become playlist master
                    presenter.playNextSong(playlistId);
                    isPlaylistMaster = true;
                }
                else
                {
                    presenter.playNextSong(playlistId);
                    signalr.resumePlaying(videoView.getCurrentPosition() / 1000);
                }
            } else {
                videoView.start();
                songProgress.postDelayed(updateProgressBarThread, 1000);
            }
        }
    }

    private Runnable updateProgressBarThread;
    private void playSongFromUrl(final String url, final int trackTimeToStartAt)
    {
        songLoading.setVisibility(View.VISIBLE);

        try {
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            final Uri video = Uri.parse(url);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener()
            {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra)
                {
                    new MaterialDialog.Builder(PlaylistActivity.this)
                            .title("Error loading track")
                            .content("BeatBuddy can't play the current track. Your internet connection timed out, bad KdG WiFi!\n\nErrorcode: " + what)
                            .positiveText("Retry")
                            .onPositive(new MaterialDialog.SingleButtonCallback()
                            {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                                {
                                    playSongFromUrl(url, trackTimeToStartAt);
                                }
                            })
                            .negativeText("Close")
                            .show();
                    songLoading.setVisibility(View.GONE);
                    return true;
                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {
                @Override
                public void onPrepared(MediaPlayer mp)
                {
                    songLoading.setVisibility(View.GONE);

                    if (trackTimeToStartAt != 0)
                        videoView.seekTo(trackTimeToStartAt * 1000);

                    videoView.start();

                    if (lastPlaybackType == PlaybackType.PLAYLIST && isPlaylistMaster)
                        signalr.startPlaying(track.getArtist(), track.getCoverArtUrl(), playlist.getPlaylistTracks().size(), track.getTitle(), track.getTrackSource().getTrackId());

                    //This will update the progress bar
                    updateProgressBarThread = new Runnable()
                    {
                        public void run()
                        {
                            if (songProgress != null)
                            {
                                int progress = videoView.getCurrentPosition() / 1000;
                                songProgress.setProgress(progress);
                                songTimeLeft.setText("-" + DateUtil.secondsToFormattedString((videoView.getDuration() - videoView.getCurrentPosition()) / 1000));
                            }
                            if (videoView.isPlaying())
                            {
                                assert songProgress != null;
                                songProgress.postDelayed(updateProgressBarThread, 1000);
                            }
                        }
                    };
                    songProgress.postDelayed(updateProgressBarThread, 1000);
                }
            });
            videoView.setOnCompletionListener(this);
            videoView.requestFocus();
            playPauseButton.setImageResource(R.drawable.ic_pause_24dp);
        } catch (Exception e) {
            Snackbar.make(playPauseButton, "Error playing music: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTrackAdded()
    {
        onQueueRefreshRequested();
    }

    @Override
    public void onStopMusic()
    {
        //TODO: check if should show play button
        playPauseButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlayLive(final Track track, int currentTrackProgress)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onPlaySong(track);
            }
        });
    }

    @Override
    public void onNewTrackPlaying(final Track track)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onPlaySong(track);
            }
        });
    }

    @Override
    public void onPlaylinkFetched(final String playlink)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                playSongFromUrl(playlink, 0);
            }
        });
    }

    @Override
    public void onErrorConnecting(String errorMessage)
    {
        new MaterialDialog.Builder(this)
                .title("Error connectiong")
                .content(errorMessage)
                .positiveText("Retry")
                .onPositive(new MaterialDialog.SingleButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                    {
                        setupSignalR();
                    }
                })
                .negativeText("Close")
                .show();
    }

    @Override
    public void onHistoryTrackClicked(Track track, int position) {
        lastPlaybackType = PlaybackType.HISTORY;
        historyPosition = position;

        presenter.getPlaybackTrack(track.getId());
        if(videoView.isPlaying()) videoView.stopPlayback();

        Picasso.with(this)
                .load(track.getCoverArtUrl())
                .error(R.drawable.default_cover)
                .into(coverArt, PicassoPalette.with(track.getCoverArtUrl(), coverArt)
                                .intoCallBack(
                                        new PicassoPalette.CallBack()
                                        {
                                            @Override
                                            public void onPaletteLoaded(android.support.v7.graphics.Palette palette)
                                            {
                                                songProgress.getProgressDrawable().setColorFilter(palette.getVibrantColor(Color.BLACK), PorterDuff.Mode.SRC_IN);
                                            }
                                        })
                );
        songTitle.setText(track.getTitle());
        songArtist.setText(track.getArtist());
        songTimeLeft.setText("-" + DateUtil.secondsToFormattedString(track.getDuration() / 1000));
        songProgress.setMax(track.getDuration());

        playPauseButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlaybackTrackLoaded(Track track) {
        if (track.getUrl() != null)
        {
            String link = track.getUrl();
            playSongFromUrl(link, 0);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        switch(lastPlaybackType){
            case PLAYLIST:
                if (isPlaylistMaster)
                    presenter.playNextSong(playlistId);
                break;
            case HISTORY:
                int nextTrackPosition = historyPosition + 1;
                List<PlaylistTrack> history = historyFragment.getHistory();
                if(nextTrackPosition >= history.size()) return;

                Track nextTrack = history.get(nextTrackPosition).getTrack();
                onHistoryTrackClicked(nextTrack, nextTrackPosition);
                break;
        }
    }
}
