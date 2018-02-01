package ru.openet.nix.opennetclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import javax.annotation.ParametersAreNonnullByDefault;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Nix on 09.01.2018.
 */

public class SavedArticlesFragment extends Fragment {
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private RealmResults<Article> mArticles;
    private RecyclerView mRecyclerView;
    private SavedArticleAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private Realm mRealm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Realm.init(getContext());
        mRealm = Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.favs_fragment_layout, container, false);
        mToolbar = v.findViewById(R.id.toolbar_favs);
        mRecyclerView = v.findViewById(R.id.saved_recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mToolbar.setTitle(getString(R.string.favs));
        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(mToolbar);
        mDrawerLayout = actionBar.findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, mToolbar, R.string.app_name,
                R.string.app_name);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mArticles = getArticles();
        mAdapter = new SavedArticleAdapter(mArticles);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.favs_delete_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_favs:
                deleteAllArticles();
        }
        return super.onOptionsItemSelected(item);
    }

    private RealmResults<Article> getArticles(){
        return mRealm.where(Article.class).findAll();
    }
    @ParametersAreNonnullByDefault
    private void deleteAllArticles(){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (realm.isEmpty()){
                    Toast.makeText(getContext(), R.string.all_articles_already_deleted, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), R.string.toast_all_deteted, Toast.LENGTH_SHORT).show();
                    realm.deleteAll();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
