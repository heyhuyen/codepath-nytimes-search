package com.huyentran.nytsearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.huyentran.nytsearch.R;
import com.huyentran.nytsearch.adapters.ArticleArrayAdapter;
import com.huyentran.nytsearch.model.Article;
import com.huyentran.nytsearch.model.FilterSettings;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import cz.msebera.android.httpclient.Header;

import static com.huyentran.nytsearch.utils.Constants.*;

/**
 * Main activity for searching for and displaying article results.
 */
public class SearchActivity extends AppCompatActivity {

    private EditText etQuery;
    private Button btnSearch;
    private GridView gvResults;

    private ArrayList<Article> articles;
    private ArticleArrayAdapter articleArrayAdapter;

    private FilterSettings filterSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();
        this.filterSettings = new FilterSettings();
    }

    /**
     * Wiring and setup of view and view-related components.
     */
    private void setupViews() {
        this.etQuery = (EditText) findViewById(R.id.etQuery);
        this.btnSearch = (Button) findViewById(R.id.btnSearch);
        this.gvResults = (GridView) findViewById(R.id.gvResults);

        this.articles = new ArrayList<>();
        this.articleArrayAdapter = new ArticleArrayAdapter(this, this.articles);
        this.gvResults.setAdapter(this.articleArrayAdapter);

        // setup listener for search button click
        this.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                articleSearch();
            }
        });

        // setup listener for article grid click
        this.gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchArticleView(position);
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
    private void articleSearch() {
        String query = this.etQuery.getText().toString();
        if (query.isEmpty()) {
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams requestParams = paramsWithFilters();
        requestParams.put(API_KEY_PARAM_KEY, API_KEY);
        requestParams.put(PAGE_PARAM_KEY, 0);
        requestParams.put(QUERY_PARAM_KEY, query);

        client.get(NYT_SEARCH_API_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject(RESPONSE_KEY).getJSONArray(DOCS_KEY);
                    articleArrayAdapter.clear();
                    articleArrayAdapter.addAll(Article.fromJSONArray(articleJsonResults));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Returns a {@link RequestParams} object initialized with filter search settings, if any.
     */
    private RequestParams paramsWithFilters() {
        RequestParams params = new RequestParams();
        String beginDate = this.filterSettings.getBeginDate();
        if (beginDate != null && !TextUtils.isEmpty(beginDate)) {
            params.put(BEGIN_DATE_PARAM_KEY, beginDate.replace(HYPHEN, EMPTY));
        }
        FilterSettings.SortOrder sortOrder = this.filterSettings.getSortOrder();
        if (sortOrder != FilterSettings.SortOrder.NONE) {
            params.put(SORT_BY_PARAM_KEY, sortOrder.name().toLowerCase());
        }
        HashSet<String> newsDeskValues = this.filterSettings.getNewsDeskValues();
        if (!newsDeskValues.isEmpty()) {
            Joiner joiner = Joiner.on(SPACE);
            String newsDeskString = joiner.join(Iterables.transform(newsDeskValues,
                    NEWS_DESK_VALUE_TRANSFORM));
            params.put(FILTERS_PARAM_KEY, String.format(NEWS_DESK_FORMAT, newsDeskString));
        }
        return params;
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
                if (!this.articleArrayAdapter.isEmpty()) {
                    articleSearch();
                }
            }
        }
    }
}
