package com.example.marwa.medicinesinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.marwa.medicinesinventory.data.MedicineContract.MedicineEntry;

/**
 * Created by Marwa on 2/11/2018.
 */

public class MedicineDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "pharmacy.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link MedicineDbHelper}.
     *
     * @param context of the app
     */
    public MedicineDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the medicines table
        String SQL_CREATE_MEDICINES_TABLE = "CREATE TABLE " + MedicineEntry.TABLE_NAME + " ("
                + MedicineEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MedicineEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + MedicineEntry.COLUMN_PRICE + " REAL NOT NULL, "
                + MedicineEntry.COLUMN_IMAGE + " BLOB, "
                + MedicineEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + MedicineEntry.COLUMN_SUPPLIER + " TEXT NOT NULL, "
                + MedicineEntry.COLUMN_PHONE + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_MEDICINES_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the Database
        db.execSQL("DROP TABLE IF EXISTS " + MedicineEntry.TABLE_NAME);
        // Create a new one
        onCreate(db);
    }

}
