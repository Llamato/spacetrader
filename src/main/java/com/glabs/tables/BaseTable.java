package com.glabs.tables;
import java.util.ArrayList;
import java.util.Arrays;

class BaseTable{
    protected ArrayList<Object> fields;
    protected int columns;
    protected int rows;

    public BaseTable(int columns, Object[] fields){
        this.columns = columns;
        this.rows = fields.length / columns;
        this.fields = new ArrayList<>(Arrays.asList(fields));
    }

    public int getColumns(){
        return columns;
    }

    public int getRows(){
        return rows;
    }

    public String[] getFields(){
        return fields.toArray(new String[0]);
    }

    public void setFields(String[] fields){
        this.fields = new ArrayList<>(Arrays.asList(fields));
        this.rows = fields.length / columns;
    }

    public Object getField(int column, int row){
        return fields.get(row * columns + column);
    }

    public static Object getFieldFromArray(Object[] array, int columnsPerRow, int column, int row){
        return array[row * columnsPerRow + column];
    }
}
