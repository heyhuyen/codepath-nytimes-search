package com.huyentran.nytsearch.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huyentran.nytsearch.R;
import com.huyentran.nytsearch.model.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter for {@link Article Articles}.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get data item for position
        Article article = this.getItem(position);

        // check if existing view is being reused
        // if not using a recycled view, inflate the layout
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.item_article_result, parent, false);
        }

        // find the image view
        ImageView ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
        // clear out recycled image
        ivThumbnail.setImageResource(0);
        TextView tvHeadline = (TextView) convertView.findViewById(R.id.tvHeadline);
        tvHeadline.setText(article.getHeadline());

        // populate thumbnail image
        // remote download image in background
        String thumbnailUrl = article.getThumbnail();
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Picasso.with(getContext()).load(thumbnailUrl).into(ivThumbnail);
        }

        return convertView;
    }
}
