package edu.uw.tacoma.jwolf059.pubcrawler.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import edu.uw.tacoma.jwolf059.pubcrawler.R;

/**
 * The SQLite database class to save pubs in the crawl that
 * the user created or was randomly created.
 */

public class CrawlDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Crawl.db";

    private CrawlDBHelper mCrawlDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public CrawlDB(Context context) {
        mCrawlDBHelper = new CrawlDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mCrawlDBHelper.getWritableDatabase();
    }

    /**
     * Insert a pub into the SQLite database Crawl table.
     * @param name the pub's name.
     * @param latitude the pub's location latitude.
     * @param longitude the pub's location longitude.
     * @param rating the pub's rating.
     * @param address the pub's address.
     * @param hasFood whether the pub has food.
     * @return whether the pub was successfully inserted into the Crawl table.
     */
    public boolean insertPub(String address, String name, double latitude, double longitude,
                             double rating, String hasFood) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("address", address);
        contentValues.put("name", name);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("rating", rating);
        contentValues.put("hasFood", hasFood);

        long rowId = mSQLiteDatabase.insert("Crawl", null, contentValues);
        return rowId != -1;
    }

    /**
     * Close the SQLite database.
     */
    public void closeDB() {
        mSQLiteDatabase.close();
    }

    /**
     * Delete all the data from the CRAWL_TABLE
     */
    public void updateCrawl() {
        mCrawlDBHelper.onUpgrade(mSQLiteDatabase, 1, 1);
    }




    class CrawlDBHelper extends SQLiteOpenHelper {

        private final String CREATE_CRAWL_SQL;

        private final String DROP_CRAWL_SQL;



        public CrawlDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_CRAWL_SQL = context.getString(R.string.CREATE_CRAWL_SQL);
            DROP_CRAWL_SQL = context.getString(R.string.DROP_CRAWL_SQL);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_CRAWL_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_CRAWL_SQL);
            onCreate(sqLiteDatabase);
        }
    }
}

