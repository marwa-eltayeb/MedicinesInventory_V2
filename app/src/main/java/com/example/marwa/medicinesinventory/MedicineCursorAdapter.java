package com.example.marwa.medicinesinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marwa.medicinesinventory.data.MedicineContract.MedicineEntry;
import com.example.marwa.medicinesinventory.utility.DbBitmapUtility;


/**
 * Created by Marwa on 2/12/2018.
 */

public class MedicineCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link MedicineCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public MedicineCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
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
     * This method binds the medicine data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current medicine can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView price = (TextView) view.findViewById(R.id.price);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        RelativeLayout parentView = (RelativeLayout) view.findViewById(R.id.parent_layout);

        // Find the columns of medicine attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(MedicineEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(MedicineEntry.COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(MedicineEntry.COLUMN_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(MedicineEntry.COLUMN_IMAGE);
        int quantityColumnIndex = cursor.getColumnIndex(MedicineEntry.COLUMN_QUANTITY);

        // Read the medicine attributes from the Cursor for the current medicine
        final int rowId = cursor.getInt(idColumnIndex);
        String medicineName = cursor.getString(nameColumnIndex);
        double medicinePrice = cursor.getDouble(priceColumnIndex);
        byte[] arrayImage = cursor.getBlob(imageColumnIndex);
        Bitmap medicineImage = DbBitmapUtility.getImage(arrayImage);
        final int medicineQuantity = cursor.getInt(quantityColumnIndex);

        if (medicineQuantity <= 1) {
            quantityTextView.setText(medicineQuantity + " " + context.getResources().getString(R.string.unit));
        } else {
            quantityTextView.setText(medicineQuantity + " " + context.getResources().getString(R.string.units));
        }

        // Update the TextViews with the attributes for the current medicine
        name.setText(medicineName);
        price.setText(String.valueOf(medicinePrice));
        image.setImageBitmap(medicineImage);

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Detail activity
                Intent intent = new Intent(context, DetailActivity.class);

                // Form the content URI that represents clicked medicine.
                Uri currentInventoryUri = ContentUris.withAppendedId(MedicineEntry.CONTENT_URI, rowId);

                // Set the URI on the data field of the intent
                intent.setData(currentInventoryUri);

                context.startActivity(intent);
            }
        });


        Button sellButton = (Button) view.findViewById(R.id.sell_Button);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get the Text
                String text = quantityTextView.getText().toString();
                // Split it
                String[] splittedText = text.split(" ");
                // Take the first part
                int quantity = Integer.parseInt(splittedText[0]);

                if (quantity == 0) {
                    Toast.makeText(context, R.string.no_more_stock, Toast.LENGTH_SHORT).show();
                } else if (quantity > 0) {
                    quantity = quantity - 1;

                    String quantityString = Integer.toString(quantity);

                    ContentValues values = new ContentValues();
                    values.put(MedicineEntry.COLUMN_QUANTITY, quantityString);

                    Uri currentInventoryUri = ContentUris.withAppendedId(MedicineEntry.CONTENT_URI, rowId);

                    int rowsAffected = context.getContentResolver().update(currentInventoryUri, values, null, null);

                    if (rowsAffected != 0) {
                        /* update text view if database update is successful */
                        if (medicineQuantity <= 1) {
                            quantityTextView.setText(quantity + " " + context.getResources().getString(R.string.unit));
                        } else {
                            quantityTextView.setText(quantity + " " + context.getResources().getString(R.string.units));
                        }
                    } else {
                        Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


}
