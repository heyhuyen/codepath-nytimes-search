package com.huyentran.nytsearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huyentran.nytsearch.R;
import com.huyentran.nytsearch.model.Article;

import java.util.List;

/**
 * Adapter for {@link Article Articles}.
 */
public class ArticleArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Article> mArticles;
    private Context mContext;

    private final int TEXT = 0, THUMBNAIL = 1;

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        this.mArticles = articles;
        this.mContext = context;
    }

    private Context getContext() {
        return this.mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case THUMBNAIL:
                View thumbnailView = inflater.inflate(R.layout.item_thumbnail, parent, false);
                viewHolder = new ThumbnailViewHolder(thumbnailView);
                break;
            case TEXT:
                View textView = inflater.inflate(R.layout.item_text, parent, false);
                viewHolder = new TextViewHolder(textView);
                break;
            default:
                View defaultView = inflater.inflate(R.layout.item_text, parent, false);
                viewHolder = new TextViewHolder(defaultView);
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case THUMBNAIL:
                ThumbnailViewHolder vh1 = (ThumbnailViewHolder) viewHolder;
                configureThumnailViewHolder(vh1, position);
                break;
            case TEXT:
                TextViewHolder vh2 = (TextViewHolder) viewHolder;
                configureTextViewHolder(vh2, position);
                break;
            default:
                TextViewHolder vh = (TextViewHolder) viewHolder;
                configureTextViewHolder(vh, position);
                break;
        }
    }

    private void configureThumnailViewHolder(ThumbnailViewHolder viewHolder, int position) {
        Article article = this.mArticles.get(position);

        ImageView ivThumbnail = viewHolder.getThumbnail();
        ivThumbnail.setImageResource(0);
        String thumbnailUrl = article.getThumbnail();
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Glide.with(getContext()).load(thumbnailUrl)
                    .placeholder(R.drawable.icon_news)
                    .centerCrop()
                    .into(ivThumbnail);
        }
        TextView tvHeadline = viewHolder.getHeadline();
        tvHeadline.setText(article.getHeadline());
    }

    private void configureTextViewHolder(TextViewHolder viewHolder, int position) {
        Article article = this.mArticles.get(position);

        TextView tvHeadline = viewHolder.getHeadline();
        tvHeadline.setText(article.getHeadline());

        TextView tvSnippet = viewHolder.getSnippet();
        tvSnippet.setText(article.getSnippet());
    }

    @Override
    public int getItemCount() {
        return this.mArticles.size();
    }

    @Override
    public int getItemViewType(int position) {
        String thumbnail = this.mArticles.get(position).getThumbnail();
        if (thumbnail != null && !TextUtils.isEmpty(thumbnail)) {
            return THUMBNAIL;
        } else {
            return TEXT;
        }
    }
}
