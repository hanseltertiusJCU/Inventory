package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

        // Create and/or open a database to read from it
        SQLiteDatabase db = inventoryDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM inventories"
        // to get a Cursor that contains all rows from the inventories table.
        Cursor cursor = db.rawQuery("SELECT * FROM " +
                InventoryEntry.TABLE_NAME, null);

        try{
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_inventory);
            displayView.setText("Number of rows in inventories database table: " + cursor.getCount());
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
        // Gets the database in write mode
        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();

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
        // *) The first argument for db.insert() is the inventories table name.
        // *) The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty, which means that the
        // framework will not insert a row when there are no values.
        // *) The third argument is the ContentValues object containing the info for Samsung TV.
        long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, dummyValues);

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
