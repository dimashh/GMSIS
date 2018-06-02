package gmsis.database.delete;

import java.util.ArrayList;
import java.util.List;

public class DeleteQuery {
    
    public static DeleteQuery builder() {
        return new DeleteQuery();
    }
    
    private String table = null;
    private String where = null;
    private List<Object> whereValues = null;
    
    private DeleteQuery() {}
    
    public DeleteQuery table(String table) {
        this.table = table;
        return this;
    }
    
    public String table() {
        return table;
    }
    
    public DeleteQuery where(String where) {
        this.where = where;
        return this;
    }
    
    public String where() {
        return where;
    }
    
    public DeleteQuery whereValues(Object... objects) {
        whereValues = new ArrayList<>();
        for(Object o : objects) {
            whereValues.add(o);
        }
        return this;
    }
    
    public List<Object> whereValues() {
        return whereValues;
    }
    
    public String toSQL() {
        String sql = "DELETE FROM " + table;
        if(where != null) {
            sql += " WHERE " + where;
        }
        
        return sql;
    }
    
}