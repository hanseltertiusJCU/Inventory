package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

public class InventoryProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    /** Database Helper object */
    private InventoryDbHelper inventoryDbHelper;

    /** URI matcher code for the content URI for the inventories table */
    private static final int INVENTORIES = 100;

    /** URI matcher code for the content URI for a single pet in the inventories table */
    private static final int INVENTORY_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        uriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORIES, INVENTORIES);
        uriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORIES + "/#", INVENTORY_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        inventoryDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Get readable database
        SQLiteDatabase database = inventoryDbHelper.getReadableDatabase();

        // Create a new Cursor object that hold the result of the query
        Cursor cursor;

        // Initiate an int variable in order to compare with the URI matcher code
        int match = uriMatcher.match(uri);

        // Check if URI matcher can match the URI
        switch (match){
            case INVENTORIES:
                // For the INVENTORIES code, query the inventories table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INVENTORY_ID:
                // For the INVENTORY_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.inventory/inventories/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the inventories table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }


        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case INVENTORIES:
                return insertInventory(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    private Uri insertInventory(Uri uri, ContentValues values){

        // Create and/or open a database to write from it
        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();

        // Check that the brand is not null
        String brand = values.getAsString(InventoryEntry.COLUMN_INVENTORY_BRAND);
        if (brand == null){
            throw new IllegalArgumentException("Inventory requires the brand value.");
        }

        // Check that the name is not null
        String name = values.getAsString(InventoryEntry.COLUMN_INVENTORY_NAME);
        if (name == null){
            throw new IllegalArgumentException("Inventory requires the name value.");
        }

        // Check that the price is more than or equal to 0
        Double price = values.getAsDouble(InventoryEntry.COLUMN_INVENTORY_PRICE);
        if (price != null && price < 0){
            throw new IllegalArgumentException("The price for inventory must be 0 or more.");
        }

        // Check that the quantity is more than or equal to 0
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        if (quantity != null && quantity < 0){
            throw new IllegalArgumentException("The quantity for inventory must be 0 or more.");
        }

        // Check that the phone number is not null
        String phoneNumber = values.getAsString(InventoryEntry.COLUMN_INVENTORY_PHONE_NUMBER);
        if (phoneNumber == null){
            throw new IllegalArgumentException("Inventory requires the supplier phone number.");
        }

        // Check that the e-mail is not null
        String email = values.getAsString(InventoryEntry.COLUMN_INVENTORY_EMAIL);
        if (email == null){
            throw new IllegalArgumentException("Inventory requires the supplier email.");
        }

        // By calling the insert method, we return the id in order to return the URI with the id
        // and to get the new inventory
        long id = db.insert(InventoryEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match){
            case INVENTORIES:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                // For the INVENTORY_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.inventory/inventories/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update inventory in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more inventories).
     * Return the number of rows that were successfully updated.
     */
    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Check if the updated value involves the respective key(s)
        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_BRAND)){
            // Check if brand is not null
            String brand = values.getAsString(InventoryEntry.COLUMN_INVENTORY_BRAND);
            if (brand == null) {
                throw new IllegalArgumentException("Inventory requires the brand value.");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_NAME)){
            // Check if name is not null
            String brand = values.getAsString(InventoryEntry.COLUMN_INVENTORY_NAME);
            if (brand == null) {
                throw new IllegalArgumentException("Inventory requires the name value.");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_PRICE)){
            // Check that the price is more than or equal to 0
            Double price = values.getAsDouble(InventoryEntry.COLUMN_INVENTORY_PRICE);
            if (price != null && price < 0){
                throw new IllegalArgumentException("The price for inventory must be 0 or more.");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_QUANTITY)){
            // Check that the quantity is more than or equal to 0
            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            if (quantity != null && quantity < 0){
                throw new IllegalArgumentException("The quantity for inventory must be 0 or more.");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_PHONE_NUMBER)){
            // Check that the phone number is not null
            String phoneNumber = values.getAsString(InventoryEntry.COLUMN_INVENTORY_PHONE_NUMBER);
            if (phoneNumber == null){
                throw new IllegalArgumentException("Inventory requires the supplier phone number.");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_EMAIL)){
            // Check that the e-mail is not null
            String email = values.getAsString(InventoryEntry.COLUMN_INVENTORY_EMAIL);
            if (email == null){
                throw new IllegalArgumentException("Inventory requires the supplier email.");
            }
        }

        // Get writable database by calling getWritableDatabase method
        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();

        // Call the SQLiteDatabase update method to return how many row(s) is/are updated
        int updatedRows = db.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        // If there is no ContentValues to update, then return no updated rows (updatedRows = 0)
        if (values.size() == 0){
            return 0;
        }

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (updatedRows != 0){
            // Notify the listeners that the data has changed for the pet content URI
            // uri: content://com.example.android.inventory/inventories
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of updated rows
        return updatedRows;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
