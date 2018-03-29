package com.example.marwa.medicinesinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Marwa on 2/11/2018.
 */

public final class MedicineContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private MedicineContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.marwa.medicinesinventory";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * content://com.example.marwa.medicinesinventory/medicines/ is a valid path for
     * looking at medicine data.
     */
    public static final String PATH_MEDICINES = "medicines";

    /**
     * Inner class that defines constant values for the medicines database table.
     * Each entry in the table represents a single medicine.
     */
    public static abstract class MedicineEntry implements BaseColumns {

        /**
         * The content URI to access the medicine data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MEDICINES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of medicines.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDICINES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single medicine.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEDICINES;


        /**
         * Name of database table for medicines
         */
        public static final String TABLE_NAME = "medicines";

        /**
         * Unique ID number for the medicine (only for use in the database table).
         * Type: INTEGER
         */
        public static final String COLUMN_ID = BaseColumns._ID;

        /**
         * Name of the medicine.
         * Type: TEXT
         */
        public static final String COLUMN_NAME = "name";

        /**
         * Price of the medicine.
         * Type: REAL
         */
        public static final String COLUMN_PRICE = "price";

        /**
         * Image of the medicine.
         * Type: INTEGER
         */
        public static final String COLUMN_IMAGE = "image";

        /**
         * Quantity of the medicine.
         * Type: INTEGER
         */
        public static final String COLUMN_QUANTITY = "quantity";
        /**
         * Supplier of the medicine.
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER = "supplier";
        /**
         * Phone of the supplier.
         * Type: TEXT
         */
        public static final String COLUMN_PHONE = "phone";


    }


}
