package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    private InventoryContract(){}

    /** Initialize content authority, which represent the package of an app */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

    /** Initialize Uri object by calling the method parse, which takes string and return Uri */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /** Initiate PATH_TableName to be appended to the base content URI */
    public static final String PATH_INVENTORIES = "inventories";

    public static final class InventoryEntry implements BaseColumns {

        /** Initiate complete content uri with withAppendedPath method from Uri, which is to
         * access the data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORIES);

        /** Name of database table for inventories */
        public static final String TABLE_NAME = "inventories";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_INVENTORY_BRAND = "brand";

        public static final String COLUMN_INVENTORY_NAME = "name";

        public static final String COLUMN_INVENTORY_PRICE = "price";

        public static final String COLUMN_INVENTORY_QUANTITY = "quantity";

        public static final String COLUMN_INVENTORY_PHONE_NUMBER = "phone_number";

        public static final String COLUMN_INVENTORY_EMAIL = "email";

        public static final String COLUMN_INVENTORY_IMAGE = "image";

        /** The MIME type of the {@link #CONTENT_URI} for a list of inventories.*/
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_INVENTORIES;

        /** The MIME type of the {@link #CONTENT_URI} for a single inventory.*/
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_INVENTORIES;

    }
}