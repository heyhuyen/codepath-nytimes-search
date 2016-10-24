package com.huyentran.nytsearch.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.huyentran.nytsearch.R;

/**
 * ViewHolder class for text only article result items.
 */
public class TextViewHolder extends RecyclerView.ViewHolder {
    private TextView tvHeadline;
    private TextView tvSnippet;

    public TextViewHolder(View itemView) {
        super(itemView);
        this.tvHeadline = (TextView) itemView.findViewById(R.id.tvHeadline);
        this.tvSnippet = (TextView) itemView.findViewById(R.id.tvSnippet);
    }

    public TextView getHeadline() {
        return this.tvHeadline;
    }

    public void setHeadline(TextView headline) {
        this.tvHeadline = headline;
    }


    public TextView getSnippet() {
        return this.tvSnippet;
    }

    public void setSnippet(TextView snippet) {
        this.tvSnippet = snippet;
    }
}
