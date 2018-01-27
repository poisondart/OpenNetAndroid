package ru.openet.nix.opennetclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Nix on 28.01.2018.
 */

public class ArticleFragment extends Fragment {

    private TextView mArticleTitleView;
    private Toolbar mToolbar;

    private String mArticleTitle, mArticleDate, mArticleLink;

    private static final String ARG_ARTICLE_DATE = "article_date";
    private static final String ARG_ARTICLE_TITLE = "article_title";
    private static final String ARG_ARTICLE_LINK = "article_link";

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.article_fragment, container, false);
        mToolbar = v.findViewById(R.id.toolbar_article);
        mArticleTitleView = v.findViewById(R.id.article_title);
        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(mToolbar);
        if(actionBar.getSupportActionBar() != null){
            actionBar.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        actionBar.getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle(mArticleDate);
        mArticleTitleView = v.findViewById(R.id.article_title);
        mArticleTitleView.setText(mArticleTitle);
        return v;
    }
}
