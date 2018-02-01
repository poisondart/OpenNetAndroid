package ru.openet.nix.opennetclient;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * Created by Nix on 01.02.2018.
 */

public class WebViewFragment extends Fragment {
    private static final String URI = "uri";
    private WebView mWebView;
    private Toolbar mToolbar;
    private AppCompatActivity mActionBar;
    private ProgressBar mProgressBar;
    private Uri mUri;
    private String mLink;

    public static WebViewFragment newInstance(String uri){
        WebViewFragment webViewFragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(URI, uri);
        webViewFragment.setArguments(bundle);
        return webViewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLink = (String) getArguments().getSerializable(URI);
        mUri = Uri.parse(mLink);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
    @Nullable
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.webview_fragment_layout, container,false);
        mToolbar = v.findViewById(R.id.web_toolbar);
        mProgressBar = v.findViewById(R.id.webview_progress_bar);
        mWebView = v.findViewById(R.id.webview);
        mActionBar = (AppCompatActivity) getActivity();
        mActionBar.setSupportActionBar(mToolbar);
        mToolbar.setTitle(mLink);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView webView, int newProgress){
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }
            public void onReceivedTitle(WebView webView, String title) {
                mToolbar.setTitle(webView.getTitle());
            }
        });
        mWebView.loadUrl(mUri.toString());
        return v;
    }
}
