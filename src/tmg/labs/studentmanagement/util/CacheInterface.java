package tmg.labs.studentmanagement.util;

public interface CacheInterface<Key, Type> {
	public Type get(Key key);

	public boolean cache(Key id, Type data);

	public boolean clear();
}
