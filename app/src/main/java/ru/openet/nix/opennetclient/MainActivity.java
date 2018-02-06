package ru.openet.nix.opennetclient;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private FragmentManager mFragmentManager;
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = findViewById(R.id.drawerlayout);
        mFragmentManager = getSupportFragmentManager();
        mFragment = new BasicNewsFragment();
        setupDefaultFragment();
        final NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setCheckedItem(R.id.main_item);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
                mDrawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (item.getItemId()){
                            case R.id.main_item:
                                loadNewsFragment();
                                break;
                            case R.id.favs_item:
                                loadFavsFragment();
                                break;
                            case R.id.pref_item:
                                loadSettingsFragment();
                                break;
                        }
                    }
                }, 250);
                return true;
            }
        });
    }
    private void setupDefaultFragment(){
        mFragmentManager.beginTransaction().add(R.id.main_view, mFragment).commit();
    }
    private void loadNewsFragment(){
        mFragmentManager.beginTransaction().remove(mFragment).commit();
        mFragment = new BasicNewsFragment();
        mFragmentManager.beginTransaction().add(R.id.main_view, mFragment).commit();
    }
    private void loadFavsFragment(){
        mFragmentManager.beginTransaction().remove(mFragment).commit();
        mFragment = new SavedArticlesFragment();
        mFragmentManager.beginTransaction().add(R.id.main_view, mFragment).commit();
    }
    private void loadSettingsFragment(){
        Intent intent = new Intent(MainActivity.this, SettingsHolderActivity.class);
        startActivity(intent);
    }
}
