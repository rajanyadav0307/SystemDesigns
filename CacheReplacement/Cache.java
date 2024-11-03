package CacheReplacement;
public interface Cache {
    public void put(Object Key, Object Value);
    public Object get(Object Key);
    public void evict();
}
