package com.huyentran.nytsearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huyentran.nytsearch.R;
import com.huyentran.nytsearch.model.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter for {@link Article Articles}.
 */
public class ArticleArrayAdapter extends RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivThumbnail;
        public TextView tvHeadline;

        public ViewHolder(View itemView) {
            super(itemView);

            ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
            tvHeadline = (TextView) itemView.findViewById(R.id.tvHeadline);
        }
    }

    private List<Article> mArticles;
    private Context mContext;

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    private Context getContext() {
        return this.mContext;
    }

    @Override
    public ArticleArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);

        // Return a new holder instance
        return new ViewHolder(articleView);
    }

    @Override
    public void onBindViewHolder(ArticleArrayAdapter.ViewHolder viewHolder, int position) {
        Article article = this.mArticles.get(position);

        ImageView ivThumbnail = viewHolder.ivThumbnail;
        ivThumbnail.setImageResource(0);
        String thumbnailUrl = article.getThumbnail();
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Picasso.with(getContext()).load(thumbnailUrl).fit()
                    .centerCrop().into(ivThumbnail);
        }
        TextView tvHeadline = viewHolder.tvHeadline;
        tvHeadline.setText(article.getHeadline());
    }

    @Override
    public int getItemCount() {
        return this.mArticles.size();
    }
}
