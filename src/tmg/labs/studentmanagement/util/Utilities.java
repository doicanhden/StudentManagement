package tmg.labs.studentmanagement.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;

public class Utilities {
	public static File createImageFile() throws IOException {
		// Create an image file name
		String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());

		File dir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		if (!dir.exists()) {
			dir.mkdir();
		}

		return File.createTempFile("JPEG_" + time, ".jpg", dir);
	}

	public static String getPath(Context context, Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { MediaColumns.DATA }, null, null, null);
		cursor.moveToFirst();

		return cursor.getString(0);
	}
}
