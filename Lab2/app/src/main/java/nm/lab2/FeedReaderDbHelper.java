package nm.lab2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

import nm.lab2.FeedReaderContract.FeedEntry;

/**
 * Created by nmohamed on 9/22/2015. Class that helps create database for fragments to read from
 */
public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "FeedReader.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FeedEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(FeedEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    //writes to database
    public void writeDatabase(String input){
        SQLiteDatabase dbwrite = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.URL_COLUMN, input);

        dbwrite.insertOrThrow(
                FeedEntry.TABLE_NAME,
               null, // FeedEntry.URL_COLUMN,
                values);
        Log.d("Wrote to database:", FeedEntry.TABLE_NAME + "-----" + FeedEntry.URL_COLUMN + "---" + input);
        dbwrite.close();
    }

    //deletes image from database
    public void deleteDatabase(String url){
        SQLiteDatabase dbdelete = getWritableDatabase();
        Log.d("Deleting entry:",FeedEntry.TABLE_NAME + "---" + url);
        String selection = FeedEntry.URL_COLUMN + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(url)};
        dbdelete.delete(
                FeedEntry.TABLE_NAME,
                selection,
                selectionArgs);
        Log.d("entry deleted:", "-");
    }

    //reads images from database
    public ArrayList<String> readDatabase(){
        SQLiteDatabase dbread = getReadableDatabase();
        ArrayList<String> imageURLs = new ArrayList<>();
        String[] projection = {
                FeedEntry.URL_ID,
                FeedEntry.URL_COLUMN};

        //Cursor c = dbread.rawQuery("SELECT * FROM " + FeedEntry.TABLE_NAME, null);

        Cursor c = dbread.query(
                FeedEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        c.moveToFirst();
        while (!c.isAfterLast()){
            imageURLs.add(c.getString(1));
            Log.d("added 1 image", c.getString(1));
            c.moveToNext();
        }
        c.close();
        dbread.close();
        return imageURLs;
    }
}