package com.example.android.inventory;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

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

    /** int value for setup default quantity */
    private int quantityInventory;



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

        // Setup string value to get the default text of EditText value
        String value = mQuantityEditText.getText().toString();

        // Set the value for int variable based on default text
        quantityInventory = Integer.parseInt(value);

        // Reference to button
        Button quantityDownButton = (Button) findViewById(R.id.decrease_quantity_button);
        Button quantityUpButton = (Button) findViewById(R.id.increase_quantity_button);

        quantityDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantityInventory > 0){
                    quantityInventory -= 1;
                    mQuantityEditText.setText("" + quantityInventory);
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot enter negative values, " +
                            "the quantity must be more than or equal to 0", Toast.LENGTH_SHORT).show();
                }
            }
        });

        quantityUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                quantityInventory += 1;
                mQuantityEditText.setText("" + quantityInventory);
            }
        });
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
        double priceValue;
        // Check if priceString has values, if not set the price value to 0
        if(priceString.length() != 0){
            priceValue = Double.parseDouble(priceString);
        } else {
            priceValue = 0;
        }

        int quantityValue;
        String quantityString = mQuantityEditText.getText().toString().trim();
        // Check if quantityString has values, if not set the price value to 0
        if(quantityString.length() != 0){
            quantityValue = Integer.parseInt(quantityString);
        } else {
            quantityValue = 0;
        }
        String phoneString = mPhoneNumberEditText.getText().toString().trim();
        String emailString = mEmailEditText.getText().toString().trim();

        // Use toast message when there is no string value in edit text
        if (TextUtils.isEmpty(brandString)){
            Toast.makeText(this, "Cannot enter empty brand value. " +
                            "Please insert a brand." ,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nameString)){
            Toast.makeText(this, "Cannot enter empty name value. " +
                            "Please insert a name." ,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(priceString)){
            Toast.makeText(this, "Cannot enter empty price value. " +
                            "Please insert the appropriate price value." ,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(quantityString)){
            Toast.makeText(this, "Cannot enter empty quantity value. " +
                            "Please insert the appropriate quantity." ,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phoneString)){
            Toast.makeText(this, "Cannot enter empty phone number. " +
                            "Please insert your supplier phone number." ,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(emailString)){
            Toast.makeText(this, "Cannot enter empty email. " +
                            "Please insert your supplier email." ,
                    Toast.LENGTH_SHORT).show();
            return;
        }



        // Create a ContentValues object where column names are the keys,
        // and inventory attributes from the editor are the values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryEntry.COLUMN_INVENTORY_BRAND, brandString);
        contentValues.put(InventoryEntry.COLUMN_INVENTORY_NAME, nameString);
        contentValues.put(InventoryEntry.COLUMN_INVENTORY_PRICE, priceValue);
        contentValues.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantityValue);
        contentValues.put(InventoryEntry.COLUMN_INVENTORY_PHONE_NUMBER, phoneString);
        contentValues.put(InventoryEntry.COLUMN_INVENTORY_EMAIL, emailString);

        Uri newUri = getContentResolver().insert(
                InventoryEntry.CONTENT_URI,     // Content URI from InventoryEntry
                contentValues                   // The values to insert
        );

        // Check if the Uri resulted from insert method in content resolver is null.
        if(newUri != null){
            Toast.makeText(this, "Inventory successfully saved" , Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "There is an error with saving an inventory" , Toast.LENGTH_SHORT).show();
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
