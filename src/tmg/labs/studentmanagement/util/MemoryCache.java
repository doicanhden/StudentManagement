package tmg.labs.studentmanagement.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

/**
 * Modified version from:
 * https://github.com/thest1/LazyList/blob/master/src/com/fedorvlasov/lazylist/
 * ImageLoader.java
 * 
 * @author Khanh Tran
 * 
 */
public abstract class MemoryCache<Key, Type> implements
    CacheInterface<Key, Type> {
  private static final String TAG = "MemoryCache";
  private final Map<Key, Type> mMap;

  private long mSize = 0;// current allocated size
  private long mLimit = 1000000;// max memory in bytes

  public MemoryCache() {
    mMap = Collections.synchronizedMap(new LinkedHashMap<Key, Type>(10, 1.5f,
        true)); // Last argument true for LRU ordering

    // use 25% of available heap size
    setLimit(Runtime.getRuntime().maxMemory() / 4);
  }

  public void setLimit(long new_limit) {
    mLimit = new_limit;
    Log.i(TAG, "MemoryCache will use up to " + mLimit / 1024. / 1024. + "MB");
  }

  @Override
  public boolean cache(Key id, Type data) {
    try {
      if (mMap.containsKey(id))
        mSize -= getSizeInBytes(mMap.get(id));

      mMap.put(id, data);
      mSize += getSizeInBytes(data);
      checkSize();

      return true;
    }
    catch (Throwable th) {
      th.printStackTrace();
    }

    return false;
  }

  @Override
  public Type get(Key key) {
    try {
      if (!mMap.containsKey(key))
        return null;
      // NullPointerException sometimes happen here
      // http://code.google.com/p/osmdroid/issues/detail?id=78
      return mMap.get(key);
    }
    catch (NullPointerException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  @Override
  public boolean clear() {
    try {
      // NullPointerException sometimes happen here
      // http://code.google.com/p/osmdroid/issues/detail?id=78
      mMap.clear();
      mSize = 0;

      return true;
    }
    catch (NullPointerException ex) {
      ex.printStackTrace();
    }

    return false;
  }

  protected abstract long getSizeInBytes(Type bitmap);

  private void checkSize() {
    Log.i(TAG, "Cache size=" + mSize + " length=" + mMap.size());

    if (mSize > mLimit) {
      // least recently accessed item will be the first one iterated
      Iterator<Entry<Key, Type>> iter = mMap.entrySet().iterator();

      while (iter.hasNext()) {
        mSize -= getSizeInBytes(iter.next().getValue());
        iter.remove();

        if (mSize <= mLimit)
          break;
      }

      Log.i(TAG, "Clean cache. New size " + mMap.size());
    }
  }
}