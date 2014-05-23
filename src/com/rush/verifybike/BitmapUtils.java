package com.rush.verifybike;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// 
// Class used for resizing user images
//

public class BitmapUtils {	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
			
			// Added an extra halfing 
			inSampleSize *= 2;
		}

		return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmapFromResource(String imagePath, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(imagePath, options);
	}
	
	public static Bitmap fromByteArray(byte[] bytes) {
		if (bytes == null)
			return null;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inMutable = true;
	
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);		
	}
}
