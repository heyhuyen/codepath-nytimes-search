package com.huyentran.nytsearch.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huyentran.nytsearch.R;

/**
 * ViewHolder class for article result items with thumbnail images.
 */
public class ThumbnailViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivThumbnail;
    private TextView tvHeadline;

    public ThumbnailViewHolder(View itemView) {
        super(itemView);
        this.ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
        this.tvHeadline = (TextView) itemView.findViewById(R.id.tvHeadline);
    }

    public ImageView getThumbnail() {
        return this.ivThumbnail;
    }

    public void setThumbnail(ImageView thumbnail) {
        this.ivThumbnail = thumbnail;
    }

    public TextView getHeadline() {
        return this.tvHeadline;
    }

    public void setHeadline(TextView headline) {
        this.tvHeadline = headline;
    }
}
