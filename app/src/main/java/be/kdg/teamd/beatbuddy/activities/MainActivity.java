package be.kdg.teamd.beatbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import be.kdg.teamd.beatbuddy.BeatBuddyApplication;
import be.kdg.teamd.beatbuddy.R;
import be.kdg.teamd.beatbuddy.UserConfigurationManager;
import be.kdg.teamd.beatbuddy.model.users.User;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int RESULT_LOGIN = 1;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.nav_view) NavigationView navigationView;
    @Bind(R.id.drawer_layout) DrawerLayout drawer;

    private UserConfigurationManager userConfigurationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle("BeatBuddy");

        userConfigurationManager = (BeatBuddyApplication) getApplication();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RESULT_LOGIN && resultCode == RESULT_OK)
        {
            //Login is successful (aw yeah!), now put the user data in the navigation drawer!
            User user = userConfigurationManager.getUser();
            bindUserToNavigationView(user);

            navigationView.getMenu().setGroupVisible(R.id.group_guest, false);
            navigationView.getMenu().setGroupVisible(R.id.group_logged_in, true);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.nav_login:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, RESULT_LOGIN);
                break;
            case R.id.nav_logout:
                userConfigurationManager.setUser(null);
                userConfigurationManager.setAccessToken(null);
                User user = userConfigurationManager.getUser();
                bindUserToNavigationView(user);

                navigationView.getMenu().setGroupVisible(R.id.group_logged_in, false);
                navigationView.getMenu().setGroupVisible(R.id.group_guest, true);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.main_fab_create_playlist) void onClickOrganisationsFab()
    {
        startActivity(new Intent(this, CreateOrganisationActivity.class));
    }

    private void bindUserToNavigationView(User user)
    {
        View headerView = navigationView.getHeaderView(0);
        CircleImageView userAvatar = ButterKnife.findById(headerView, R.id.header_profile_image);
        TextView username = ButterKnife.findById(headerView, R.id.header_name);
        TextView subname = ButterKnife.findById(headerView, R.id.header_subname);

        if (user == null)
        {
            userAvatar.setImageResource(R.mipmap.ic_launcher);
            username.setText("Guest");
            subname.setText("Not logged in");
        }
        else
        {
            Picasso.with(this)
                    .load(user.getImageUrl())
                    .into(userAvatar);
            username.setText(user.getNickname());
            subname.setText(user.getFirstName() + " " + user.getLastName());
        }
    }
}
