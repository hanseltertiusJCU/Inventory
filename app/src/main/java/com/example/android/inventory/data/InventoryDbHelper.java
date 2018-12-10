package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    /** Database file name */
    private static final String DATABASE_NAME = "inventories.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Create a constructor to construct a new instance of {@link InventoryDbHelper}.
     *
     * @param context of the app
     */
    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * This is called when the database is created.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_INVENTORIES_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InventoryEntry.COLUMN_INVENTORY_BRAND + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_INVENTORY_NAME + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_INVENTORY_PRICE + " REAL NOT NULL DEFAULT 0.00, " +
                InventoryEntry.COLUMN_INVENTORY_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                InventoryEntry.COLUMN_INVENTORY_PHONE_NUMBER + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_INVENTORY_EMAIL + " TEXT NOT NULL, " +
                InventoryEntry.COLUMN_INVENTORY_IMAGE + " BLOB NOT NULL" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_INVENTORIES_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int updatedVersion) {
        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
