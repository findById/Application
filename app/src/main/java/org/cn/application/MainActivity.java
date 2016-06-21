package org.cn.application;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.cn.application.ui.activity.AboutActivity;
import org.cn.application.ui.suspension.FloatActivity;
import org.cn.application.ui.tab.TabActivity;
import org.cn.core.permission.PermissionDenied;
import org.cn.core.permission.PermissionGranted;
import org.cn.core.permission.PermissionDispatcher;
import org.cn.core.utils.LocateManager;
import org.cn.push.Log;

import java.util.ArrayList;

public class MainActivity extends SmartActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPermission();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();
        initData();
    }

    private void initView() {
    }

    private void initData() {
    }

    public void initPermission() {
        ArrayList<String> per = new ArrayList<>();
        per.add(Manifest.permission.ACCESS_FINE_LOCATION);
        per.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        per.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        per.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        PermissionDispatcher.requestPermissions(this, "1:网络定位权限\n2:SD卡读写权限", REQUEST_LOCATION, per.toArray(new String[per.size()]));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed();
            super.supportFinishAfterTransition();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView sv = (SearchView) menu.findItem(R.id.action_search).getActionView();
        buildSearchView(sv);
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
            return true;
        }
        switch (id) {
            case R.id.action_search: {
                break;
            }
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buildSearchView(SearchView searchView) {
        if (searchView == null) {
            return;
        }
        searchView.setQueryHint("search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // // close drawer layout first
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onNavigationChanged(id);
            }
        }, 300);

        return true;
    }

    /**
     * Handle navigation view item clicks here.
     *
     * @param id Handle the @id action
     * @return
     */
    private boolean onNavigationChanged(int id) {

        String tag = "";
        switch (id) {
            case R.id.nav_tab: {
                startActivity(new Intent(this, TabActivity.class));
                return true;
            }
            case R.id.nav_about: {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }
            case R.id.nav_camera: {
                PermissionDispatcher.requestPermissions(this, "This app needs access to your camera so you can take pictures.", REQUEST_CAMERA, Manifest.permission_group.CAMERA);
                break;
            }
            default:
                // ignored
                startActivity(new Intent(this, FloatActivity.class));
                break;
        }

        onChangeFragment(tag);
        return true;
    }

    public static final int REQUEST_CAMERA = 0;
    public static final int REQUEST_LOCATION = 1;
    @PermissionGranted
    protected void onPermissionsGranted(int requestCode) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                Log.d("Permission", "Camera permission has now been granted.");
                break;
            }
            case REQUEST_LOCATION: {
                Log.d("Permission", "Location permission has now been granted.");
                LocateManager.getLocation(MainActivity.this);
                break;
            }
            default:
                break;
        }
    }

    @PermissionDenied
    protected void onPermissionsDenied(int requestCode) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                Log.d("Permission", "Camera permission was NOT granted.");
                break;
            }
            case REQUEST_LOCATION: {
                Log.d("Permission", "Location permission was NOT granted.");
                break;
            }
            default:
                break;
        }
    }
}
