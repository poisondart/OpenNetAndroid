package ru.openet.nix.opennetclient;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ArticleHostActivity extends AppCompatActivity {
    private static final String EXTRA_TITLE = "ru.opennet.nix.opennetclient.title";
    private static final String EXTRA_LINK = "ru.opennet.nix.opennetclient.link";
    private static final String EXTRA_DATE = "ru.opennet.nix.opennetclient.date";

    private String mArticleTitle, mArticleDate, mArticleLink;

    private FragmentManager mFragmentManager;
    private ArticleFragment mArticleFragment;

    public static Intent newInstance(Context context, String title, String link, String date){
        Intent intent = new Intent(context, ArticleHostActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_LINK, link);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_base);

        mArticleTitle = getIntent().getStringExtra(EXTRA_TITLE);
        mArticleDate = getIntent().getStringExtra(EXTRA_DATE);
        mArticleLink = getIntent().getStringExtra(EXTRA_LINK);

        mFragmentManager = getSupportFragmentManager();
        mArticleFragment = ArticleFragment.newInstance(mArticleDate, mArticleTitle, mArticleLink);
        mFragmentManager.beginTransaction().add(R.id.article_fragment_host, mArticleFragment).commit();
    }
}
