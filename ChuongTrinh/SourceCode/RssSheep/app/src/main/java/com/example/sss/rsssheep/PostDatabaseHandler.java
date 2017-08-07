package com.example.sss.rsssheep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by SSS on 02/05/2017.
 */

public class PostDatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    SQLiteDatabase db = null;

    // Database Name
    private static final String DATABASE_NAME = "Post_Data";

    // Contacts table name
    private static final String TABLE_POST = "Post_Data";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATE = "date";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_LINK = "link";
    private static final String KEY_RSS_GROUP = "rss_group";
    private static final String KEY_THUMB_URL = "thumb_url";
    private static final String KEY_BOOKMARK = "bookmark";
    private static final String KEY_POST_LIFE = "post_life";

    public PostDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_RSS_TABLE = "CREATE TABLE " + TABLE_POST + "(" + KEY_ID
                + " INTEGER PRIMARY KEY, " + KEY_TITLE + " TEXT, " + KEY_DATE
                + " TEXT, " + KEY_CONTENT + " TEXT, " + KEY_LINK + " TEXT, "
                + KEY_RSS_GROUP + " TEXT, " + KEY_THUMB_URL + " TEXT, "
                + KEY_BOOKMARK + " TEXT, " + KEY_POST_LIFE + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_RSS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_POST);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public String getDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Adding a new post in post table Function will check if a post
     * already existed in database. If existed will update the old one else
     * creates a new row
     * */
    public void addPost(PostData post) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, post.getPostTitle()); // post title
        values.put(KEY_DATE, post.getPostDate()); // post date
        values.put(KEY_CONTENT, post.getPostContent()); // post content
        values.put(KEY_LINK, post.getPostLink()); // post link
        values.put(KEY_RSS_GROUP, post.getPostRSSGroup()); // rss link url
        values.put(KEY_THUMB_URL, post.getPostThumbUrl()); // post thumb url
        values.put(KEY_BOOKMARK, false); // post bookmark
        values.put(KEY_POST_LIFE, getDateTime()); // post add to database date

        // Check if row already existed in database
        if (!isPostExists(db, post.getPostRSSGroup())) {
            // site not existed, create a new row
            db.insert(TABLE_POST, null, values);
            db.close();
        } else {
            // site already existed update the row
            updatePost(post);
            db.close();
        }
    }

    /**
     * Reading all rows from database
     * */
    public List<PostData> getAllPost() {
        List<PostData> postList = new ArrayList<PostData>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_POST
                + " ORDER BY id ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        try{
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    PostData post = new PostData();
                    post.setId(Integer.parseInt(cursor.getString(0)));
                    post.setPostTitle(cursor.getString(1));
                    post.setPostDate(cursor.getString(2));
                    post.setPostContent(cursor.getString(3));
                    post.setPostLink(cursor.getString(4));
                    post.setPostRSSGroup(cursor.getString(5));
                    post.setPostThumbUrl(cursor.getString(6));
                    post.setBookmark((cursor.getInt(7)) > 0);
                    post.setPostLife(cursor.getString(8));

                    // Adding contact to list
                    postList.add(post);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        // return contact list
        return postList;
    }

    public PostData getPost(String link) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_POST, new String[] { KEY_ID, KEY_TITLE, KEY_DATE,
                        KEY_CONTENT, KEY_LINK, KEY_RSS_GROUP, KEY_THUMB_URL, KEY_BOOKMARK,
                        KEY_POST_LIFE}, KEY_LINK + "=?",
                new String[] { link }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        PostData post = new PostData(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5), cursor.getString(6), (cursor.getInt(7)) > 0,
                cursor.getString(8));

        post.setId(Integer.parseInt(cursor.getString(0)));
        post.setPostTitle(cursor.getString(1));
        post.setPostDate(cursor.getString(2));
        post.setPostContent(cursor.getString(3));
        post.setPostLink(cursor.getString(4));
        post.setPostRSSGroup(cursor.getString(5));
        post.setPostThumbUrl(cursor.getString(6));
        post.setBookmark((cursor.getInt(7)) > 0);
        post.setPostLife(cursor.getString(8));

        cursor.close();
        db.close();
        return post;
    }

    /**
     * Reading a row (website) row is identified by row id
     * */
    public PostData getPost(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_POST, new String[] { KEY_ID, KEY_TITLE, KEY_DATE,
                KEY_CONTENT, KEY_LINK, KEY_RSS_GROUP, KEY_THUMB_URL, KEY_BOOKMARK,
                KEY_POST_LIFE}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        PostData post = new PostData(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5), cursor.getString(6), (cursor.getInt(7)) > 0,
                cursor.getString(8));

        post.setId(Integer.parseInt(cursor.getString(0)));
        post.setPostTitle(cursor.getString(1));
        post.setPostDate(cursor.getString(2));
        post.setPostContent(cursor.getString(3));
        post.setPostLink(cursor.getString(4));
        post.setPostRSSGroup(cursor.getString(5));
        post.setPostThumbUrl(cursor.getString(6));
        post.setBookmark((cursor.getInt(7)) > 0);
        post.setPostLife(cursor.getString(8));

        cursor.close();
        db.close();
        return post;
    }

    /**
     * Updating a single row row will be identified by rss link
     * */
    public int updatePost(PostData post) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, post.getPostTitle());
        values.put(KEY_DATE, post.getPostDate());
        values.put(KEY_CONTENT, post.getPostContent());
        values.put(KEY_LINK, post.getPostLink());
        values.put(KEY_RSS_GROUP, post.getPostRSSGroup());
        values.put(KEY_THUMB_URL, post.getPostThumbUrl());
        values.put(KEY_BOOKMARK, post.isBookmark());
        if(post.isBookmark()){
            values.put(KEY_POST_LIFE, "sheep");
        }
        else{
            values.put(KEY_POST_LIFE, post.getPostLife());
        }

        // updating row return
        int update = db.update(TABLE_POST, values, KEY_LINK + " = ?",
                new String[] { String.valueOf(post.getPostLink()) });
        db.close();
        return update;
    }

    /**
     * Deleting single row
     * */
    public void deletePost(PostData post) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POST, KEY_LINK + " = ?",
                new String[] { String.valueOf(post.getPostLink())});
        db.close();
    }

    public void deleteAllPostInSite(String rssSite){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_POST, new String[] { KEY_ID, KEY_TITLE, KEY_DATE,
                        KEY_CONTENT, KEY_LINK, KEY_RSS_GROUP, KEY_THUMB_URL, KEY_BOOKMARK,
                        KEY_POST_LIFE}, KEY_RSS_GROUP + " == ?",
                new String[] { rssSite }, null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                PostData post = new PostData(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5), cursor.getString(6), (cursor.getInt(7)) > 0,
                        cursor.getString(8));
                if(!post.isBookmark()) {
                    deletePost(post);
                }
                cursor.moveToNext();
            }
        }
    }

    /**
     * Delete post when it lasts more than n days
     * */
    public void autoDeleteAfterNDays(int n){
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String today = getDateTime();
        long second = n*86400;
        try{
            Date todayDate = dateFormat.parse(today);
            Cursor cursor = db.query(TABLE_POST, new String[] { KEY_ID, KEY_TITLE, KEY_DATE,
                            KEY_CONTENT, KEY_LINK, KEY_RSS_GROUP, KEY_THUMB_URL, KEY_BOOKMARK,
                            KEY_POST_LIFE}, KEY_POST_LIFE + " != ?",
                    new String[] { "sheep" }, null, null, null, null);
            if(cursor != null){
                cursor.moveToFirst();
                while(!cursor.isAfterLast()){
                    PostData post = new PostData(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                            cursor.getString(4), cursor.getString(5), cursor.getString(6), (cursor.getInt(7)) > 0,
                            cursor.getString(8));
                    Date postCreationDate = dateFormat.parse(cursor.getString(8));
                    long differenceInSeconds = (todayDate.getTime() - postCreationDate.getTime()) / 1000;
                    if(differenceInSeconds >= second)
                        deletePost(post);
                    cursor.moveToNext();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.close();
    }

    /**
     * Checking whether a post is already existed check is done by matching rss
     * link
     * */
    public boolean isPostExists(SQLiteDatabase db, String post_link) {

        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_POST
                + " WHERE link = '" + post_link + "'", new String[] {});
        boolean exists = (cursor.getCount() > 0);
        return exists;
    }
}
