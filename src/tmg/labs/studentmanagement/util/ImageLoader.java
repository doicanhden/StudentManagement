package tmg.labs.studentmanagement.util;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

/**
 * Modified version from:
 * https://github.com/thest1/LazyList/blob/master/src/com/fedorvlasov/lazylist/
 * ImageLoader.java
 * 
 * @author Khanh Tran
 * 
 */
public abstract class ImageLoader {
  private final CacheInterface<String, Bitmap> mCache;
  private final Map<ImageView, String> mImageViews = Collections
      .synchronizedMap(new WeakHashMap<ImageView, String>());

  private final ExecutorService mExecutor = Executors.newFixedThreadPool(5);
  // handler to display images in UI thread
  private final Handler mHandler = new Handler();

  public ImageLoader(
      CacheInterface<String, Bitmap> cache) {
    this.mCache = cache;
  }

  public void displayImage(ImageView view, String path, int resId) {
    mImageViews.put(view, path);

    Bitmap bitmap = mCache.get(path);

    if (bitmap != null) {
      view.setImageBitmap(bitmap);
    }
    else {
      queuePhoto(view, path, resId);
      view.setImageResource(resId);
    }
  }

  private void queuePhoto(ImageView view, String path, int resId) {
    mExecutor.submit(new PhotosLoader(Triple.create(view, path, resId)));
  }

  protected abstract Bitmap loadBitmap(String path);

  public void clearCache() {
    mCache.clear();
  }

  private boolean imageViewReused(Triple<ImageView, String, Integer> holder) {
    final String path = mImageViews.get(holder.first);
    if (path == null || !path.equals(holder.second)) {
      return true;
    }
    return false;
  }

  private class PhotosLoader implements Runnable {
    private final Triple<ImageView, String, Integer> holder;

    PhotosLoader(
        Triple<ImageView, String, Integer> holder) {
      this.holder = holder;
    }

    @Override
    public void run() {
      try {
        if (imageViewReused(holder))
          return;

        Bitmap bm = loadBitmap(holder.second);
        mCache.cache(holder.second, bm);

        if (imageViewReused(holder))
          return;

        mHandler.post(new BitmapDisplayer(holder, bm));
      }
      catch (Throwable th) {
        th.printStackTrace();
      }
    }
  }

  // Used to display bitmap in the UI thread
  private class BitmapDisplayer implements Runnable {
    private final Triple<ImageView, String, Integer> holder;
    private final Bitmap bitmap;

    public BitmapDisplayer(
        Triple<ImageView, String, Integer> h, Bitmap b) {
      this.holder = h;
      this.bitmap = b;
    }

    public void run() {
      if (imageViewReused(holder))
        return;

      if (bitmap != null)
        holder.first.setImageBitmap(bitmap);
      else
        holder.first.setImageResource(holder.thirst);
    }
  }
}