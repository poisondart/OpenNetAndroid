package ru.openet.nix.opennetclient;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class ArticleActivity extends AppCompatActivity {
    private static final String EXTRA_TITLE = "ru.opennet.nix.opennetclient.title";
    private static final String EXTRA_LINK = "ru.opennet.nix.opennetclient.link";
    private static final String EXTRA_DATE = "ru.opennet.nix.opennetclient.date";

    private TextView mArticleTitleView;
    private Toolbar mToolbar;

    private String mArticleTitle, mArticleDate, mArticleLink;

    public static Intent newInstance(Context context, String title, String link, String date){
        Intent intent = new Intent(context, ArticleActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_LINK, link);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        mArticleTitle = getIntent().getStringExtra(EXTRA_TITLE);
        mArticleDate = getIntent().getStringExtra(EXTRA_DATE);
        mArticleLink = getIntent().getStringExtra(EXTRA_LINK);

        mToolbar = findViewById(R.id.toolbar_article);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(mArticleDate);
        mArticleTitleView = findViewById(R.id.article_title);
        mArticleTitleView.setText(mArticleTitle);
    }
}
