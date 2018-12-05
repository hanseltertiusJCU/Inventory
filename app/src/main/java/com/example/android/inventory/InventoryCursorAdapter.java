package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

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
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView itemBrand = (TextView) view.findViewById(R.id.brandText);
        TextView itemName = (TextView) view.findViewById(R.id.nameText);
        TextView itemPrice = (TextView) view.findViewById(R.id.priceText);
        TextView itemQuantity = (TextView) view.findViewById(R.id.quantityText);

        // Extract properties from cursor
        String brand = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_BRAND));
        String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME));
        double price = cursor.getDouble(cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE));
        // Convert the double into string as setText cannot directly set double value
        String priceResult = String.valueOf(price);
        int quantity = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY));
        // Convert the integer into string as setText cannot accept integer value
        String quantityResult = String.valueOf(quantity);

        // Populate fields with extracted properties
        itemBrand.setText(brand);
        itemName.setText(name);
        itemPrice.setText(priceResult);
        itemQuantity.setText(quantityResult);
    }
}
