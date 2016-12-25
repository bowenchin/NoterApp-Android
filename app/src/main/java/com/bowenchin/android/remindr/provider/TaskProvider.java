package com.bowenchin.android.remindr.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by bowenchin on 28/12/15.
 */
public class TaskProvider extends ContentProvider {

    //Content Provider Uri and Authority
    public static final String AUTHORITY = "com.bowenchin.android.remindr.provider.TaskProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/task");

    //MIME types used for listing tasks or looking up a single task
    private static final String TASKS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.bowenchin.android.remindr.tasks";
    private static final String TASK_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.bowenchin.android.remindr.task";

    //UriMatcher stuff
    private static final int LIST_TASK = 0;
    private static final int ITEM_TASK = 1;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    //Database Columns
    public static final String COLUMN_TASKID = "_id";
    public static final String COLUMN_DATE_TIME = "task_date_time";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_NOTES = "notes";
    //public static final String COLUMN_URGENCY = "urgency_level";

    //Database Related Contents
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "tasks";

    //The database itself
    SQLiteDatabase db;

    //Build up a UriMathcer for search suggestion and shortcut refresh queries
    private static UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "task", LIST_TASK);
        matcher.addURI(AUTHORITY, "task/#", ITEM_TASK);
        return matcher;
    }

    //This method is required in order to query the supported types
    @Override
    public String getType(Uri uri){
        switch(URI_MATCHER.match(uri)){
            case LIST_TASK:
                return TASKS_MIME_TYPE;
            case ITEM_TASK:
                return TASK_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    //This method is called when someone wants to insert something into our content provider
    @Override
    public Uri insert(Uri uri, ContentValues values){
        //you can't choose your own task id
        if(values.containsKey(COLUMN_TASKID))
            throw new UnsupportedOperationException();
        long id = db.insertOrThrow(DATABASE_TABLE, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri,id);
    }

    //This method is called when someone wants to update something in our content provider
    @Override
    public int update(Uri uri, ContentValues values, String ignored1, String[] ignored2){
        //you can't change a task id
        if(values.containsKey(COLUMN_TASKID))
            throw new UnsupportedOperationException();

        int count = db.update(DATABASE_TABLE, values, COLUMN_TASKID + "=?",
                new String[]{Long.toString(ContentUris.parseId(uri))});

        if(count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    //This method is called when someone wants to delete something from our content provider
    @Override
    public int delete(Uri uri, String ignored1, String[] ignored2){
        int count = db.delete(DATABASE_TABLE, COLUMN_TASKID + "=?",
                new String[]{Long.toString(ContentUris.parseId(uri))});
        if(count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    //This method is called when someone wants to read something form our content provider.
    //We'll turn around and ask our database for the information, and then return it in a Cursor
    @Override
    public Cursor query(Uri uri, String[] ignored1, String selection, String[] selectionArgs, String sortOrder){
        String[] projection = new String[]{
                COLUMN_TASKID,
                COLUMN_TITLE,
                COLUMN_NOTES,
                COLUMN_DATE_TIME};
        Cursor c;
        switch (URI_MATCHER.match(uri)) {
            case LIST_TASK:
                c = db.query(DATABASE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_TASK:
                c = db.query(DATABASE_TABLE, projection, COLUMN_TASKID + "=?", new String[]{
                        Long.toString(ContentUris.parseId(uri))}, null, null, null, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }



    @Override
    public boolean onCreate(){
        //Grab a connection to our database
        db = new DatabaseHelper(getContext()).getWritableDatabase();
        return true;
    }

    //A helper class which knows how to create and update database.
    protected static class DatabaseHelper extends SQLiteOpenHelper{
        static final String DATABASE_CREATE =
                "create table " + DATABASE_TABLE + " (" +
                        COLUMN_TASKID + " integer primary key autoincrement, " +
                        COLUMN_TITLE + " text not null, " +
                        COLUMN_NOTES + " text not null, " +
                        COLUMN_DATE_TIME + " integer not null);";
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            throw new UnsupportedOperationException();
        }
    }
}
