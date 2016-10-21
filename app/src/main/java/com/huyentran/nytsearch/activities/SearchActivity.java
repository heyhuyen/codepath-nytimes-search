package com.huyentran.nytsearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.huyentran.nytsearch.R;
import com.huyentran.nytsearch.adapters.ArticleArrayAdapter;
import com.huyentran.nytsearch.adapters.EndlessRecyclerViewScrollListener;
import com.huyentran.nytsearch.adapters.SpacesItemDecoration;
import com.huyentran.nytsearch.model.Article;
import com.huyentran.nytsearch.model.FilterSettings;
import com.huyentran.nytsearch.net.ArticleClient;
import com.huyentran.nytsearch.adapters.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.huyentran.nytsearch.utils.Constants.*;

/**
 * Main activity for searching for and displaying article results.
 */
public class SearchActivity extends AppCompatActivity {
    private static final int GRID_NUM_COLUMNS = 4;
    private static final int GRID_SPACE_SIZE = 5;
    private static final int FIRST_PAGE = 0;
    private static final int PAGE_MAX = 2; // TODO: lower for now

    private EditText etQuery;
    private Button btnSearch;
    private RecyclerView rvArticles;

    private ArrayList<Article> articles;
    private ArticleArrayAdapter articleArrayAdapter;

    private FilterSettings filterSettings;

    private ArticleClient client;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();
        this.filterSettings = new FilterSettings();
        this.client = new ArticleClient();
    }

    /**
     * Wiring and setup of view and view-related components.
     */
    private void setupViews() {
        this.etQuery = (EditText) findViewById(R.id.etQuery);

        this.btnSearch = (Button) findViewById(R.id.btnSearch);
        this.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                articleSearch(FIRST_PAGE);
            }
        });

        this.rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
        this.articles = new ArrayList<>();
        this.articleArrayAdapter = new ArticleArrayAdapter(this, this.articles);
        this.rvArticles.setAdapter(this.articleArrayAdapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(GRID_NUM_COLUMNS,
                        StaggeredGridLayoutManager.VERTICAL);
        this.rvArticles.setLayoutManager(gridLayoutManager);
        ItemClickSupport.addTo(this.rvArticles).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        launchArticleView(position);
                    }
                }
        );
        SpacesItemDecoration decoration = new SpacesItemDecoration(GRID_SPACE_SIZE);
        this.rvArticles.addItemDecoration(decoration);
        this.rvArticles.addOnScrollListener(
                new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        articleSearch(page);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miFilter) {
            launchFilterOptions();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Launches request for an article search for text in the query Edit Text.
     */
    private void articleSearch(final int page) {
        String query = this.etQuery.getText().toString();
        if (query.isEmpty() || page == PAGE_MAX || !isOnline()) {
            return;
        }
        if (page == FIRST_PAGE) {
            articles.clear();
            articleArrayAdapter.notifyDataSetChanged();
        }
        this.client.getArticles(page, query, this.filterSettings, new JsonHttpResponseHandler
                () {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject(RESPONSE_KEY)
                            .getJSONArray(DOCS_KEY);
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    articleArrayAdapter.notifyDataSetChanged();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (statusCode == 429 && errorResponse.toString().equals("{\"message\":\"API rate limit exceeded\"}")) {
                    Log.d("DEBUG", "API rate limit exceeded. Retrying.");
                    articleSearch(page);
                }
            }
        });
    }

    /**
     * Launches {@link ArticleActivity}.
     */
    private void launchArticleView(int position) {
        Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
        Article article = articles.get(position);
        intent.putExtra(ARTICLE_KEY, article);
        startActivity(intent);
    }

    /**
     * Launches {@link FilterOptionsActivity}.
     */
    private void launchFilterOptions() {
        Intent intent = new Intent(getApplicationContext(), FilterOptionsActivity.class);
        intent.putExtra(FILTER_SETTINGS_KEY, this.filterSettings);
        startActivityForResult(intent, FILTER_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == FILTER_CODE) {
            FilterSettings filterSettings =
                    (FilterSettings) data.getSerializableExtra(FILTER_SETTINGS_KEY);
            boolean filtersChanged = !this.filterSettings.equals(filterSettings);
            if (filtersChanged) {
                this.filterSettings = filterSettings;
                if (this.articleArrayAdapter.getItemCount() != 0) {
                    articleSearch(FIRST_PAGE);
                }
            }
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        boolean network = isNetworkAvailable();
        if (!network) {
            // no network!
            this.snackbar = Snackbar.make(this.rvArticles, R.string.error_network,
                    Snackbar.LENGTH_INDEFINITE);
            this.snackbar.show();
            return false;
        }

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            if (exitValue == 0) {
                if (this.snackbar != null && this.snackbar.isShown()) {
                    this.snackbar = Snackbar.make(this.rvArticles, R.string.online,
                            Snackbar.LENGTH_SHORT);
                    this.snackbar.show();
                }
                return true;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // offline
        this.snackbar = Snackbar.make(this.rvArticles, R.string.error_offline,
                Snackbar.LENGTH_INDEFINITE);
        this.snackbar.show();
        return false;
    }
}
