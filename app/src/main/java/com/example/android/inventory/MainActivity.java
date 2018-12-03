package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;
import com.example.android.inventory.data.InventoryDbHelper;

public class MainActivity extends AppCompatActivity {

    private InventoryDbHelper inventoryDbHelper;

    private Uri newUri;

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

        // To access our database, we instantiate the child class of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        inventoryDbHelper = new InventoryDbHelper(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDbInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDbInfo(){

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


        // Perform a query on content URI
        Cursor cursor = getContentResolver().query(InventoryEntry.CONTENT_URI, // the URI to query
                projection, // The columns to return
                null, // The columns for WHERE clause
                null, // The values for WHERE clause
                null // The sort order
        );

        try{
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_inventory);
            displayView.setText("Number of rows in inventories database table: " + cursor.getCount() + " inventories.\n\n");

            // Append the column title into the displayView, it's like forming the table
            displayView.append(InventoryEntry._ID + " - " +
                    InventoryEntry.COLUMN_INVENTORY_BRAND + " - " +
                    InventoryEntry.COLUMN_INVENTORY_NAME + " - " +
                    InventoryEntry.COLUMN_INVENTORY_PRICE + " - " +
                    InventoryEntry.COLUMN_INVENTORY_QUANTITY + " - " +
                    InventoryEntry.COLUMN_INVENTORY_PHONE_NUMBER + " - " +
                    InventoryEntry.COLUMN_INVENTORY_EMAIL + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int brandColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_BRAND);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PHONE_NUMBER);
            int emailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_EMAIL);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int or double value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentBrand = cursor.getString(brandColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                double currentPrice = cursor.getDouble(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentPhone = cursor.getString(phoneColumnIndex);
                String currentEmail = cursor.getString(emailColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentBrand + " - " +
                        currentName + " - " +
                        "$" + currentPrice + " - " +
                        currentQuantity + " - " +
                        currentPhone + " - " +
                        currentEmail));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
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
                displayDbInfo();
                return true;
            // Respond when clicked "Delete all inventories" menu option
            case R.id.action_delete_all_entries:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
