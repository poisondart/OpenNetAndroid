package ru.openet.nix.opennetclient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import io.realm.RealmResults;

/**
 * Created by Nix on 01.02.2018.
 */

public class SavedArticleAdapter extends RecyclerView.Adapter<SavedArticleAdapter.SavedViewHolder> {
    private RealmResults<Article> mArticles;

    public SavedArticleAdapter(RealmResults<Article> articles) {
        mArticles = articles;
    }

    @Override
    public SavedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_article_card, parent,false);
        return new SavedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SavedViewHolder holder, int position) {
        Article article = mArticles.get(position);
        holder.bindItem(article);
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    static class SavedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
       TextView dateView, titleView;
       Article article;
       public SavedViewHolder(View itemView) {
           super(itemView);
           dateView = itemView.findViewById(R.id.date_view_saved);
           titleView = itemView.findViewById(R.id.title_view_saved);
           itemView.setOnClickListener(this);
       }

       private void bindItem(Article item){
           article = item;
           dateView.setText(article.getDate());
           titleView.setText(article.getTitle());
       }
        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "OK", Toast.LENGTH_SHORT).show();
        }
    }
}
