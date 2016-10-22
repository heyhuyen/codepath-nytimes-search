package com.huyentran.nytsearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.huyentran.nytsearch.R;
import com.huyentran.nytsearch.adapters.ArticleArrayAdapter;
import com.huyentran.nytsearch.adapters.EndlessRecyclerViewScrollListener;
import com.huyentran.nytsearch.adapters.SpacesItemDecoration;
import com.huyentran.nytsearch.fragments.FilterOptionsDialogFragment;
import com.huyentran.nytsearch.model.Article;
import com.huyentran.nytsearch.model.FilterSettings;
import com.huyentran.nytsearch.net.ArticleClient;
import com.huyentran.nytsearch.adapters.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.huyentran.nytsearch.utils.Constants.*;

/**
 * Main activity for searching for and displaying article results.
 */
public class SearchActivity extends AppCompatActivity
        implements FilterOptionsDialogFragment.FilterOptionsFragmentListener{

    private static final int GRID_NUM_COLUMNS = 4;
    private static final int GRID_SPACE_SIZE = 5;
    private static final int FIRST_PAGE = 0;
    private static final int PAGE_MAX = 2; // TODO: lower for now
    private static final String FILTER_OPTIONS_FRAGMENT_TAG = "fragment_filter_options";

    private SearchView searchView;
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
                        articleSearch(searchView.getQuery().toString(), page);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // hookup search view in action bar
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                articleSearch(query, FIRST_PAGE);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

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
    private void articleSearch(final String query, final int page) {
        Log.d("DEBUG", "Search query: " + query + "; page: "+ page);
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
                    articleSearch(query, page);
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
        intent.putExtra(ARTICLE_KEY, Parcels.wrap(article));
        startActivity(intent);
    }

    /**
     * Launches {@link FilterOptionsDialogFragment} modal overlay.
     */
    private void launchFilterOptions() {
        FilterOptionsDialogFragment filterOptionsDialogFragment =
                FilterOptionsDialogFragment.newInstance(new FilterSettings(this.filterSettings));
        filterOptionsDialogFragment.setStyle(
                DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        filterOptionsDialogFragment.show(getSupportFragmentManager(), FILTER_OPTIONS_FRAGMENT_TAG);
    }

    @Override
    public void onFinishFilterDialog(FilterSettings filterSettings) {
        boolean filtersChanged = !this.filterSettings.equals(filterSettings);
        Log.d("DEBUG", "filter changesd " + filtersChanged);
        if (filtersChanged) {
            this.filterSettings = filterSettings;
            if (this.articleArrayAdapter.getItemCount() != 0) {
                Log.d("DEBUG", "refresh!");
                articleSearch(this.searchView.getQuery().toString(), FIRST_PAGE);
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
                if (this.snackbar != null && this.snackbar.isShownOrQueued()) {
                    this.snackbar.dismiss();
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
