package com.example.marwa.medicinesinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.marwa.medicinesinventory.data.MedicineContract.MedicineEntry;

/**
 * Created by Marwa on 2/11/2018.
 */

public class MedicineProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = MedicineProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the medicines table
     */
    private static final int MEDICINES = 100;

    /**
     * URI matcher code for the content URI for a single medicine in the medicines table
     */
    private static final int MEDICINE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The content URI of the form "content://com.example.marwa.medicinesinventory/medicines/" will map to the
        // integer code {@link #MEDICINES}. This URI is used to provide access to MULTIPLE rows
        // of the medicines table.
        sUriMatcher.addURI(MedicineContract.CONTENT_AUTHORITY, MedicineContract.PATH_MEDICINES, MEDICINES);

        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.marwa.medicinesinventory/medicines/3" matches.
        sUriMatcher.addURI(MedicineContract.CONTENT_AUTHORITY, MedicineContract.PATH_MEDICINES + "/#", MEDICINE_ID);
    }

    /**
     * Database helper object
     */
    private MedicineDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new MedicineDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICINES:
                // For the MEDICINES code, query the medicines table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the medicines table.
                cursor = database.query(MedicineEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MEDICINE_ID:
                // For the MEDICINE_ID code, extract out the ID from the URI.
                selection = MedicineEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Returns a Cursor containing that row of the table.
                cursor = database.query(MedicineEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }


    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICINES:
                return insertMedicine(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    /**
     * Insert a medicine into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertMedicine(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(MedicineEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Medicine requires a name");
        }

        // Check that the price is not null and it's greater than or equal to 0
        Double price = values.getAsDouble(MedicineEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Medicine requires valid price");
        }

        /*
        // Check that the image is not null
        byte[] image = values.getAsByteArray(MedicineEntry.COLUMN_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException("Medicine requires an image");
        }
        */

        // If the quantity is provided, check that it's greater than or equal to 0 kg
        Integer quantity = values.getAsInteger(MedicineEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Medicine requires valid quantity");
        }

        // Check that the name is not null
        String supplier = values.getAsString(MedicineEntry.COLUMN_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Medicine requires a supplier");
        }

        // Check that the phone is not null
        String phone = values.getAsString(MedicineEntry.COLUMN_PHONE);
        if (phone == null) {
            throw new IllegalArgumentException("Medicine requires a supplier");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new medicine with the given values
        long id = database.insert(MedicineEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the medicine content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICINES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(MedicineEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MEDICINE_ID:
                // Delete a single row given by the ID in the URI
                selection = MedicineEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(MedicineEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICINES:
                return updateMedicine(uri, contentValues, selection, selectionArgs);
            case MEDICINE_ID:
                // For the MEDICINE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = MedicineEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMedicine(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    /**
     * Update medicines in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more medicines).
     * Return the number of rows that were successfully updated.
     */
    private int updateMedicine(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link MedicineEntry#COLUMN_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(MedicineEntry.COLUMN_NAME)) {
            String name = values.getAsString(MedicineEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Medicine requires a name");
            }
        }

        // If the {@link MedicineEntry#COLUMN_COLUMN_PRICE} key is present,
        // check that the price value is valid.
        if (values.containsKey(MedicineEntry.COLUMN_PRICE)) {
            Double price = values.getAsDouble(MedicineEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Medicine requires a price");
            }
        }

        // If the {@link MedicineEntry#COLUMN_IMAGE} key is present,
        // check that the image value is not null.
        if (values.containsKey(MedicineEntry.COLUMN_IMAGE)) {
            byte[] image = values.getAsByteArray(MedicineEntry.COLUMN_IMAGE);
            if (image == null) {
                throw new IllegalArgumentException("Medicine requires an image");
            }
        }

        // If the {@link MedicineEntry#COLUMN_PRICE} key is present,
        // check that the price value is valid.
        if (values.containsKey(MedicineEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(MedicineEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Medicine requires valid quantity");
            }
        }

        // If the {@link MedicineEntry#COLUMN_SUPPLIER} key is present,
        // check that the supplier value is not null.
        if (values.containsKey(MedicineEntry.COLUMN_SUPPLIER)) {
            String supplier = values.getAsString(MedicineEntry.COLUMN_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Medicine requires a supplier");
            }
        }

        // If the {@link MedicineEntry#COLUMN_SUPPLIER} key is present,
        // check that the phone value is not null.
        if (values.containsKey(MedicineEntry.COLUMN_PHONE)) {
            String phone = values.getAsString(MedicineEntry.COLUMN_SUPPLIER);
            if (phone == null) {
                throw new IllegalArgumentException("Phone Number is required");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(MedicineEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }


    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEDICINES:
                return MedicineEntry.CONTENT_LIST_TYPE;
            case MEDICINE_ID:
                return MedicineEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
