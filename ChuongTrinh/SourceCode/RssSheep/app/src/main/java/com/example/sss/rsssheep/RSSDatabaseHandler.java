package com.example.sss.rsssheep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SSS on 30/04/2017.
 */

public class RSSDatabaseHandler extends SQLiteOpenHelper{
    // Database Version
    private static final int DATABASE_VERSION = 1;
    SQLiteDatabase db = null;

    // Database Name
    private static final String DATABASE_NAME = "RSS_Sites";

    // Contacts table name
    private static final String TABLE_RSS = "RSS_Sites";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_GROUP =  "group_site";
    //private static final String KEY_LINK = "link";
    private static final String KEY_RSS_LINK = "rss_link";
    //private static final String KEY_DESCRIPTION = "description";

    public RSSDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_RSS_TABLE = "CREATE TABLE " + TABLE_RSS + "(" + KEY_ID
                + " INTEGER PRIMARY KEY, " + KEY_TITLE + " TEXT, " + KEY_GROUP
                + " TEXT, " + KEY_RSS_LINK + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_RSS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RSS);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    /**
     * Adding a new website in websites table Function will check if a site
     * already existed in database. If existed will update the old one else
     * creates a new row
     * */
    public void addSite(Website site) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, site.getTitle()); // site title
        values.put(KEY_GROUP, site.getGroup()); // site group
        values.put(KEY_RSS_LINK, site.getRssLink()); // rss link url

        // Check if row already existed in database
        if (!isSiteExists(db, site.getRssLink())) {
            // site not existed, create a new row
            db.insert(TABLE_RSS, null, values);
            db.close();
        } else {
            // site already existed update the row
            updateSite(site);
            db.close();
        }
    }

    /**
     * Reading all rows from database
     * */
    public List<Website> getAllSites() {
        List<Website> siteList = new ArrayList<Website>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_RSS
                + " ORDER BY id ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Website site = new Website();
                site.setId(Integer.parseInt(cursor.getString(0)));
                site.setTitle(cursor.getString(1));
                site.setGroup(cursor.getString(2));
                site.setRssLink(cursor.getString(3));
                // Adding contact to list
                siteList.add(site);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return contact list
        return siteList;
    }

    public Website getSite(String rssLink){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RSS, new String[] { KEY_ID, KEY_TITLE, KEY_GROUP,
                        KEY_RSS_LINK}, KEY_RSS_LINK + "=?",
                new String[] { rssLink }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Website site = new Website(cursor.getString(1), cursor.getString(2), cursor.getString(3));

        site.setId(Integer.parseInt(cursor.getString(0)));
        site.setTitle(cursor.getString(1));
        site.setGroup(cursor.getString(2));
        site.setRssLink(cursor.getString(3));
        cursor.close();
        db.close();
        return site;
    }

    /**
     * Updating a single row row will be identified by rss link
     * */
    public int updateSite(Website site) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, site.getTitle());
        values.put(KEY_GROUP, site.getGroup());
        values.put(KEY_RSS_LINK, site.getRssLink());

        // updating row return
        int update = db.update(TABLE_RSS, values, KEY_RSS_LINK + " = ?",
                new String[] { String.valueOf(site.getRssLink()) });
        db.close();
        return update;
    }

    /**
     * Reading a row (website) row is identified by row id
     * */
    public Website getSite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RSS, new String[] { KEY_ID, KEY_TITLE, KEY_GROUP,
                        KEY_RSS_LINK}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Website site = new Website(cursor.getString(1), cursor.getString(2), cursor.getString(3));

        site.setId(Integer.parseInt(cursor.getString(0)));
        site.setTitle(cursor.getString(1));
        site.setGroup(cursor.getString(2));
        site.setRssLink(cursor.getString(3));
        cursor.close();
        db.close();
        return site;
    }

    public int getGroupCount(){
        int cnt = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true, TABLE_RSS, new String[]{KEY_ID, KEY_TITLE, KEY_GROUP,
        KEY_RSS_LINK}, null, null, KEY_GROUP, null, null, null);
        cnt = cursor.getCount();
        /*while(cursor.isAfterLast()){
            cnt++;
            cursor.moveToNext();
        }*/
        return cnt;
    }

    /**
     * Deleting single row
     * */
    public void deleteSite(Website site) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RSS, KEY_ID + " = ?",
                new String[] { String.valueOf(site.getId())});
        db.close();
    }

    /**
     * Checking whether a site is already existed check is done by matching rss
     * link
     * */
    public boolean isSiteExists(SQLiteDatabase db, String rss_link) {

        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_RSS
                + " WHERE rss_link = '" + rss_link + "'", new String[] {});
        boolean exists = (cursor.getCount() > 0);
        return exists;
    }
}
