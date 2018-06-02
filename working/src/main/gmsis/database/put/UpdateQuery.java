package gmsis.database.put;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UpdateQuery {
    
    public static UpdateQuery builder() {
        return new UpdateQuery();
    }
    
    private String table = null;
    private String where = null;
    private List<Object> whereValues = null;
    
    private UpdateQuery() {}
    
    public UpdateQuery table(String table) {
        this.table = table;
        return this;
    }
    
    public String table() {
        return table;
    }
    
    public UpdateQuery where(String where) {
        this.where = where;
        return this;
    }
    
    public String where() {
        return where;
    }
    
    public UpdateQuery whereValues(Object... objects) {
        whereValues = new ArrayList<>();
        for(Object o : objects) {
            whereValues.add(o);
        }
        return this;
    }
    
    public List<Object> whereValues() {
        return whereValues;
    }
    
    public String toSQL(Map<String, Object> values) {
        String sql = "UPDATE " + table + " SET ";
        String delim = "";
        for(Object column : values.keySet()) {
            sql += delim + column.toString() + "=?";
            delim = ",";
        }
        if(where != null) {
            sql += " WHERE " + where;
        }
        
        return sql;
    }
    
}