package ru.openet.nix.opennetclient;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
    private String mLink;
    private String mTitle;
    private Parcelable mListState;
    private Callbacks mCallbacks;
    /**
     * Обязательный интерфейс для активности-хоста.
     */
    public interface Callbacks {
        void onItemSelected(NewsItem item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mLink = bundle.getString(MainActivity.LINK_TAG);
        mTitle = bundle.getString(MainActivity.TITLE_TAG);
        if(savedInstanceState != null){
            mListState = savedInstanceState.getParcelable("ListState");
        }
        setRetainInstance(true);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_news_fragment, container, false);
        mNewsItems = new ArrayList<>();
        mToolbar = v.findViewById(R.id.toolbar);
        mRecyclerView = v.findViewById(R.id.recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new NewsItemAdapter(mNewsItems);
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout = v.findViewById(R.id.swipetorefresh);
        mToolbar.setTitle(mTitle);
        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(mToolbar);
        mDrawerLayout = actionBar.findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, mToolbar, R.string.app_name,
                R.string.app_name);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null || !savedInstanceState.containsKey("key")){
            new FetchFeedTask(this, mLink).execute();
        }else{
            mNewsItems = savedInstanceState.getParcelableArrayList("key");
            mAdapter = new NewsItemAdapter(mNewsItems);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchFeedTask(BasicNewsFragment.this, mLink).execute();
            }
        });
        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("key", mNewsItems);
        outState.putParcelable("ListState", mRecyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
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
            //fragment.mRecyclerView.setAdapter(new NewsItemAdapter(fragment.mNewsItems));
            fragment.mAdapter.setItems(fragment.mNewsItems);
            fragment.mAdapter.notifyDataSetChanged();
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


    public class NewsItemAdapter extends RecyclerView.Adapter<NewsItemAdapter.NewsItemViewHolder> {

        private ArrayList<NewsItem> mNewsItems;

        NewsItemAdapter(ArrayList<NewsItem> items) {
            mNewsItems = items;
        }
        public void setItems(ArrayList<NewsItem> items){
            mNewsItems = items;
        }
        @Override
        public NewsItemAdapter.NewsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_card, parent,false);
            return new NewsItemAdapter.NewsItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final NewsItemViewHolder holder, int position) {
            NewsItem item = mNewsItems.get(position);
            holder.bindItem(item);
        }

        @Override
        public int getItemCount() {
            return mNewsItems.size();
        }

        class NewsItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView mDateView, mTitleView, mDescrView;
            private NewsItem mItem;
            boolean mBoolean;
            NewsItemViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                mDateView = itemView.findViewById(R.id.date_view);
                mTitleView = itemView.findViewById(R.id.title_view);
                mDescrView = itemView.findViewById(R.id.descr_view);
                mDescrView.setMovementMethod(ClickableMovementMethod.getInstance());
                mDescrView.setClickable(false);
                mDescrView.setLongClickable(false);
            }

            public void bindItem(NewsItem item){
                mItem = item;
                Spanned spanned = Html.fromHtml(mItem.getDescr().replaceAll("<img.+?>", ""));
                mTitleView.setText(mItem.getTitle());
                mDescrView.setText(spanned);
                mDateView.setText(mItem.getDate());
            }
            @Override
            public void onClick(View view) {
                mCallbacks.onItemSelected(mItem);
            }
        }
    }

}
