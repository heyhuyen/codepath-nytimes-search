package com.huyentran.nytsearch.utils;

import android.support.annotation.Nullable;

import com.google.common.base.Function;

import java.util.Calendar;

/**
 * Shared constants.
 */
public final class Constants {
    public static final String ARTICLE_KEY = "article";
    public static final String FILTER_SETTINGS_KEY = "filterSettings";

    public static final int FILTER_CODE = 5;
    public static final String DATE_PICKER_KEY = "datePicker";
    public static final String DATE_KEY = "date";

    // NY Time Search API
    public static final String NYT_SEARCH_API_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    public static final String API_KEY = "e9f2d2081760448296bde86891581508";
    public static final String API_KEY_PARAM_KEY = "api-key";
    public static final String PAGE_PARAM_KEY = "page";
    public static final String QUERY_PARAM_KEY = "q";
    public static final String BEGIN_DATE_PARAM_KEY = "begin_date";
    public static final String SORT_BY_PARAM_KEY = "sort";
    public static final String FILTERS_PARAM_KEY = "fq";

    public static final String HYPHEN = "-";
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    private static final String NEWS_DESK_VALUE_FORMAT = "\"%s\"";
    public static final String NEWS_DESK_FORMAT = "news_desk:(%s)";
    public static final Function<String, String> NEWS_DESK_VALUE_TRANSFORM = new Function<String, String>() {
        @Nullable
        @Override
        public String apply(String input) {
            return String.format(NEWS_DESK_VALUE_FORMAT, input);
        }
    };
    public static final String NEWS_DESK_ARTS = "Arts";
    public static final String NEWS_DESK_FASHION = "Fashion & Style";
    public static final String NEWS_DESK_SPORTS = "Sports";

    public static final String RESPONSE_KEY = "response";
    public static final String DOCS_KEY = "docs";

    public static final String MIN_DATE_STR = "1851-09-18";
    public static final Calendar MIN_DATE = DateUtils.getDateFromString(MIN_DATE_STR);
    public static final long MIN_DATE_MILLIS = MIN_DATE.getTimeInMillis();
}
