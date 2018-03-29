package com.example.marwa.medicinesinventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.marwa.medicinesinventory.data.MedicineContract.MedicineEntry;
import com.example.marwa.medicinesinventory.utility.DbBitmapUtility;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the medicine data loader
     */
    private static final int MEDICINE_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    MedicineCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the medicine data
        ListView medicineListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        medicineListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of medicine data in the Cursor.
        // There is no medicine data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new MedicineCursorAdapter(this, null);
        medicineListView.setAdapter(mCursorAdapter);

        // Kick off the loader
        getSupportLoaderManager().initLoader(MEDICINE_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded medicine data into the database. For debugging purposes only.
     */
    private void insertDummyMedicine() {

        // Dummy Image
        byte[] bitMapData = DbBitmapUtility.getImage(this, R.drawable.polyfresh);

        // Create a ContentValues object where column names are the keys,
        // and Toto's medicine attributes are the values.
        ContentValues values = new ContentValues();
        values.put(MedicineEntry.COLUMN_NAME, "Polyfresh");
        values.put(MedicineEntry.COLUMN_PRICE, 20);
        values.put(MedicineEntry.COLUMN_IMAGE, bitMapData);
        values.put(MedicineEntry.COLUMN_QUANTITY, 5);
        values.put(MedicineEntry.COLUMN_SUPPLIER, "Karma");
        values.put(MedicineEntry.COLUMN_PHONE, "01004567893");

        // Insert Dummy Data
        getContentResolver().insert(MedicineEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all medicines in the database.
     */
    private void deleteAllMedicines() {
        int rowsDeleted = getContentResolver().delete(MedicineEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from medicines database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.insert_dummy_data:
                // insert new medicine
                insertDummyMedicine();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.delete_all_entries:
                deleteAllMedicines();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                MedicineEntry._ID,
                MedicineEntry.COLUMN_NAME,
                MedicineEntry.COLUMN_PRICE,
                MedicineEntry.COLUMN_IMAGE,
                MedicineEntry.COLUMN_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                MedicineEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null); // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link MedicineCursorAdapter} with this new cursor containing updated medicine data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
