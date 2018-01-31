package ru.openet.nix.opennetclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.ProgressBar;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Nix on 28.01.2018.
 */

public class ArticleFragment extends Fragment {

    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private Article mArticle;
    private ArrayList<ArticlePart> mArticleParts;
    private ArticleRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private String mArticleTitle, mArticleDate, mArticleLink;

    private static final String ARG_ARTICLE_DATE = "article_date";
    private static final String ARG_ARTICLE_TITLE = "article_title";
    private static final String ARG_ARTICLE_LINK = "article_link";

    private boolean mSaved = false;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.article_fragment, container, false);
        mToolbar = v.findViewById(R.id.toolbar_article);
        mProgressBar = v.findViewById(R.id.progressbar_article);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = v.findViewById(R.id.article_recyclerview);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mArticleParts = new ArrayList<>();
        mArticle = new Article(mArticleDate, mArticleTitle, mArticleLink);
        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(mToolbar);
        if(actionBar.getSupportActionBar() != null){
            actionBar.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        actionBar.getSupportActionBar().setDisplayShowHomeEnabled(true);
        actionBar.setTitle(mArticleDate);
        mAdapter = new ArticleRecyclerViewAdapter(getContext(), mArticleTitle, mArticleParts);
        mRecyclerView.setAdapter(mAdapter);
        new FetchPartsTask(mArticleLink, this).execute();
        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.article_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
                Toast.makeText(getContext(), R.string.share_button_hint, Toast.LENGTH_SHORT).show();
                break;
            case R.id.star:
                if(!mSaved){
                    mSaved = !mSaved;
                    Toast.makeText(getContext(), R.string.added_to_favs, Toast.LENGTH_SHORT).show();
                    item.setIcon(getResources().getDrawable(R.drawable.ic_favorited));
                }else{
                    mSaved = !mSaved;
                    Toast.makeText(getContext(), R.string.deleted_from_favs, Toast.LENGTH_SHORT).show();
                    item.setIcon(getResources().getDrawable(R.drawable.ic_not_favorited));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class FetchPartsTask extends AsyncTask<Integer, Void, Integer>{
        private Document mDocument;
        private Element mElement, mElementExtraLink;
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
            fragment.mArticle.setArticleParts(fragment.mArticleParts);
            fragment.mAdapter.setParts(fragment.mArticleParts);
            fragment.mAdapter.notifyDataSetChanged();
            fragment.mProgressBar.setVisibility(View.GONE);
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
                for(Element e : mChilds){
                    if(e.tagName().equals("p")){
                        ArticlePart articlePart = new ArticlePart(ArticlePart.SIMPLE_TEXT, e.html());
                        fragment.mArticleParts.add(articlePart);
                    }else if(e.tagName().equals("li")){
                        ArticlePart articlePart = new ArticlePart(ArticlePart.LIST_ITEM, e.html());
                        fragment.mArticleParts.add(articlePart);
                    }else if(e.tagName().equals("pre")){
                        ArticlePart articlePart = new ArticlePart(ArticlePart.CODE, e.text());
                        fragment.mArticleParts.add(articlePart);
                    }else if(e.tagName().equals("img")){
                        ArticlePart articlePart = new ArticlePart(ArticlePart.IMAGE, e.attr("src"));
                        fragment.mArticleParts.add(articlePart);
                    }
                }
                for(Element e : mExtraChilds){
                    if(e.tagName().equals("a")){
                        fragment.mArticleParts.add(new ArticlePart(ArticlePart.ETRA_LINKS_ITEM,
                                e.text(), e.attr("href")));
                    }
                }
                size = mChilds.size();
            }catch (IOException e){
                e.printStackTrace();
            }
            return size;
        }
    }
}
