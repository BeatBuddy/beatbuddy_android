package be.kdg.teamd.beatbuddy.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import be.kdg.teamd.beatbuddy.BeatBuddyApplication;
import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.adapters.PlaylistTabAdapter;
import be.kdg.teamd.beatbuddy.dal.PlaylistRepository;
import be.kdg.teamd.beatbuddy.dal.RepositoryFactory;
import be.kdg.teamd.beatbuddy.fragments.ChatFragment;
import be.kdg.teamd.beatbuddy.fragments.QueueFragment;
import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.model.users.User;
import be.kdg.teamd.beatbuddy.presenter.PlaylistPresenter;
import be.kdg.teamd.beatbuddy.userconfiguration.UserConfigurationManager;
import be.kdg.teamd.beatbuddy.util.DateUtil;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaylistActivity extends AppCompatActivity implements PlaylistPresenter.PlaylistPresenterListener, QueueFragment.QueueFragmentListener {
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

    private UserConfigurationManager userConfigurationManager;
    private PlaylistRepository playlistRepository;
    private PlaylistPresenter presenter;

    private QueueFragment queueFragment;
    private ChatFragment chatFragment;

    private long playlistId;
    private Playlist playlist;

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

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(playlistKey);

        setupViewPager(viewPager);
        if(!isTesting) presenter.loadPlaylist(playlistId); // TODO: join by key, not by ID
    }

    private void setupViewPager(final ViewPager viewPager)
    {
        queueFragment = new QueueFragment();
        queueFragment.setListener(this);
        Bundle arguments = new Bundle();
        arguments.putLong(QueueFragment.ARG_PLAYLIST_ID, playlistId);
        queueFragment.setArguments(arguments);

        chatFragment = new ChatFragment();

        PlaylistTabAdapter adapter = new PlaylistTabAdapter(getSupportFragmentManager());
        adapter.addFrag(queueFragment, "Queue");
        adapter.addFrag(chatFragment, "Chat");

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
        String link = track.getTrackSource().getUrl();
        playSongFromUrl(link);

        Picasso.with(this)
                .load(track.getCoverArtUrl())
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
        songTimeLeft.setText("-" + DateUtil.secondsToFormattedString((videoView.getDuration() - videoView.getCurrentPosition()) / 1000));
        songProgress.setMax(track.getDuration());

        //Temp fix: should get pushed by SinglR
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

    @OnClick(R.id.musicplayer_play_pause) void onClickPlayPause()
    {
        if (videoView.isPlaying())
        {
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_24dp);
            videoView.pause();
        }
        else
        {
            playPauseButton.setImageResource(R.drawable.ic_pause_24dp);
            presenter.playNextSong(playlistId);
        }
    }

    private Runnable updateProgressBarThread;
    private void playSongFromUrl(String url)
    {
        try {
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            final Uri video = Uri.parse(url);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {
                @Override
                public void onPrepared(MediaPlayer mp)
                {
                    videoView.start();

                    //This will update the progress bar
                    updateProgressBarThread = new Runnable() {
                        public void run() {
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
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    presenter.playNextSong(playlistId);
                }
            });
            videoView.requestFocus();
        } catch (Exception e) {
            Snackbar.make(playPauseButton, "Error playing music", Snackbar.LENGTH_SHORT).show();
        }
    }
}
