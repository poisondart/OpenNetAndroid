package ru.openet.nix.opennetclient;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;
    private Fragment mFragment;
    private BottomNavigationViewEx mBottomNavigationViewEx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBottomNavigationViewEx = findViewById(R.id.bottom_navigation);
        mFragmentManager = getSupportFragmentManager();
        mFragment = new BasicNewsFragment();
        setupDefaultFragment();
        mBottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.main_item:
                        loadNewsFragment();
                        break;
                    case R.id.board_item:
                        loadBoardFragment();
                        break;
                    case R.id.favs_item:
                        loadFavsFragment();
                        break;
                    case R.id.pref_item:
                        loadSettingsFragment();
                }
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
        mFragmentManager.beginTransaction().remove(mFragment).commit();
        mFragment = new SettingsFragment();
        mFragmentManager.beginTransaction().add(R.id.main_view, mFragment).commit();
    }
    private void loadBoardFragment(){
        mFragmentManager.beginTransaction().remove(mFragment).commit();
        mFragment = new BoardFragment();
        mFragmentManager.beginTransaction().add(R.id.main_view, mFragment).commit();
    }
}
