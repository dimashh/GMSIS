package gmsis.database.get;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Query builds get queries for the database, it supports WHERE, LIMIT, ORDER BY and OFFSET sql
 */
public class Query {
    
    public static Query builder() {
        return new Query();
    }
    
    private String table = null;
    private String where = null;
    private String orderBy = null;
    private Integer limit = null;
    private Integer offset = null;
    private List<Object> whereValues = null;
    
    private Query() {}
    
    public Query table(String table) {
        this.table = table;
        return this;
    }
    
    public String table() {
        return table;
    }
    
    public Query where(String where) {
        this.where = where;
        return this;
    }
    
    public String where() {
        return where;
    }
    
    public Query whereValues(List<Object> objects) {
        whereValues = objects;
        return this;
    }
    
    public Query whereValues(Object... objects) {
        whereValues = new ArrayList<>();
        for(Object o : objects) {
            whereValues.add(o);
        }
        return this;
    }
    
    public List<Object> whereValues() {
        return whereValues;
    }
    
    
    public Query limit(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
        return this;
    }
    
    public Query limit(int limit) {
        this.limit = limit;
        return this;
    }
    
    public Query orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public String getTable() {
        return table;
    }

    @Override
    public String toString() {
        return "Query{" +
                "table='" + table + '\'' +
                ", where='" + where + '\'' +
                ", orderBy='" + orderBy + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                ", whereValues=" + whereValues +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query query = (Query) o;
        return Objects.equals(table, query.table) &&
                Objects.equals(where, query.where) &&
                Objects.equals(orderBy, query.orderBy) &&
                Objects.equals(limit, query.limit) &&
                Objects.equals(offset, query.offset) &&
                Objects.equals(whereValues, query.whereValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, where, orderBy, limit, offset, whereValues);
    }

    public String toSQL() {
        String sql = "SELECT * FROM " + table;
        if(where != null) {
            sql += " WHERE " + where;
        }
        if(orderBy != null) {
            sql += " ORDER BY " + orderBy;
        }
        if(limit != null) {
            sql += " LIMIT " + limit;
            if(offset != null) {
                sql += " OFFSET " + offset;
            }
        }
        
        return sql;
    }
    
}
