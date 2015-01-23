package org.jboss.aerogear.syncdemo.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import org.jboss.aerogear.sync.ClientDocument;


public class SyncDatabase extends SQLiteOpenHelper {
    
    public static final String DB_NAME = "sync.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "documents";
    
    public static final String ID = BaseColumns._ID;
    public static final String DOC_ID = "docId";
    public static final String CLIENT_ID = "clientId";
    public static final String CONTENT = "content";

    public SyncDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        final String sql = String.format("create table %s (%s int primary key, %s text, %s text)",
                TABLE, ID, DOC_ID, CONTENT);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE);
        onCreate(db);
    }
    
    public void saveClientDocument(final ClientDocument<String> document) {
        final SQLiteDatabase db = getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(DOC_ID, document.id());
        values.put(CLIENT_ID, document.clientId());
        values.put(CONTENT, document.content());
        db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    
}
