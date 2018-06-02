package gmsis.database.put;

import java.util.Collection;
import java.util.Map;

public class InsertQuery {
    
    public static InsertQuery builder() {
        return new InsertQuery();
    }
    
    private String table = null;
    
    private InsertQuery() {}
    
    public InsertQuery table(String table) {
        this.table = table;
        return this;
    }
    
    public String table() {
        return table;
    }
    
    public String toSQL(Map<String, Object> values) {
        String sql = "INSERT INTO " + table + "(";
        String delim = "";
        for(Object column : values.keySet()) {
            sql += delim + column.toString();
            delim = ",";
        }
        sql += ") VALUES(";
        Collection<Object> vals = values.values();
        delim = "";
        for(int i = 0; i < vals.size(); i++) {
            sql += delim + "?";
            delim = ",";
        }
        sql += ")";
        
        return sql;
    }
    
}