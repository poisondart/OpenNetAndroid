package ru.openet.nix.opennetclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Nix on 02.01.2018.
 */

public class BasicNewsFragment extends Fragment {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ArrayList<NewsItem> mNewsItems;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private NewsItemAdapter mAdapter;
    private DividerItemDecoration mDividerItemDecoration;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Spinner mSpinner;

    private String mLink;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_news_fragment, container, false);
        mNewsItems = new ArrayList<>();
        mToolbar = v.findViewById(R.id.toolbar);
        mSpinner = v.findViewById(R.id.spinner_nav);
        iniSpinner();
        mRecyclerView = v.findViewById(R.id.recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new NewsItemAdapter(mNewsItems);
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout = v.findViewById(R.id.swipetorefresh);
        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(mToolbar);
        actionBar.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDrawerLayout = actionBar.findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, mToolbar, R.string.app_name,
                R.string.app_name);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        new FetchFeedTask(this, mLink).execute();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchFeedTask(BasicNewsFragment.this, mLink).execute();
            }
        });
        return v;
    }


    private void iniSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.spinner_topics, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        new FetchFeedTask(BasicNewsFragment.this, Links.MAIN_NEWS_RSS_LINK).execute();
                        mLink = Links.MAIN_NEWS_RSS_LINK;
                        break;
                    case 1:
                        new FetchFeedTask(BasicNewsFragment.this, Links.MAIN_SECURITY_PROB_RSS_LINK).execute();
                        mLink = Links.MAIN_SECURITY_PROB_RSS_LINK;
                        break;
                    case 2:
                        new FetchFeedTask(BasicNewsFragment.this, Links.MAIN_NEW_SOFT_PROB_RSS_LINK).execute();
                        mLink = Links.MAIN_NEW_SOFT_PROB_RSS_LINK;
                        break;
                    case 3:
                        new FetchFeedTask(BasicNewsFragment.this, Links.MAIN_LINUX_RSS_LINK).execute();
                        mLink = Links.MAIN_LINUX_RSS_LINK;
                        break;
                    case 4:
                        new FetchFeedTask(BasicNewsFragment.this, Links.MAIN_BSD_RSS_LINK).execute();
                        mLink = Links.MAIN_BSD_RSS_LINK;
                        break;
                    case 5:
                        new FetchFeedTask(BasicNewsFragment.this, Links.UBUNTU_NEWS_RSS_LINK).execute();
                        mLink = Links.UBUNTU_NEWS_RSS_LINK;
                        break;
                    case 6:
                        new FetchFeedTask(BasicNewsFragment.this, Links.MAIN_FEDORA_RSS_LINK).execute();
                        mLink = Links.MAIN_FEDORA_RSS_LINK;
                        break;
                    case 7:
                        new FetchFeedTask(BasicNewsFragment.this, Links.MAIN_MOZILLA_FIREFOX_RSS_LINK).execute();
                        mLink = Links.MAIN_MOZILLA_FIREFOX_RSS_LINK;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });
    }

    private static class FetchFeedTask extends AsyncTask<Integer, Void, Integer>{
        private WeakReference<BasicNewsFragment> fragmentRef;
        private String linkRef;

        private FetchFeedTask(BasicNewsFragment context, String link) {
            fragmentRef = new WeakReference<>(context);
            linkRef = link;
        }

        @Override
        protected void onPreExecute() {
            BasicNewsFragment fragment = fragmentRef.get();
            if (fragment == null) return;
            fragment.mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            BasicNewsFragment fragment = fragmentRef.get();
            try{
                URL url = new URL(linkRef);
                InputStream inputStream = url.openConnection().getInputStream();
                fragment.mNewsItems = parseFeed(inputStream);

            }catch (MalformedURLException m){
                m.printStackTrace();
            }catch (IOException ioe){
                ioe.printStackTrace();
            }catch (XmlPullParserException x){
                x.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            BasicNewsFragment fragment = fragmentRef.get();
            if (fragment == null) return;
            fragment.mSwipeRefreshLayout.setRefreshing(false);
            fragment.mRecyclerView.setAdapter(new NewsItemAdapter(fragment.mNewsItems));
        }
    }

    public static ArrayList<NewsItem> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException{
        String title = null;
        String pubDate = null;
        String descr = null;
        String link = null;
        boolean isItem = false;
        boolean hook = false;
        ArrayList<NewsItem> items = new ArrayList<>();

        try{
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT){


                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();

                if(name == null){
                    continue;
                }

                if(name.equals("channel")){
                    hook = true;
                    continue;
                }

                if(eventType == XmlPullParser.END_TAG){
                    if(name.equalsIgnoreCase("item")){
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("pubDate")) {
                    DateFormat oldDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
                    DateFormat newDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:MM", new Locale("ru"));
                    try{
                        Date date = oldDateFormat.parse(result);
                        pubDate = newDateFormat.format(date);
                    }catch (ParseException p){
                        p.printStackTrace();
                    }

                } else if (name.equalsIgnoreCase("description")) {
                    if(!hook){
                        descr = result;
                    }else{
                        hook = false;
                        continue;
                    }
                }else if(name.equalsIgnoreCase("link")){
                    link = result;
                }

                if (title != null && pubDate != null && descr != null && link != null) {
                    if (isItem) {
                        NewsItem item = new NewsItem(pubDate, title, descr, link);
                        items.add(item);
                    }

                    title = null;
                    pubDate = null;
                    descr = null;
                    link = null;
                    isItem = false;
                }
            }
            return items;
        }finally {
            inputStream.close();
        }
    }
}
