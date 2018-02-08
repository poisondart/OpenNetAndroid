package ru.openet.nix.opennetclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;

/**
 * Created by Nix on 28.01.2018.
 */

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
                    //nothing
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
                    //nothing
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