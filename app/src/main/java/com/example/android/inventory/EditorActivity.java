package com.example.android.inventory;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;
import com.example.android.inventory.data.InventoryDbHelper;

public class EditorActivity extends AppCompatActivity {

    /** EditText field to enter the inventory's brand */
    private EditText mBrandEditText;

    /** EditText field to enter the inventory's name */
    private EditText mNameEditText;

    /** EditText field to enter the inventory's price */
    private EditText mPriceEditText;

    /** EditText field to enter the inventory's quantity */
    private EditText mQuantityEditText;

    /** EditText field to enter the inventory's phone number */
    private EditText mPhoneNumberEditText;

    /** EditText field to enter the inventory's E-mail */
    private EditText mEmailEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mBrandEditText = (EditText) findViewById(R.id.edit_product_brand);
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mPhoneNumberEditText = (EditText) findViewById(R.id.edit_product_phone_number);
        mEmailEditText = (EditText) findViewById(R.id.edit_product_email);
    }

    /**
     * Get user input from editor and save new inventory into database.
     */
    private void insertInventory(){
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String brandString = mBrandEditText.getText().toString().trim();
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        double priceValue = Double.parseDouble(priceString);
        String quantityString = mQuantityEditText.getText().toString().trim();
        int quantityValue = Integer.parseInt(quantityString);
        String phoneString = mPhoneNumberEditText.getText().toString().trim();
        String emailString = mEmailEditText.getText().toString().trim();

        // Create database helper
        InventoryDbHelper inventoryDbHelper = new InventoryDbHelper(this);

        // Get the database in write mode
        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and inventory attributes from the editor are the values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryEntry.COLUMN_INVENTORY_BRAND, brandString);
        contentValues.put(InventoryEntry.COLUMN_INVENTORY_NAME, nameString);
        contentValues.put(InventoryEntry.COLUMN_INVENTORY_PRICE, priceValue);
        contentValues.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantityValue);
        contentValues.put(InventoryEntry.COLUMN_INVENTORY_PHONE_NUMBER, phoneString);
        contentValues.put(InventoryEntry.COLUMN_INVENTORY_EMAIL, emailString);

        // Insert a new row for inventory in the database, returning the ID of that new row.
        long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, contentValues);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving inventory",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Inventory saved with row id: " +
                    newRowId, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertInventory();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
