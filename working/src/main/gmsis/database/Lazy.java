package gmsis.database;

/**
 * Lazy loading holder, defers loading of a variable
 * @param <T>
 */
public class Lazy<T> {
// FIXME: Causes issues with cache invalidation

    private T object;
    private boolean hasLoaded = false;
    private LazyCallback<T> callback;

    public Lazy(LazyCallback<T> callback) {
        this.callback = callback;
    }

    public T get() {
        if(!hasLoaded) {
            object = callback.load();
            hasLoaded = true;
        }
        return object;
    }

    public void set(T newObject) {
        object = newObject;
        hasLoaded = true;
    }

    public interface LazyCallback<T> {
        T load();
    }

}
