package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the inventory data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current inventory can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data (based on the URI to query the database).
     *                The cursor is already moved to the correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        ImageView itemImage = (ImageView) view.findViewById(R.id.image_item_display);
        TextView itemBrand = (TextView) view.findViewById(R.id.brandText);
        TextView itemName = (TextView) view.findViewById(R.id.nameText);
        TextView itemPrice = (TextView) view.findViewById(R.id.priceText);
        TextView itemQuantity = (TextView) view.findViewById(R.id.quantityText);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        // Find columns of inventory attributes needed
        int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int brandColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_BRAND);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_IMAGE);

        // Read inventory attributes from the cursor for current inventory
        final int inventoryId = cursor.getInt(idColumnIndex);
        String brand = cursor.getString(brandColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        // Convert the double into string as setText cannot directly set double value
        String priceResult = String.valueOf(price);
        final int quantity = cursor.getInt(quantityColumnIndex);
        // Convert the integer into string as setText cannot accept integer value
        String quantityResult = String.valueOf(quantity);
        byte[] inventoryImage = cursor.getBlob(imageColumnIndex);


        // Populate fields with extracted properties
        itemBrand.setText(brand);
        itemName.setText(name);
        itemPrice.setText(priceResult);
        itemQuantity.setText(quantityResult);
        Bitmap inventoryBitmap = BitmapFactory.decodeByteArray(inventoryImage, 0, inventoryImage.length);
        itemImage.setImageBitmap(inventoryBitmap);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Append the URI with id (represents the id for the database as well as
                // the position of the list view item)
                Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, inventoryId);
                // Call the method that brings context of the Activity, appended id URI as well as
                // quantity of the respective inventory
                sellInventoryItem(context, uri, quantity);
            }
        });
    }

    /**
     * Method for selling an item in inventory.
     *
     * @param context context of the targeted activity
     * @param uri URI with appended ID (to select the item)
     * @param quantity quantity of the inventory (useful for calculations)
     */
    public void sellInventoryItem(Context context, Uri uri, int quantity){

        // Initiate quantity variable value for displaying the result
        int newQuantity = 0;
        // Check if quantity (the parameter) is more than 0 to be able to perform operation
        if (quantity > 0){
            newQuantity = quantity - 1;
        } else {
            // Show Toast message to notify that the inventory has no quantity
            Toast.makeText(context.getApplicationContext(), "The product is out of stock", Toast.LENGTH_SHORT).show();
        }

        // Create a new ContentValues object
        ContentValues values = new ContentValues();
        // Put the resulting quantity value
        values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, newQuantity);

        // Call the SQLiteDatabase update method to update the value {@link ContentValues}
        // based on the URI {@link Uri}, by calling getContentResolver in the context object
        // {@link Context}
        context.getContentResolver().update(uri, values, null, null);
    }

}
