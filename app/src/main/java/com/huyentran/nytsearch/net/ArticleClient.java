package com.huyentran.nytsearch.net;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.huyentran.nytsearch.model.FilterSettings;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.HashSet;

import static com.huyentran.nytsearch.utils.Constants.EMPTY;

public class ArticleClient {
    private static final String API_SEARCH_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    private static final String API_KEY = "e9f2d2081760448296bde86891581508";
    private static final String API_KEY_PARAM_KEY = "api-key";
    private static final String PAGE_PARAM_KEY = "page";
    private static final String QUERY_PARAM_KEY = "q";
    private static final String BEGIN_DATE_PARAM_KEY = "begin_date";
    private static final String SORT_BY_PARAM_KEY = "sort";
    private static final String FILTERS_PARAM_KEY = "fq";

    private static final String HYPHEN = "-";
    private static final String SPACE = " ";
    private static final String NEWS_DESK_VALUE_FORMAT = "\"%s\"";
    private static final String NEWS_DESK_FORMAT = "news_desk:(%s)";
    private static final Function<String, String> NEWS_DESK_VALUE_TRANSFORM =
            new Function<String, String>() {
                @Nullable
                @Override
                public String apply(String input) {
                    return String.format(NEWS_DESK_VALUE_FORMAT, input);
                }
            };

    private AsyncHttpClient client;

    public ArticleClient() {
        this.client = new AsyncHttpClient();
    }

    // Method for accessing the search API
    public void getArticles(final String query, FilterSettings
            filterSettings, JsonHttpResponseHandler handler) {
        RequestParams requestParams = getRequestParams(query, filterSettings);
        client.get(API_SEARCH_URL, requestParams, handler);
    }

    /**
     * Returns a {@link RequestParams} object initialized with filter search settings, if any.
     */
    private RequestParams getRequestParams(String query, FilterSettings filterSettings) {
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM_KEY, API_KEY);
        params.put(PAGE_PARAM_KEY, 0);
        params.put(QUERY_PARAM_KEY, query);

        // filter params
        String beginDate = filterSettings.getBeginDate();
        if (!TextUtils.isEmpty(beginDate)) {
            params.put(BEGIN_DATE_PARAM_KEY, beginDate.replace(HYPHEN, EMPTY));
        }
        FilterSettings.SortOrder sortOrder = filterSettings.getSortOrder();
        if (sortOrder != FilterSettings.SortOrder.NONE) {
            params.put(SORT_BY_PARAM_KEY, sortOrder.name().toLowerCase());
        }
        HashSet<String> newsDeskValues = filterSettings.getNewsDeskValues();
        if (!newsDeskValues.isEmpty()) {
            Joiner joiner = Joiner.on(SPACE);
            String newsDeskString = joiner.join(Iterables.transform(newsDeskValues,
                    NEWS_DESK_VALUE_TRANSFORM));
            params.put(FILTERS_PARAM_KEY, String.format(NEWS_DESK_FORMAT, newsDeskString));
        }

        return params;
    }
}
