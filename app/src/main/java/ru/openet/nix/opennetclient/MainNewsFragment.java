package ru.openet.nix.opennetclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Nix on 02.01.2018.
 */

public class MainNewsFragment extends Fragment {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sample_fragment, container, false);
        mToolbar = v.findViewById(R.id.toolbar);
        mToolbar.setTitle("Главные новости");
        /*способ взят по ссылке https://stackoverflow.com/q/35015182*/
        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(mToolbar);
        mDrawerLayout = actionBar.findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, mToolbar, R.string.app_name,
                R.string.app_name);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        return v;
    }
}
