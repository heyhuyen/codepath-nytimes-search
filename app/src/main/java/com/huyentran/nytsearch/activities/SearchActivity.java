package com.huyentran.nytsearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.huyentran.nytsearch.R;
import com.huyentran.nytsearch.adapters.ArticleArrayAdapter;
import com.huyentran.nytsearch.model.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Main activity for searching for and displaying article results.
 */
public class SearchActivity extends AppCompatActivity {

    private EditText etQuery;
    private Button btnSearch;
    private GridView gvResults;

    private ArrayList<Article> articles;
    private ArticleArrayAdapter articleArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();
    }

    public void setupViews() {
        this.etQuery = (EditText) findViewById(R.id.etQuery);
        this.btnSearch = (Button) findViewById(R.id.btnSearch);
        this.gvResults = (GridView) findViewById(R.id.gvResults);

        this.articles = new ArrayList<>();
        this.articleArrayAdapter = new ArticleArrayAdapter(this, this.articles);
        this.gvResults.setAdapter(this.articleArrayAdapter);

        // setup listener for article grid click
        this.gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // create an intent to display the article
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                // get the article to display
                Article article = articles.get(position);
                // pass article to intent
                intent.putExtra("article", article);
                // launch activity
                startActivity(intent);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {
        String query = this.etQuery.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams requestParams = new RequestParams();
        requestParams.put("api-key", "e9f2d2081760448296bde86891581508");
        requestParams.put("page", 0);
        requestParams.put("q", query);

        client.get(url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articleArrayAdapter.addAll(Article.fromJSONArray(articleJsonResults));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
