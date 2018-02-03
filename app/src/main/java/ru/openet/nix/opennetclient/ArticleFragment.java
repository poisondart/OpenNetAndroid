package ru.openet.nix.opennetclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import io.realm.Realm;

/**
 * Created by Nix on 28.01.2018.
 */

public class ArticleFragment extends Fragment {

    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private Article mArticle, mArticleCache;
    private ArrayList<ArticlePart> mArticleParts;
    private List mArticlePartsCache;
    private ArticleRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private AppCompatActivity mActionBar;
    private Realm mRealm;

    private String mArticleTitle, mArticleDate, mArticleLink;

    private static final String ARG_ARTICLE_DATE = "article_date";
    private static final String ARG_ARTICLE_TITLE = "article_title";
    private static final String ARG_ARTICLE_LINK = "article_link";

    private boolean mSaved;

    public static ArticleFragment newInstance(String date, String title, String link){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_ARTICLE_DATE, date);
        bundle.putSerializable(ARG_ARTICLE_TITLE, title);
        bundle.putSerializable(ARG_ARTICLE_LINK, link);
        ArticleFragment articleFragment = new ArticleFragment();
        articleFragment.setArguments(bundle);
        return articleFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticleTitle = (String) getArguments().getSerializable(ARG_ARTICLE_TITLE);
        mArticleDate = (String)getArguments().getSerializable(ARG_ARTICLE_DATE);
        mArticleLink = (String)getArguments().getSerializable(ARG_ARTICLE_LINK);
        setHasOptionsMenu(true);
        Realm.init(getContext());
        mRealm = Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.article_fragment, container, false);
        mToolbar = v.findViewById(R.id.toolbar_article);
        mProgressBar = v.findViewById(R.id.progressbar_article);
        mArticleParts = new ArrayList<>();
        mArticlePartsCache = new ArrayList<>();
        mProgressBar.setMax(100);
        setHasOptionsMenu(true);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = v.findViewById(R.id.article_recyclerview);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mArticle = new Article(mArticleDate, mArticleTitle, mArticleLink);
        mActionBar = (AppCompatActivity) getActivity();
        mActionBar.setSupportActionBar(mToolbar);
        if(mActionBar.getSupportActionBar() != null){
            mActionBar.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mActionBar.getSupportActionBar().setDisplayShowHomeEnabled(true);
        if(mArticleDate != null){
            mActionBar.setTitle(mArticleDate);
        }else {
            mActionBar.setTitle("...");
        }

        if(!checkArticleInRealm(mArticleLink)){
            new FetchPartsTask(mArticleLink, this).execute();
        }
        mAdapter = new ArticleRecyclerViewAdapter(getContext(), mArticleTitle, mArticleParts);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.article_menu, menu);
        if(mSaved){
            menu.getItem(0).setIcon(R.drawable.ic_favorited);
        }else{
            menu.getItem(0).setIcon(R.drawable.ic_not_favorited);
        }
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(!mSaved){
                    Toast.makeText(getContext(), R.string.added_to_favs, Toast.LENGTH_SHORT).show();
                    menuItem.setIcon(getResources().getDrawable(R.drawable.ic_favorited));
                    addArticleToRealm();
                }else{
                    Toast.makeText(getContext(), R.string.deleted_from_favs, Toast.LENGTH_SHORT).show();
                    menuItem.setIcon(getResources().getDrawable(R.drawable.ic_not_favorited));
                    deleteArticleFromRealm();
                }
                mSaved = !mSaved;
                return true;
            }
        });
        menu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //Toast.makeText(getContext(), R.string.share_button_hint, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, mArticleLink);
                i.putExtra(Intent.EXTRA_SUBJECT, mArticleTitle);
                startActivity(Intent.createChooser(i, getString(R.string.share_link_hint)));
                return true;
            }
        });
    }

    private static class FetchPartsTask extends AsyncTask<Integer, Integer, Integer>{
        private Document mDocument;
        private Element mElement, mElementExtraLink, mElementDate;
        private Elements mChilds, mExtraChilds;
        private String mLink;
        private WeakReference<ArticleFragment> mReference;

        public FetchPartsTask(String link, ArticleFragment reference) {
            mLink = link;
            mReference = new WeakReference<>(reference);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ArticleFragment fragment = mReference.get();
            if (fragment == null) return;
            fragment.mProgressBar.setVisibility(View.VISIBLE);
            fragment.mProgressBar.setProgress(0);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            ArticleFragment fragment = mReference.get();
            if (fragment == null) return;
            if (integer == 0){
                Toast.makeText(fragment.getContext(), "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
                return;
            }
            fragment.mAdapter.setParts(fragment.mArticleParts);
            fragment.mAdapter.notifyDataSetChanged();
            fragment.mProgressBar.setProgress(100);
            fragment.mProgressBar.setVisibility(View.GONE);
            if(fragment.mArticleDate == null){
                fragment.mArticleDate = mElementDate.text();
                fragment.mActionBar.setTitle(fragment.mArticleDate);
                fragment.mArticle.setDate(fragment.mArticleDate);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            ArticleFragment fragment = mReference.get();
            if (fragment == null) return;
            fragment.mProgressBar.setProgress(values[0]);
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            int size = 0;
            ArticleFragment fragment = mReference.get();
            try{
                mDocument = Jsoup.connect(mLink).get();
                mElement = mDocument.select("td[class = chtext]").first();
                mElementExtraLink = mDocument.select("ol").first();
                mChilds = mElement.getAllElements();
                mExtraChilds = mElementExtraLink.getAllElements();
                if(fragment.mArticleDate == null){
                    mElementDate = mDocument.select("font").first();
                }
                for(Element e : mChilds){
                    if(e.tagName().equals("p")){
                        ArticlePart articlePart = new ArticlePart(ArticlePart.SIMPLE_TEXT, e.html(), mLink);
                        fragment.mArticleParts.add(articlePart);
                    }else if(e.tagName().equals("li")){
                        ArticlePart articlePart = new ArticlePart(ArticlePart.LIST_ITEM, e.html(), mLink);
                        fragment.mArticleParts.add(articlePart);
                    }else if(e.tagName().equals("pre")){
                        ArticlePart articlePart = new ArticlePart(ArticlePart.CODE, e.text(), mLink);
                        fragment.mArticleParts.add(articlePart);
                    }else if(e.tagName().equals("img")){
                        ArticlePart articlePart = new ArticlePart(ArticlePart.IMAGE, e.attr("src"), mLink);
                        fragment.mArticleParts.add(articlePart);
                    }
                    publishProgress(mChilds.size());
                }
                for(Element e : mExtraChilds){
                    if(e.tagName().equals("a")){
                        fragment.mArticleParts.add(new ArticlePart(ArticlePart.ETRA_LINKS_ITEM,
                                e.text(), e.attr("href"), mLink));
                    }
                }
                publishProgress(mExtraChilds.size());
                size = mChilds.size();
            }catch (IOException e){
                e.printStackTrace();
            }
            return size;
        }
    }
    @ParametersAreNonnullByDefault
    private void addArticleToRealm(){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if(!mArticle.isValid()){
                    Log.d("Not valid ", "kek");
                    mArticle = new Article(mArticleCache);
                    mArticleParts.clear();
                    mArticleParts.addAll(mArticlePartsCache);
                    realm.copyToRealmOrUpdate(mArticle);
                    realm.copyToRealm(mArticleParts);
                }else{
                    realm.copyToRealmOrUpdate(mArticle);
                    realm.copyToRealm(mArticleParts);
                }
            }
        });
    }
    @ParametersAreNonnullByDefault
    private void deleteArticleFromRealm(){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Article.class).equalTo(Article.LINK, mArticle.getLink()).findAll().deleteAllFromRealm();
                realm.where(ArticlePart.class).equalTo(ArticlePart.ARTICLE_LINK, mArticleLink)
                        .findAll().deleteAllFromRealm();
            }
        });
    }
    @ParametersAreNonnullByDefault
    private boolean checkArticleInRealm(final String articleLink){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if(realm.where(Article.class).equalTo(Article.LINK, articleLink).findAll().isEmpty()){
                    mSaved = false;
                }else {
                    mArticle = realm.where(Article.class).equalTo(Article.LINK, articleLink).findAll().first();
                    mArticleDate = mArticle.getDate();
                    mArticleTitle = mArticle.getTitle();
                    mArticleLink = mArticle.getLink();
                    mArticleParts.addAll(realm.where(ArticlePart.class)
                            .equalTo(ArticlePart.ARTICLE_LINK, articleLink).findAll());
                    mArticleCache = realm.copyFromRealm(mArticle);
                    mArticlePartsCache = realm.copyFromRealm(mArticleParts);
                    mSaved = true;
                }
            }
        });
        return mSaved;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
