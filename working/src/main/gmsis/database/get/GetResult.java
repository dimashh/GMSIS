package gmsis.database.get;

import java.util.List;

public class GetResult<T> {
    
    private List<T> results;
    
    public GetResult(List<T> results) {
        this.results = results;
    }
    
    /**
     * Returns a list of items
     * @return 
     */
    public List<T> getList() {
        return results;
    }
    
    /**
     * Returns a single item
     * @return 
     */
    public T getSingle() {
        return results.size() > 0 ? results.get(0) : null;
    }
    
}
