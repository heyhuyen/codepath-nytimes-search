package com.huyentran.nytsearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.huyentran.nytsearch.R;
import com.huyentran.nytsearch.db.ArticleDBHelper;
import com.huyentran.nytsearch.model.Article;

import org.parceler.Parcels;

import static com.huyentran.nytsearch.utils.Constants.ARTICLE_KEY;

/**
 * Activity for viewing an article.
 */
public class ArticleActivity extends AppCompatActivity {
    private Menu menu;
    private WebView webView;
    private ArticleDBHelper articleDBHelper;
    private Article article;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.articleDBHelper = ArticleDBHelper.getInstance(this);

        this.article = Parcels.unwrap(getIntent().getParcelableExtra(ARTICLE_KEY));
        this.webView = (WebView) findViewById(R.id.wvArticle);
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        this.webView.loadUrl(article.getWebUrl());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        this.menu = menu;

        MenuItem shareItem = menu.findItem(R.id.miShare);
        ShareActionProvider miShareAction =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        // pass in the URL currently being used by the WebView
        shareIntent.putExtra(Intent.EXTRA_TEXT, this.webView.getUrl());
        miShareAction.setShareIntent(shareIntent);

        // check if article is saved and set save icon
        boolean articleSaved = this.articleDBHelper.getArticle(article) != null;
        saveIconState(articleSaved);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.miSave) {
            saveArticle(true);
        }
        else if (id == R.id.miUnSave) {
            saveArticle(false);
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveIconState(boolean filled) {
        if (filled) {
            this.menu.setGroupVisible(R.id.group_heart_empty, false);
            this.menu.setGroupVisible(R.id.group_heart_filled, true);
        } else {
            this.menu.setGroupVisible(R.id.group_heart_filled, false);
            this.menu.setGroupVisible(R.id.group_heart_empty, true);
        }
    }

    private void saveArticle(boolean save) {
        if (save) {
            saveIconState(true);
            this.articleDBHelper.addArticle(this.article);
        } else {
            saveIconState(false);
            this.articleDBHelper.deleteArticle(this.article.getId());
        }
    }
}
