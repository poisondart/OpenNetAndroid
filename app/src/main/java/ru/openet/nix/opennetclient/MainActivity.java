package ru.openet.nix.opennetclient;

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
        mFragment = new MainNewsFragment();
        mFragmentManager.beginTransaction()
                .add(R.id.main_view, mFragment)
                .commit();
        final NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
                mDrawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (item.getItemId()){
                            case R.id.main_item:
                                setFragment(new MainNewsFragment());
                                break;
                        }
                    }
                }, 250);
                return true;
            }
        });
    }

    private void setFragment(Fragment fragment){
        mFragmentManager.beginTransaction()
                .remove(mFragment)
                .commit();
        mFragment = fragment;
        mFragmentManager.beginTransaction()
                .add(R.id.main_view, mFragment)
                .commit();
    }
}
