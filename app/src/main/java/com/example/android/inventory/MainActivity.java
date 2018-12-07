package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the inventory data loader */
    private static final int INVENTORY_LOADER = 0;

    private InventoryCursorAdapter inventoryCursorAdapter;

    private Uri newUri;

    /** Int variable for showing how many rows does the whole database deleted */
    private int rowsDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find ListView to populate
        ListView listViewItems = (ListView) findViewById(R.id.list_view_inventory);

        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Set up an intent to open up EditorActivity
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                // Create specific content URI for editing the specific inventory
                Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                // Set the URI into the intent
                intent.setData(uri);
                // Start the EditorActivity
                startActivity(intent);
            }
        });

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        listViewItems.setEmptyView(emptyView);

        // Setup cursor adapter using cursor from last step
        // There is no inventory data yet (until the loader finishes)
        // so pass in null for the Cursor.
        inventoryCursorAdapter = new InventoryCursorAdapter(this, null);
        // Attach cursor adapter to the ListView
        listViewItems.setAdapter(inventoryCursorAdapter);

        // Initialize the loader
        getSupportLoaderManager().initLoader(INVENTORY_LOADER, null, this);

    }

    /**
     * Helper method to insert hardcoded dummy inventory data into the database.
     */
    private void insertInventory(){
        // Create a ContentValues object where column names are the keys,
        // and Samsung TV's attributes are the values.
        ContentValues dummyValues = new ContentValues();
        dummyValues.put(InventoryEntry.COLUMN_INVENTORY_BRAND, "Samsung");
        dummyValues.put(InventoryEntry.COLUMN_INVENTORY_NAME, "32” Class N5300");
        dummyValues.put(InventoryEntry.COLUMN_INVENTORY_PRICE, 199.99);
        dummyValues.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, 2);
        dummyValues.put(InventoryEntry.COLUMN_INVENTORY_PHONE_NUMBER, "(888) 3386902");
        dummyValues.put(InventoryEntry.COLUMN_INVENTORY_EMAIL, "samsungbusiness@gmail.com");

        // Insert a new row for Samsung TV's in the database, returning the ID of that new row.
        // *) The first argument for db.insert() is the content URI from InventoryEntry.
        // *) The second argument is the ContentValues object containing the info for Samsung TV.
        newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, dummyValues);

    }

//    public void clickSaleItem(long id, int quantity){
//        inventoryProvider.sellInventoryItem(id, quantity);
//        getSupportLoaderManager().restartLoader(INVENTORY_LOADER, null, this);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on the menu option in the app bar overflow menu.
        switch (item.getItemId()){
            // Respond when clicked "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertInventory();
                Toast.makeText(this, "Dummy data insertion successful.",
                        Toast.LENGTH_SHORT).show();
                return true;
            // Respond when clicked "Delete all inventories" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete all inventories.
                deleteAllInventories();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the inventory.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Helper method to delete all inventories in the database.
     */
    private void deleteAllInventories() {
        rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_INVENTORY_BRAND,
                InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryEntry.COLUMN_INVENTORY_PHONE_NUMBER,
                InventoryEntry.COLUMN_INVENTORY_EMAIL
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Update {@link InventoryCursorAdapter} with this new cursor containing updated inventory data
        inventoryCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        inventoryCursorAdapter.swapCursor(null);
    }
}
