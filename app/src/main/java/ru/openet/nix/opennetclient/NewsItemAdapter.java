package ru.openet.nix.opennetclient;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;


/**
 * Created by Nix on 07.01.2018.
 */

public class NewsItemAdapter extends RecyclerView.Adapter<NewsItemAdapter.NewsItemViewHolder> {

    private ArrayList<NewsItem> mNewsItems;

    NewsItemAdapter(ArrayList<NewsItem> items) {
        mNewsItems = items;
    }

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_card, parent,false);
        return new NewsItemViewHolder(v);
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

    static class NewsItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mDateView, mTitleView, mDescrView;
        private NewsItem mItem;
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

        private void bindItem(NewsItem item){
            mItem = item;
            Spanned spanned = Html.fromHtml(mItem.getDescr().replaceAll("<img.+?>", ""));
            mTitleView.setText(mItem.getTitle());
            mDescrView.setText(spanned);
            mDateView.setText(mItem.getDate());
        }
        @Override
        public void onClick(View view) {
            Intent intent = ArticleHostActivity.newInstance(view.getContext(),
                    mItem.getTitle(),
                    mItem.getLink(),
                    mItem.getDate());
            view.getContext().startActivity(intent);
        }
    }
}
