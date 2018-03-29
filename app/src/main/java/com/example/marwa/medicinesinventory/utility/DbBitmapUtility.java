package com.example.marwa.medicinesinventory.utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;

/**
 * Created by Marwa on 2/13/2018.
 */

public class DbBitmapUtility {

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    // convert from drawable To bitmap
    public static byte[] getImage(Context context, int image) {
        Resources res = context.getResources();
        Drawable drawable = res.getDrawable(image);
        // convert Drawable to bitmap
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        // convert from bitmap to byte array
        return DbBitmapUtility.getBytes(bitmap);
    }

}
