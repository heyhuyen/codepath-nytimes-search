package com.huyentran.nytsearch.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

import static com.huyentran.nytsearch.utils.Constants.EMPTY;

/**
 * Article model.
 */
public class Article implements Serializable {
    private static final String WEB_URL_KEY = "web_url";
    private static final String HEADLINE_KEY = "headline";
    private static final String MAIN_HEADLINE_KEY = "main";
    private static final String MULTIMEDIA_KEY = "multimedia";
    private static final String URL_KEY = "url";
    private static final String THUMBNAIL_URL_FORMAT = "http://www.nytimes.com/%s";

    private String webUrl;
    private String headline;
    private String thumbnail;

    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString(WEB_URL_KEY);
            this.headline = jsonObject.getJSONObject(HEADLINE_KEY).getString(MAIN_HEADLINE_KEY);

            JSONArray multimedia = jsonObject.getJSONArray(MULTIMEDIA_KEY);
            if (multimedia.length() > 0) {
                JSONObject multimediaJSON = multimedia.getJSONObject(0);
                this.thumbnail =  String.format(THUMBNAIL_URL_FORMAT,
                        multimediaJSON.getString(URL_KEY));
            } else {
                this.thumbnail = EMPTY;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializes a JSONArray of article results into a list of {@link Article} objects.
     */
    public static ArrayList<Article> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Article> results = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                results.add(new Article(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    public String getWebUrl() {
        return this.webUrl;
    }

    public String getHeadline() {
        return this.headline;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }
}
