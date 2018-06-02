package gmsis.database;

import gmsis.Log;
import gmsis.database.get.GetResult;
import gmsis.database.get.Query;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class QueryCache {

    private static final int MAX_CACHED_QUERIES = 50;

    private HashMap<String, LRUCacheLHM<Query, GetResult>> queryCache;

    public QueryCache() {
        queryCache = new HashMap<>();
    }


    public boolean has(Query query) {
        return queryCache.containsKey(query.getTable())
                && queryCache.get(query.getTable()).containsKey(query);
    }

    public <T> GetResult<T> get(Query query) {
        Log.trace("Using query cache for query {0}", query);
        return queryCache.containsKey(query.getTable()) ? queryCache.get(query.getTable()).get(query) : null;
    }

    public <T> void put(Query query, GetResult<T> result) {
        Log.trace("Saving query {0} to query cache", query);
        if(!queryCache.containsKey(query.getTable())) queryCache.put(query.getTable(), new LRUCacheLHM<>(MAX_CACHED_QUERIES));
        queryCache.get(query.getTable()).put(query, result);
    }

    public void clear(String table) {
        Log.trace("Clearing query cache for table {0}", table);
        // FIXME, currently clear all cache as stale cache may persist within Lazy objects for another table, e.g. customer lazy
        // object contains stale reference to list of bookings
        queryCache.clear();
        //if(queryCache.containsKey(table)) queryCache.get(table).clear();
    }

    class LRUCacheLHM<K,V> extends LinkedHashMap<K,V> {

        private int capacity;

        public LRUCacheLHM(int capacity) {
            // 1 extra element as add happens before remove, and load factor big
            // enough to avoid triggering resize.  True = keep in access order.
            super(capacity + 1, 1.1f, true);
            this.capacity = capacity;
        }

        @Override
        public boolean removeEldestEntry(Map.Entry<K,V> eldest) {
            return size() > capacity;
        }

    }
}
