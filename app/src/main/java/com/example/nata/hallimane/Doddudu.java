package com.example.nata.hallimane;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

public class Doddudu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    long backPressedTime=0;
    SessionManager session;
    String tempEmail,tempPassword;
    TextView LoggedInIdTextMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doddudu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        View BackgroundImage = findViewById(R.id.content_doddudu);
        Drawable background = BackgroundImage.getBackground();
        background.setAlpha(180);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        session = new SessionManager(Doddudu.this);
        HashMap<String,String> user = session.getUserDetails();
        tempEmail =user.get(SessionManager.KEY_EMAIL);
        tempPassword = user.get(SessionManager.KEY_PASSWORD);

        View header = navigationView.getHeaderView(0);
        LoggedInIdTextMail= (TextView)header.findViewById(R.id.LoggedInId);
        LoggedInIdTextMail.setText(tempEmail);
        //Toast.makeText(Doddudu.this,tempEmail.toString().trim(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            //Toast.makeText(this, "Press one more time to exit", Toast.LENGTH_SHORT).show();
            moveTaskToBack(true);
        } else {    // this guy is serious
            // clean up
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.doddudu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            session.logoutUser();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            return true;
        }
        if (id==R.id.ChangePassword){

            ChanegPasswordFragment chanegPasswordFragment =  ChanegPasswordFragment.newInstance(tempEmail,tempPassword);
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.content_doddudu,
                    chanegPasswordFragment,
                    chanegPasswordFragment.getTag()).commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            RoomAvailabilityFragment roomAvailabilityFragment =new RoomAvailabilityFragment();
            //.newInstance(tempEmail);
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.content_doddudu,
                    roomAvailabilityFragment,
                    roomAvailabilityFragment.getTag()).commit();
        }
        else if (id == R.id.nav_gallery) {
            RoomChargesFragment roomChargesFragment = new RoomChargesFragment();
            FragmentManager manager =getFragmentManager();
            manager.beginTransaction().replace(R.id.content_doddudu,
                    roomChargesFragment,
                    roomChargesFragment.getTag()).commit();
        } else if (id == R.id.nav_slideshow) {

            RoomBookedFragment  roomBookedFragment = new RoomBookedFragment();
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.content_doddudu,
                    roomBookedFragment,
                    roomBookedFragment.getTag()).commit();

        } else if (id == R.id.nav_manage) {

            FeedBackFragment feedBackFragment = new FeedBackFragment();
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.content_doddudu,
                    feedBackFragment,
                    feedBackFragment.getTag()).commit();
        } else if (id == R.id.nav_share) {
            try {
                PackageManager pm = getPackageManager();
                ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
                File srcFile = new File(ai.publicSourceDir);
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/vnd.android.package-archive");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(srcFile));
                startActivity(Intent.createChooser(share, "Halli Mane"));
            } catch (Exception e) {
                Log.e("ShareApp", e.getMessage());
            }
        } else if (id == R.id.nav_send) {
            session.logoutUser();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }else if(id ==R.id.nav_updatePassword){
            ChanegPasswordFragment chanegPasswordFragment =  ChanegPasswordFragment.newInstance(tempEmail,tempPassword);
            FragmentManager manager =getFragmentManager();
            manager.beginTransaction().replace(R.id.content_doddudu,
                    chanegPasswordFragment,
                    chanegPasswordFragment.getTag()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
