package ru.openet.nix.opennetclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.annotation.ParametersAreNonnullByDefault;
import io.realm.Realm;

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
        //setRetainInstance(true);
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
        mProgressBar.setMax(100);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = v.findViewById(R.id.article_recyclerview);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mArticle = new Article(mArticleDate, mArticleTitle, mArticleLink);
        mActionBar = (AppCompatActivity) getActivity();
        mActionBar.setSupportActionBar(mToolbar);
        /*if(mActionBar.getSupportActionBar() != null){
            mActionBar.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mActionBar.getSupportActionBar().setDisplayShowHomeEnabled(true);*/
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
                    }else if(e.tagName().equals("iframe")){
                        ArticlePart articlePart = new ArticlePart(ArticlePart.VIDEO_ITEM, e.attr("src"), mLink);
                        articlePart.initVideoId();
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
                realm.copyToRealmOrUpdate(mArticle);
                realm.copyToRealm(mArticleParts);
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
                    mArticle = realm.copyFromRealm(realm.where(Article.class)
                            .equalTo(Article.LINK, articleLink).findAll().first());
                    mArticleDate = mArticle.getDate();
                    mArticleTitle = mArticle.getTitle();
                    mArticleLink = mArticle.getLink();
                    mArticleParts.addAll(realm.copyFromRealm(realm.where(ArticlePart.class)
                            .equalTo(ArticlePart.ARTICLE_LINK, articleLink).findAll()));
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


    public class ArticleRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM_TEXT = 2;
        private static final int TYPE_ITEM_CODE = 3;
        private static final int TYPE_ITEM_IMAGE = 4;
        private static final int TYPE_ITEM_LIST = 5;
        private static final int TYPE_ITEM_EXTRA_LINK = 6;
        private static final int TYPE_ITEM_VIDEO = 7;
        private int mFirstExtraLinkPosition = -1;
        private Context mContext;
        private ArrayList<ArticlePart> mArticleParts;
        private String mArticleTitle;

        public ArticleRecyclerViewAdapter(Context context, String title, ArrayList<ArticlePart> parts) {
            super();
            mArticleParts = parts;
            mArticleTitle = title;
            mContext = context;
        }
        public void setParts(ArrayList<ArticlePart> parts){
            mArticleParts = parts;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            if(viewType == TYPE_HEADER){
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.article_header, parent, false);
                return new HeaderViewHolder(itemView);
            }else if(viewType == TYPE_ITEM_TEXT){
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.article_part_text, parent,false);
                return new TextPartViewHolder(itemView);
            }else if(viewType == TYPE_ITEM_CODE){
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.article_part_code, parent,false);
                return new CodePartViewHolder(itemView);
            }else if(viewType == TYPE_ITEM_IMAGE){
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.article_part_image, parent,false);
                return new ImagePartViewHolder(itemView);
            }else if(viewType == TYPE_ITEM_LIST){
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.article_part_list, parent,false);
                return new ListPartViewHolder(itemView);
            }else if(viewType == TYPE_ITEM_VIDEO){
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.article_part_video, parent,false);
                return new VideoPartViewHolder(itemView);
            }else if(viewType == TYPE_ITEM_EXTRA_LINK){
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.article_part_extra_links, parent,false);
                return new ExtraLinkPartViewHolder(itemView);
            }else return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeaderViewHolder) {
                final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
                headerHolder.headerTitle.setText(mArticleTitle);
            }else if (holder instanceof TextPartViewHolder) {
                final TextPartViewHolder textPartViewHolder = (TextPartViewHolder) holder;
                Spanned spanned = Html.fromHtml(mArticleParts.get(position - 1).getText().replaceAll("<img.+?>", ""));
                textPartViewHolder.textView.setText(spanned);
            }else if (holder instanceof CodePartViewHolder) {
                final CodePartViewHolder codePartViewHolder = (CodePartViewHolder) holder;
                codePartViewHolder.codeView.setText(mArticleParts.get(position - 1).getText());
            }else if (holder instanceof ListPartViewHolder) {
                final ListPartViewHolder listPartViewHolder = (ListPartViewHolder) holder;
                Spanned spanned = Html.fromHtml(mArticleParts.get(position - 1).getText().replaceAll("<img.+?>", ""));
                listPartViewHolder.textView.setText(spanned);
            }else if (holder instanceof VideoPartViewHolder) {
                final VideoPartViewHolder videoPartViewHolder = (VideoPartViewHolder) holder;
                videoPartViewHolder.bindTube(mArticleParts.get(position - 1));
            }else if (holder instanceof ExtraLinkPartViewHolder) {
                final ExtraLinkPartViewHolder extraLinkPartViewHolder = (ExtraLinkPartViewHolder) holder;
                extraLinkPartViewHolder.bindPart(mArticleParts.get(position - 1), position - 1);
            }else if (holder instanceof ImagePartViewHolder) {
                final ImagePartViewHolder imagePartViewHolder = (ImagePartViewHolder) holder;
                GlideApp.with(mContext)
                        .load(mArticleParts.get(position - 1)
                                .getContentLink())
                        .placeholder(R.drawable.preload)
                        .into(imagePartViewHolder.imageView);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEADER;
            }else{
                if(mArticleParts.get(position - 1).getType() == ArticlePart.SIMPLE_TEXT){
                    return TYPE_ITEM_TEXT;
                } else if(mArticleParts.get(position - 1).getType() == ArticlePart.CODE){
                    return TYPE_ITEM_CODE;
                }else if(mArticleParts.get(position - 1).getType() == ArticlePart.IMAGE){
                    return TYPE_ITEM_IMAGE;
                }else if(mArticleParts.get(position - 1).getType() == ArticlePart.LIST_ITEM){
                    return TYPE_ITEM_LIST;
                }else if(mArticleParts.get(position - 1).getType() == ArticlePart.VIDEO_ITEM){
                    return TYPE_ITEM_VIDEO;
                }else if(mArticleParts.get(position - 1).getType() == ArticlePart.ETRA_LINKS_ITEM){
                    if(mFirstExtraLinkPosition < 0){
                        mFirstExtraLinkPosition = position - 1;
                    }
                    return TYPE_ITEM_EXTRA_LINK;
                }else{
                    return 0;
                }
            }
        }

        @Override
        public int getItemCount() {
            return mArticleParts.size() + 1;
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView headerTitle;

            private HeaderViewHolder(View view) {
                super(view);
                headerTitle = view.findViewById(R.id.article_title);
            }
        }
        private class TextPartViewHolder extends RecyclerView.ViewHolder{
            TextView textView;
            private TextPartViewHolder(View view){
                super(view);
                textView = view.findViewById(R.id.text_part);
                textView.setMovementMethod(ClickableMovementMethod.getInstance());
                textView.setClickable(false);
                textView.setLongClickable(false);
            }
        }
        private class CodePartViewHolder extends RecyclerView.ViewHolder{
            TextView codeView;
            private CodePartViewHolder(View view){
                super(view);
                codeView = view.findViewById(R.id.code_part);
            }
        }
        private class ImagePartViewHolder extends RecyclerView.ViewHolder{
            ImageView imageView;
            private ImagePartViewHolder(View view){
                super(view);
                imageView = view.findViewById(R.id.image_part);
            }
        }
        private class ListPartViewHolder extends RecyclerView.ViewHolder{
            TextView textView;
            private ListPartViewHolder(View view){
                super(view);
                textView = view.findViewById(R.id.article_list_item);
                textView.setMovementMethod(ClickableMovementMethod.getInstance());
                textView.setClickable(false);
                textView.setLongClickable(false);
            }
        }
        private class VideoPartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            YouTubeThumbnailView mYouTubeThumbnailView;
            protected RelativeLayout mRelativeLayout;
            protected ImageView mImageView;
            ArticlePart mPart;
            boolean isInit = false;

            public VideoPartViewHolder(View itemView) {
                super(itemView);
                mYouTubeThumbnailView = itemView.findViewById(R.id.video_view);
                mImageView = itemView.findViewById(R.id.btnYoutube_player);
                mRelativeLayout = itemView.findViewById(R.id.relative_youtube);
                mImageView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if(isInit){
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent((AppCompatActivity)view.getContext(),
                            Links.YOUTUBE_API_KEY, mPart.getContentLink());
                    view.getContext().startActivity(intent);
                }
            }

            private void bindTube(final ArticlePart part){
                mPart = part;
                final YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener =
                        new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                            @Override
                            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                                youTubeThumbnailView.setVisibility(View.VISIBLE);
                                mRelativeLayout.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                                //Toast.makeText(mContext, "LOL", Toast.LENGTH_LONG).show();
                            }
                        };
                mYouTubeThumbnailView.initialize(Links.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                        youTubeThumbnailLoader.setVideo(mPart.getContentLink());
                        youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
                        isInit = true;
                    }

                    @Override
                    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                        //Toast.makeText(mContext, "LOL Init", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        private class ExtraLinkPartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView textView;
            ArticlePart part;
            int pos;
            public ExtraLinkPartViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.link_textview);
                itemView.setOnClickListener(this);
            }
            private void bindPart(ArticlePart articlePart, int p){
                part = articlePart;
                textView.setText(part.getText());
                pos = p;
            }

            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                boolean isbrowser = preferences.getBoolean(SettingsFragment.KEY_BROWSER_TYPE, true);
                Fragment fragment;
                if(pos == mFirstExtraLinkPosition){
                    if(!isbrowser){
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(part.getContentLink()));
                        view.getContext().startActivity(i);
                        return;
                    }
                    fragment = WebViewFragment.newInstance(part.getContentLink());
                }else {
                    fragment = ArticleFragment.newInstance(null, part.getText(), part.getContentLink());
                }
                FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.article_fragment_host, fragment).addToBackStack(null).commit();
            }
        }
    }

}
