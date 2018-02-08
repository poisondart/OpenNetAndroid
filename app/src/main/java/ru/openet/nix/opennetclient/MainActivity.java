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
import android.view.View;

public class MainActivity extends AppCompatActivity implements BasicNewsFragment.Callbacks,
        SavedArticlesFragment.RealmCallbacks{

    private DrawerLayout mDrawerLayout;
    //private FragmentManager mFragmentManager;
    private Fragment mFragment;

    public static final String TITLE_TAG = "title";
    public static final String LINK_TAG = "link";
    private boolean mIsDualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masterdetail);
        View articleView = findViewById(R.id.detail_view);
        mIsDualPane = articleView != null &&
                articleView.getVisibility() == View.VISIBLE;
        mDrawerLayout = findViewById(R.id.drawerlayout);
        //mFragmentManager = getSupportFragmentManager();
        if(savedInstanceState != null){
            mFragment = getSupportFragmentManager().getFragment(savedInstanceState, "name");
        }else{
            setupDefaultFragment();
        }
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
                                updateFragment("Главные новости", Links.MAIN_NEWS_RSS_LINK);
                                break;
                            case R.id.security_item:
                                updateFragment("Проблемы безопасности", Links.MAIN_SECURITY_PROB_RSS_LINK);
                                break;
                            case R.id.updates_item:
                                updateFragment("Новые версии ПО", Links.MAIN_NEW_SOFT_PROB_RSS_LINK);
                                break;
                            case R.id.linux_item:
                                updateFragment("Linux", Links.MAIN_LINUX_RSS_LINK);
                                break;
                            case R.id.bsd_item:
                                updateFragment("BSD", Links.MAIN_BSD_RSS_LINK);
                                break;
                            case R.id.ubuntu_item:
                                updateFragment("Ubuntu", Links.UBUNTU_NEWS_RSS_LINK);
                                break;
                            case R.id.fedora_item:
                                updateFragment("Fedora", Links.MAIN_FEDORA_RSS_LINK);
                                break;
                            case R.id.mozilla_item:
                                updateFragment("Mozilla/Firefox", Links.MAIN_MOZILLA_FIREFOX_RSS_LINK);
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
    private void updateFragment(String title, String link){
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        Fragment articleFragment = getSupportFragmentManager().findFragmentByTag("article");
        if(articleFragment != null) getSupportFragmentManager().beginTransaction().remove(articleFragment).commit();
        mFragment = new BasicNewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_TAG, title);
        bundle.putString(LINK_TAG, link);
        mFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.main_view, mFragment).commit();
    }
    private void setupDefaultFragment(){
        mFragment = new BasicNewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_TAG, "Главные новости");
        bundle.putString(LINK_TAG, Links.MAIN_NEWS_RSS_LINK);
        mFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.main_view, mFragment).commit();
    }
    private void loadFavsFragment(){
        getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        Fragment articleFragment = getSupportFragmentManager().findFragmentByTag("article");
        if(articleFragment != null) getSupportFragmentManager().beginTransaction().remove(articleFragment).commit();
        mFragment = new SavedArticlesFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.main_view, mFragment).commit();
    }
    private void loadSettingsFragment(){
        Intent intent = new Intent(MainActivity.this, SettingsHolderActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "name", mFragment);
    }

    @Override
    public void onItemSelected(NewsItem item) {
        if(mIsDualPane){
            ArticleFragment articleFragment = ArticleFragment.newInstance(item.getDate(),
                    item.getTitle(), item.getLink());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_view, articleFragment, "article").commit();
        }else{
            Intent intent = ArticleHostActivity.newInstance(MainActivity.this,
                    item.getTitle(),
                    item.getLink(),
                    item.getDate());
            startActivity(intent);
        }
    }

    @Override
    public void onArticleSelected(Article article) {
        if(mIsDualPane){
            ArticleFragment articleFragment = ArticleFragment.newInstance(article.getDate(),
                    article.getTitle(), article.getLink());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_view, articleFragment, "article").commit();
        }else{
            Intent intent = ArticleHostActivity.newInstance(MainActivity.this,
                    article.getTitle(),
                    article.getLink(),
                    article.getDate());
            startActivity(intent);
        }
    }
}
