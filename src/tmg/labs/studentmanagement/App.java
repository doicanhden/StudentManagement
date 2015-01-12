package tmg.labs.studentmanagement;

import tmg.labs.studentmanagement.util.ImageLoader;
import tmg.labs.studentmanagement.util.MemoryCache;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class App extends Application {
  private ImageLoader mImageLoader = null;
  private DatabaseHandler mDatabase = null;
  private MemoryCache<String, Bitmap> mMemCache = null;
  private static App sIntance = null;

  public static App getIntance() {
    return sIntance;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    sIntance = this;

    mDatabase = new DatabaseHandler(this);

    mMemCache = new MemoryCache<String, Bitmap>() {

      @Override
      protected long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null)
          return 0;

        return bitmap.getRowBytes() * bitmap.getHeight();
      }
    };

    mImageLoader = new ImageLoader(mMemCache) {

      @Override
      protected Bitmap loadBitmap(String path) {
        try {
          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inSampleSize = 8;

          return BitmapFactory.decodeFile(path, options);
        }
        catch (Throwable ex) {
          ex.printStackTrace();
          if (ex instanceof OutOfMemoryError)
            clearCache();

          return null;
        }
      }
    };
  }

  public DatabaseHandler getDbConnection() {
    return mDatabase;
  }

  public ImageLoader getImageLoader() {
    return mImageLoader;
  }
}