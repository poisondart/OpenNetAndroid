package ru.openet.nix.opennetclient;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if(direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.proof_des);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAdapter.notifyItemRemoved(position);
                            mRealm.executeTransaction(new Realm.Transaction() {
                                @ParametersAreNonnullByDefault
                                @Override
                                public void execute(Realm realm) {
                                    mAdapter.notifyItemRemoved(position);
                                    String artlink = mArticles.get(position).getLink();
                                    realm.where(Article.class).equalTo(Article.LINK,
                                            artlink).findAll().deleteAllFromRealm();
                                    realm.where(ArticlePart.class).equalTo(ArticlePart.ARTICLE_LINK, artlink)
                                            .findAll().deleteAllFromRealm();
                                }
                            });
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAdapter.notifyItemRemoved(position + 1);
                            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                        }
                    }).show();
                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
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
