package moravec_tomas.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper{

    private static final int DB_VER = 1;

    public static final String DB_FILE_NAME = "Todo.db";
    public static final String DB_NAME = "Task";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_IMAGEPATH = "IMAGEPATH";
    public static final String COLUMN_DATE_FROM = "DATE_FROM";
    public static final String COLUMN_DATE_TO = "DATE_TO";

    /**
     * Constructor
     * @param context
     */
    public Database(Context context) {
        super(context, DB_FILE_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format(
                "CREATE TABLE %s " + "(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s INTEGER, " +
                    "%s INTEGER" +
                ")",
                DB_NAME,
                COLUMN_NAME,
                COLUMN_DESCRIPTION,
                COLUMN_IMAGEPATH,
                COLUMN_DATE_FROM,
                COLUMN_DATE_TO
        );
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DELETE TABLE IF EXIST %s", DB_NAME);
        db.execSQL(query);
        onCreate(db);
    }

    /**
     * Insert one row into table
     * @param name Name of row
     */
    public void InsertRow(String name){

        // Open database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Add values
        values.put(COLUMN_NAME, name);

        // Insert values
        db.insertWithOnConflict(DB_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // Close database
        db.close();
    }

    /**
     * Insert one row into table
     * @param item Item object
     */
    public void InsertRow(Item item){

        // Open database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Add values
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_IMAGEPATH, item.getImagePath());
        values.put(COLUMN_DATE_FROM, item.getDate_from());
        values.put(COLUMN_DATE_TO, item.getDate_to());


        // Insert values
        db.insertWithOnConflict(DB_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // Close database
        db.close();
    }

    /**
     * Insert one row into table
     * @param name Name of row
     * @param description Detail description
     * @param date_from Date of creation
     * @param date_to Date of ending
     * @param imagePath Path to image
     */
    public void InsertRow(String name, String description, long date_from, long date_to, String imagePath){

        // Open database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Add values
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_IMAGEPATH, imagePath);
        values.put(COLUMN_DATE_FROM, date_from);
        values.put(COLUMN_DATE_TO, date_to);

        // Insert values
        db.insertWithOnConflict(DB_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // Close database
        db.close();
    }

    /**
     * Delete one row from table
     * @param name Text in column to delete
     */
    public void DeleteRow(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_NAME, COLUMN_NAME + " = ?", new String[]{name});  // Delete specific row from table, based on column name
        db.close();
    }

    /**
     * Get items from database as ArrayList of items
     * @return
     */
    public ArrayList<Item> GetItems(){
        ArrayList<Item> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + DB_NAME;

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            try{

                // Looping through all rows
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                        String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                        String image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGEPATH));
                        long date_from = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_FROM));
                        long date_to = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_TO));

                        Item item = new Item(name, description, image, date_from, date_to);
                        list.add(item);
                    } while (cursor.moveToNext());
                }

            } finally {
                try { cursor.close(); } catch (Exception ignore) {}  // Close cursor
            }

        } finally {
            try { db.close(); } catch (Exception ignore) {}  // Close database
        }

        //Cursor cursor = db.query(DB_NAME, new String[]{COLUMN_NAME/*,COLUMN_DESCRIPTION,COLUMN_IMAGEPATH,COLUMN_DATE_FROM,COLUMN_DATE_TO*/}, null, null, null, null, null);
        //Cursor cursor = db.rawQuery("SELECT * FROM " + DB_FILE_NAME, null);
        //Cursor cursor = db.rawQuery("SELECT * FROM " + DB_NAME, null);
/*
        if (cursor.getCount() != 0){
            while (cursor.moveToNext()){
                //String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                //String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                //String image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGEPATH));
                //long date_from = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_FROM));
                //long date_to = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_TO));

                //Item item = new Item(name, description, image, date_from, date_to);
                Item item = new Item("a" , "", "", 0, 0);
                list.add(item);
            }
        }*/

        return list;
    }

    public void ClearDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + DB_NAME);
    }
}
