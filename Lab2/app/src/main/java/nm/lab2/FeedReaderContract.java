package nm.lab2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by nmohamed on 9/22/2015.
 */
//Schema/contract for SQL database
public final class FeedReaderContract{
    public FeedReaderContract(){}
    public abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String URL_COLUMN= "url";
        public static final String URL_ID= "_id";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                        FeedEntry.URL_ID + " INTEGER PRIMARY KEY," +
                        FeedEntry.URL_COLUMN + " TEXT )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    }
}
