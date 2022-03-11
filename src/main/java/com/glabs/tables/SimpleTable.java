package com.glabs.tables;
import java.util.Arrays;

public class SimpleTable extends BaseTable{
    private final char columnSeparator = '|';
    private final char rowLiner = '-';
    private final char columnRowJoiner = '+';

    public SimpleTable(int columns, Object[] fields){
        super(columns, fields);
    }

    public void append(Object field){
        fields.add(field);
        this.rows = fields.size() / columns;
    }

    private String getRowLiner(int columnWidth, int rowWidth){
        StringBuilder linerBuilder = new StringBuilder();
        while(linerBuilder.length() < rowWidth){
            linerBuilder.append(linerBuilder.length() % columnWidth == 0 ? columnRowJoiner : rowLiner);
        }
        linerBuilder.append(columnRowJoiner);
        return linerBuilder.toString();
    }

    private String getFieldString(Object content, int width, String filler){
        StringBuilder fieldBuilder = new StringBuilder();
        fieldBuilder.append(content);
        while(fieldBuilder.length() < width){
            fieldBuilder.append(filler);
        }
        if(fieldBuilder.length() > width){
            fieldBuilder.setLength(width);
        }
        return fieldBuilder.toString();
    }

    @Override
    public String toString(){
        int columnWidth = Arrays.stream(fields.toArray()).map(Object::toString).mapToInt(String::length).max().orElse(1) +1;
        int rowWidth = columnWidth * columns;
        StringBuilder tableBuilder = new StringBuilder(getRowLiner(columnWidth, rowWidth));
        tableBuilder.append("\n");
        for(int row = 0; row < rows; row++){
            tableBuilder.append(columnSeparator);
            for(int column = 0; column < columns; column++){
                tableBuilder.append(getFieldString(getField(column, row), columnWidth, " "));
                tableBuilder.setCharAt(tableBuilder.length() -1, columnSeparator);
            }
            tableBuilder.append("\n");
            tableBuilder.append(getRowLiner(columnWidth, rowWidth));
            tableBuilder.append("\n");
        }
        return tableBuilder.toString();
    }
}
