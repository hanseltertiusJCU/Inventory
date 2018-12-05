package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

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

    /** URI for passing the data into */
    private Uri currentInventoryUri;

    /** Boolean for ensure that everything is being fulfilled */
    private boolean fulfilledTexts = false;

    /** Boolean for blank editor values (unchanged from default) */
    private boolean blankEditorValues = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Get the intent from MainActivity
        Intent intent = getIntent();

        // Get the associated URI by getData method from the respective intent
        currentInventoryUri = intent.getData();

        // Check if the current URI is null for setting up the Activity title and optionally initiate
        // the loader
        if (currentInventoryUri == null){
            // Set the title for the EditorActivity that shows create a new inventory
            this.setTitle("Add a new inventory");
        } else {
            // Set the title for the EditorActivity that shows edit an existing inventory
            this.setTitle("Edit inventory");
            // Initialize the loader or load the existing loader
            getSupportLoaderManager().initLoader(0, null, this);
        }


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
                if (quantityInventory > 0){
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
    private void saveInventory(){

        // Check if the URI is null. If so, then we will insert a new inventory. Otherwise, it would
        // be just updating the existing inventory in order to prevent insertion of a new inventory
        // after updating the values of the existing inventory
        if(currentInventoryUri == null) {

            // Create a ContentValues object where column names are the keys,
            // and inventory attributes from the editor are the values.
            ContentValues newContentValues = new ContentValues();

            // Read from input fields
            // Use trim to eliminate leading or trailing white space
            String brandString = mBrandEditText.getText().toString().trim();
            String nameString = mNameEditText.getText().toString().trim();

            double priceValue = 0.00;
            String priceString = mPriceEditText.getText().toString().trim();

            // Check if priceString has values, if not set the price value to 0 (by default)
            if(!TextUtils.isEmpty(priceString)){
                priceValue = Double.parseDouble(priceString);
            }

            int quantityValue = 0;
            String quantityString = mQuantityEditText.getText().toString().trim();

            // Check if quantityString has values, if not set the quantity value to 0 (by default)
            if(!TextUtils.isEmpty(quantityString)){
                quantityValue = Integer.parseInt(quantityString);
            }

            String phoneString = mPhoneNumberEditText.getText().toString().trim();
            String emailString = mEmailEditText.getText().toString().trim();

            // Check if the edit text values are all empty
            if(currentInventoryUri == null &&
                    TextUtils.isEmpty(brandString) && TextUtils.isEmpty(nameString) &&
                    TextUtils.isEmpty(priceString) &&
                    TextUtils.isEmpty(phoneString) && TextUtils.isEmpty(emailString)){
                blankEditorValues = true;
                Toast.makeText(this, "There is an error with saving an inventory", Toast.LENGTH_SHORT).show();
                // Return none instead of saving an inventory into the database
                return;
            }

            // Use toast message when there is no string value in edit text, else we put the
            // ContentValues based on columns
            if (TextUtils.isEmpty(brandString)){
                Toast.makeText(this, "Cannot enter empty brand value. " +
                                "Please insert a brand." ,
                        Toast.LENGTH_SHORT).show();
            }

            if (TextUtils.isEmpty(nameString)){
                Toast.makeText(this, "Cannot enter empty name value. " +
                                "Please insert a name." ,
                        Toast.LENGTH_SHORT).show();
            }

            if (TextUtils.isEmpty(phoneString)){
                Toast.makeText(this, "Cannot enter empty phone number. " +
                                "Please insert your supplier phone number." ,
                        Toast.LENGTH_SHORT).show();
            }

            if (TextUtils.isEmpty(emailString)){
                Toast.makeText(this, "Cannot enter empty email. " +
                                "Please insert your supplier email." ,
                        Toast.LENGTH_SHORT).show();
            }

            newContentValues.put(InventoryEntry.COLUMN_INVENTORY_BRAND, brandString);
            newContentValues.put(InventoryEntry.COLUMN_INVENTORY_NAME, nameString);

            // We put the price and quantity value no matter the edit text value is empty or not
            // as this is already being handled (shown above)
            newContentValues.put(InventoryEntry.COLUMN_INVENTORY_PRICE, priceValue);
            newContentValues.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantityValue);

            newContentValues.put(InventoryEntry.COLUMN_INVENTORY_PHONE_NUMBER, phoneString);
            newContentValues.put(InventoryEntry.COLUMN_INVENTORY_EMAIL, emailString);

            if(!TextUtils.isEmpty(brandString) && !TextUtils.isEmpty(nameString) &&
                    !TextUtils.isEmpty(phoneString) && !TextUtils.isEmpty(emailString)){
                fulfilledTexts = true;
            }

            if (fulfilledTexts){
                Uri newUri = getContentResolver().insert(
                        InventoryEntry.CONTENT_URI,     // Content URI from InventoryEntry
                        newContentValues                // The values to insert
                );

                // Check if the Uri resulted from insert method in content resolver is null.
                if(newUri != null){
                    Toast.makeText(this, "Inventory successfully saved" , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "There is an error with saving an inventory" , Toast.LENGTH_SHORT).show();
                }
            }


        } else {

            // Create a ContentValues object where column names are the keys,
            // and inventory attributes from the editor are the values.
            ContentValues existingContentValues = new ContentValues();

            // Read from input fields
            // Use trim to eliminate leading or trailing white space
            String brandString = mBrandEditText.getText().toString().trim();
            String nameString = mNameEditText.getText().toString().trim();

            double priceValue = 0.00;
            String priceString = mPriceEditText.getText().toString().trim();

            // Check if priceString has values, if not set the price value to 0 (by default)
            if(!TextUtils.isEmpty(priceString)){
                priceValue = Double.parseDouble(priceString);
            }

            int quantityValue = 0;
            String quantityString = mQuantityEditText.getText().toString().trim();

            // Check if quantityString has values, if not set the quantity value to 0 (by default)
            if(!TextUtils.isEmpty(quantityString)){
                quantityValue = Integer.parseInt(quantityString);
            }

            String phoneString = mPhoneNumberEditText.getText().toString().trim();
            String emailString = mEmailEditText.getText().toString().trim();

            // Check if the edit text values are all empty
            if(TextUtils.isEmpty(brandString) && TextUtils.isEmpty(nameString) &&
                    TextUtils.isEmpty(priceString) &&
                    TextUtils.isEmpty(phoneString) && TextUtils.isEmpty(emailString)){
                blankEditorValues = true;
                Toast.makeText(this, "There is an error with saving an inventory", Toast.LENGTH_SHORT).show();
                // Return none instead of saving an inventory into the database
                return;
            }

            // Use toast message when there is no string value in edit text, else we put the
            // ContentValues based on columns
            if (TextUtils.isEmpty(brandString)){
                Toast.makeText(this, "Cannot enter empty brand value. " +
                                "Please insert a brand." ,
                        Toast.LENGTH_SHORT).show();
            } else {
                existingContentValues.put(InventoryEntry.COLUMN_INVENTORY_BRAND, brandString);
            }

            if (TextUtils.isEmpty(nameString)){
                Toast.makeText(this, "Cannot enter empty name value. " +
                                "Please insert a name." ,
                        Toast.LENGTH_SHORT).show();
            } else {
                existingContentValues.put(InventoryEntry.COLUMN_INVENTORY_NAME, nameString);
            }

            // We put the price and quantity value no matter the edit text value is empty or not
            // as this is already being handled (shown above)
            existingContentValues.put(InventoryEntry.COLUMN_INVENTORY_PRICE, priceValue);
            existingContentValues.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantityValue);

            if (TextUtils.isEmpty(phoneString)){
                Toast.makeText(this, "Cannot enter empty phone number. " +
                                "Please insert your supplier phone number." ,
                        Toast.LENGTH_SHORT).show();
            } else {
                existingContentValues.put(InventoryEntry.COLUMN_INVENTORY_PHONE_NUMBER, phoneString);
            }

            if (TextUtils.isEmpty(emailString)){
                Toast.makeText(this, "Cannot enter empty email. " +
                                "Please insert your supplier email." ,
                        Toast.LENGTH_SHORT).show();
            } else {
                existingContentValues.put(InventoryEntry.COLUMN_INVENTORY_EMAIL, emailString);
            }

            if(!TextUtils.isEmpty(brandString) && !TextUtils.isEmpty(nameString) &&
                    !TextUtils.isEmpty(phoneString) && !TextUtils.isEmpty(emailString)){
                fulfilledTexts = true;
            }

            if(fulfilledTexts){
                // Otherwise this is an EXISTING inventory, so update the inventory
                // with content URI: currentInventoryUri and pass in the new ContentValues.
                // Pass in null for the selection and selection args
                // because currentInventoryUri will already identify the correct row
                // in the database that we want to modify.
                int rowsUpdated = getContentResolver().update(
                        currentInventoryUri,             // Content URI from InventoryEntry
                        existingContentValues,           // The values to update
                        null,
                        null
                );

                // Check if there are updated rows.
                if(rowsUpdated != 0){
                    Toast.makeText(this, "Inventory successfully updated" , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "There is an error with updating an inventory" , Toast.LENGTH_SHORT).show();
                }
            }

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
                saveInventory();
                if(fulfilledTexts || blankEditorValues) {
                    // Return to MainActivity
                    finish();
                }
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
                currentInventoryUri, // The content URI for the current inventory
                projection, // Columns to include in the resulting Cursor
                null, // No selection
                null, // No selection arguments
                null // Default sort order
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Check if there is no cursor available, if so then return nothing
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()){
            // Find the string columns of inventory attributes that we're interested in
            int brandStringColumn = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_BRAND);
            int nameStringColumn = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
            int priceStringColumn = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
            int quantityStringColumn = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int phoneNumberStringColumn = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PHONE_NUMBER);
            int emailStringColumn = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_EMAIL);

            // Extract out the value from the Cursor for the given column index
            String brand = cursor.getString(brandStringColumn);
            String name = cursor.getString(nameStringColumn);
            double price = cursor.getDouble(priceStringColumn);
            int quantity = cursor.getInt(quantityStringColumn);
            String phoneNumber = cursor.getString(phoneNumberStringColumn);
            String email = cursor.getString(emailStringColumn);

            // Update the views on the screen with the values from the database
            mBrandEditText.setText(brand);
            mNameEditText.setText(name);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantity));
            mPhoneNumberEditText.setText(phoneNumber);
            mEmailEditText.setText(email);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mBrandEditText.setText("");
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText(String.valueOf(1));
        mPhoneNumberEditText.setText("");
        mEmailEditText.setText("");
    }
}
