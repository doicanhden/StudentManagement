package tmg.labs.studentmanagement.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;

public class Utilities {
  public static File createImageFile()
      throws IOException {
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
        new String[] {MediaColumns.DATA}, null, null, null);
    cursor.moveToFirst();

    return cursor.getString(0);
  }

  public static int calcAge(Date birthday) {
    final Calendar now = Calendar.getInstance();
    final Calendar dob = Calendar.getInstance();
    dob.setTime(birthday);

    int years = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

    if (dob.get(Calendar.MONTH) > now.get(Calendar.MONTH) ||
      (dob.get(Calendar.MONTH) == now.get(Calendar.MONTH) && dob
          .get(Calendar.DATE) > now.get(Calendar.DATE))) {
      --years;
    }

    return years;
  }

}
