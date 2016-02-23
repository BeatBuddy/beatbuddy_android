package be.kdg.teamd.beatbuddy.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.adapters.PlaylistTabAdapter;
import be.kdg.teamd.beatbuddy.fragments.PlaylistFragment;
import butterknife.Bind;
import butterknife.ButterKnife;

public class PlaylistActivity extends AppCompatActivity
{
    public static final String EXTRA_PLAYLIST_KEY = "KEY";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tablayout) TabLayout tabLayout;
    @Bind(R.id.playlist_viewpager) ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        ButterKnife.bind(this);

        String playlistKey = getIntent().getStringExtra(EXTRA_PLAYLIST_KEY);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(playlistKey);

        setupViewPager(viewPager);
    }

    private void setupViewPager(final ViewPager viewPager)
    {
        PlaylistTabAdapter adapter = new PlaylistTabAdapter(getSupportFragmentManager());
        adapter.addFrag(PlaylistFragment.newInstance("lala"), "Playlist");
        adapter.addFrag(PlaylistFragment.newInstance("lala"), "Chat");

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
}
