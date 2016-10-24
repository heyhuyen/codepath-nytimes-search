package com.huyentran.nytsearch.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.huyentran.nytsearch.model.Article;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * DB helper class.
 */

public class ArticleDBHelper extends SQLiteOpenHelper {

    private static ArticleDBHelper articleDatabaseHelper;

    // Database Info
    private static final String DATABASE_NAME = "nytsDatabase";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    private static final String TABLE_ARTICLES = "articles";

    // Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_WEB_URL = "webUrl";
    private static final String KEY_HEADLINE = "headline";
    private static final String KEY_THUMBNAIL = "thumbnail";
    private static final String KEY_SNIPPET = "snippet";

    public static synchronized ArticleDBHelper getInstance(Context context) {
        if (articleDatabaseHelper == null) {
            articleDatabaseHelper = new ArticleDBHelper(context.getApplicationContext());
        }
        return articleDatabaseHelper;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private ArticleDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_ARTICLES +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_WEB_URL + " TEXT," +
                KEY_HEADLINE + " TEXT," +
                KEY_THUMBNAIL + " TEXT," +
                KEY_SNIPPET + " TEXT" +
                ")";

        db.execSQL(CREATE_POSTS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
            onCreate(db);
        }
    }

    /**
     * Retrieve all the articles from the database.
     * @return list of articles
     */
    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();

        String TASKS_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_ARTICLES);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
                    String webUrl = cursor.getString(cursor.getColumnIndex(KEY_WEB_URL));
                    String headline = cursor.getString(cursor.getColumnIndex(KEY_HEADLINE));
                    String thumbnail = cursor.getString(cursor.getColumnIndex(KEY_THUMBNAIL));
                    String snippet = cursor.getString(cursor.getColumnIndex(KEY_SNIPPET));
                    articles.add(new Article(id, webUrl, headline, thumbnail, snippet));
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get articles from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return articles;
    }

    /**
     * Persist given {@link Article} to the database.
     * @param article article to persist
     */
    public long addArticle(Article article) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        long id = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_WEB_URL, article.getWebUrl());
            values.put(KEY_HEADLINE, article.getHeadline());
            values.put(KEY_THUMBNAIL, article.getThumbnail());
            values.put(KEY_SNIPPET, article.getSnippet());

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            id = db.insertOrThrow(TABLE_ARTICLES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add article to database");
        } finally {
            db.endTransaction();
        }
        return id;
    }

    /**
     * Retrieve article from database
     * @return article, if it exists
     */
    public Article getArticle(Article article) {
        Article result = null;

        String SELECT_QUERY = String.format("SELECT * FROM %s WHERE webUrl=? AND headline=? AND " +
                "thumbnail=? AND snippet=?", TABLE_ARTICLES);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, new String[] {
                article.getWebUrl(),
                article.getHeadline(),
                article.getThumbnail(),
                article.getSnippet()
        });
        try {
            if (cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
                String webUrl = cursor.getString(cursor.getColumnIndex(KEY_WEB_URL));
                String headline = cursor.getString(cursor.getColumnIndex(KEY_HEADLINE));
                String thumbnail = cursor.getString(cursor.getColumnIndex(KEY_THUMBNAIL));
                String snippet = cursor.getString(cursor.getColumnIndex(KEY_SNIPPET));
                result = new Article(id, webUrl, headline, thumbnail, snippet);
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get article from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * Delete {@link Article} for the given primary key id from the database.
     * @param id primary key
     */
    public void deleteArticle(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, id);
            db.delete(TABLE_ARTICLES, KEY_ID + " = ?", new String[] { String.valueOf(id) });
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete article from database");
        } finally {
            db.endTransaction();
        }
    }
}