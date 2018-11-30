package com.example.android.inventory.data;

import android.provider.BaseColumns;

public class InventoryContract {

    private InventoryContract(){}

    public static final class InventoryEntry implements BaseColumns {

        /** Name of database table for inventories */
        public final static String TABLE_NAME = "inventories";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_INVENTORY_BRAND = "brand";

        public final static String COLUMN_INVENTORY_NAME = "name";

        public final static String COLUMN_INVENTORY_PRICE = "price";

        public final static String COLUMN_INVENTORY_QUANTITY = "quantity";

        public final static String COLUMN_INVENTORY_PHONE_NUMBER = "phone_number";

        public final static String COLUMN_INVENTORY_EMAIL = "email";

    }
}