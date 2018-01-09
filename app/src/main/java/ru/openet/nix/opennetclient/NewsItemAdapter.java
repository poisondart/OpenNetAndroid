package ru.openet.nix.opennetclient;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
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

    public NewsItemAdapter(ArrayList<NewsItem> items) {
        mNewsItems = items;
    }

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_card, parent,false);
        return new NewsItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NewsItemViewHolder holder, int position) {
        Spanned spanned = Html.fromHtml(mNewsItems.get(position).getDescr());
        holder.dateView.setText(mNewsItems.get(position).getDate());
        holder.titleView.setText(mNewsItems.get(position).getTitle());
        holder.descrView.setText(spanned);
    }

    @Override
    public int getItemCount() {
        return mNewsItems.size();
    }

    static class NewsItemViewHolder extends RecyclerView.ViewHolder{
        TextView dateView, titleView, descrView;
        NewsItemViewHolder(View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.date_view);
            titleView = itemView.findViewById(R.id.title_view);
            descrView = itemView.findViewById(R.id.descr_view);
            descrView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
