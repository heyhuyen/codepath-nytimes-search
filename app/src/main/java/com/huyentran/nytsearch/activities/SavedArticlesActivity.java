package com.huyentran.nytsearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;

import com.huyentran.nytsearch.R;
import com.huyentran.nytsearch.adapters.ArticleArrayAdapter;
import com.huyentran.nytsearch.adapters.ItemClickSupport;
import com.huyentran.nytsearch.adapters.SpacesItemDecoration;
import com.huyentran.nytsearch.db.ArticleDBHelper;
import com.huyentran.nytsearch.model.Article;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.huyentran.nytsearch.utils.Constants.ARTICLE_KEY;
import static com.huyentran.nytsearch.utils.Constants.GRID_NUM_COLUMNS;
import static com.huyentran.nytsearch.utils.Constants.GRID_SPACE_SIZE;

/**
 * Activity for viewing a list of saved articles.
 */
public class SavedArticlesActivity extends AppCompatActivity {

    private ArticleDBHelper articleDBHelper;
    private RecyclerView rvArticles;
    private ArrayList<Article> articles;
    private ArticleArrayAdapter articleArrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_articles);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.articleDBHelper = ArticleDBHelper.getInstance(this);
        initSavedArticles();
        setupViews();
    }

    /**
     * Initialize todo list and load existing todos from database.
     */
    private void initSavedArticles() {
        this.articles = new ArrayList<>();
        this.articles.addAll(articleDBHelper.getAllArticles());
    }

    /**
     * Wiring and setup of view and view-related components.
     */
    private void setupViews() {
        this.rvArticles = (RecyclerView) findViewById(R.id.rvSavedArticles);
        this.articleArrayAdapter = new ArticleArrayAdapter(this, this.articles);
        this.rvArticles.setAdapter(this.articleArrayAdapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(GRID_NUM_COLUMNS,
                        StaggeredGridLayoutManager.VERTICAL);
        this.rvArticles.setLayoutManager(gridLayoutManager);
        ItemClickSupport.addTo(this.rvArticles).setOnItemClickListener(
                (recyclerView, position, v) -> launchArticleView(position)
        );
        ItemClickSupport.addTo(this.rvArticles).setOnItemLongClickListener(
                (recyclerView, position, v) -> {
                    Article article = articles.get(position);
                    articleDBHelper.deleteArticle(article.getId());
                    articles.remove(position);
                    articleArrayAdapter.notifyDataSetChanged();
                    return true;
                }
        );
        SpacesItemDecoration decoration = new SpacesItemDecoration(GRID_SPACE_SIZE);
        this.rvArticles.addItemDecoration(decoration);
    }

    /**
     * Launches {@link ArticleActivity}.
     */
    private void launchArticleView(int position) {
        Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
        Article article = articles.get(position);
        intent.putExtra(ARTICLE_KEY, Parcels.wrap(article));
        startActivity(intent);
    }
}
