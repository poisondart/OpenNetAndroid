package ru.openet.nix.opennetclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
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
            //Inflating header view
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.article_header, parent, false);
            return new HeaderViewHolder(itemView);
        }else if(viewType == TYPE_ITEM_TEXT){
            //Inflating text view
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.article_part_text, parent,false);
            return new TextPartViewHolder(itemView);
        }else if(viewType == TYPE_ITEM_CODE){
            //Inflating code view
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.article_part_code, parent,false);
            return new CodePartViewHolder(itemView);
        }else if(viewType == TYPE_ITEM_IMAGE){
            //Inflating image view
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.article_part_image, parent,false);
            return new ImagePartViewHolder(itemView);
        }else if(viewType == TYPE_ITEM_LIST){
            //Inflating image view
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.article_part_list, parent,false);
            return new ListPartViewHolder(itemView);
        }else if(viewType == TYPE_ITEM_EXTRA_LINK){
            //Inflating image view
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
        }else if (holder instanceof ExtraLinkPartViewHolder) {
            final ExtraLinkPartViewHolder extraPartViewHolder = (ExtraLinkPartViewHolder) holder;
            extraPartViewHolder.textView.setText(mArticleParts.get(position - 1).getText());
        }else if (holder instanceof ImagePartViewHolder) {
            final ImagePartViewHolder imagePartViewHolder = (ImagePartViewHolder) holder;
            Glide.with(mContext)
                    .load(mArticleParts.get(position - 1)
                            .getContentLink())
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
            }else if(mArticleParts.get(position - 1).getType() == ArticlePart.ETRA_LINKS_ITEM){
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
    /*private class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView footerTitle;

        private FooterViewHolder(View view) {
            super(view);
            footerTitle = view.findViewById(R.id.topic_links);
        }
    }*/
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
    private class ExtraLinkPartViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ExtraLinkPartViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.link_textview);
        }
    }
}
