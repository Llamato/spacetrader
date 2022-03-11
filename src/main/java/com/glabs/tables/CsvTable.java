package com.glabs.tables;
import java.util.Arrays;

public class CsvTable extends BaseTable{
    protected String delimiter;

    public CsvTable(int columns, Object[] fields, String delimiter){
        super(columns, fields);
        this.delimiter = delimiter;
    }

    public static CsvTable fromString(String csv, String delimiter){
        String[] rows = csv.split("\n");
        String[] fields = Arrays.stream(rows).flatMap(row -> Arrays.stream(row.split(delimiter))).toArray(String[]::new);
        int columns = fields.length / rows.length;
        return new CsvTable(columns, fields, delimiter);
    }

    @Override
    public String toString(){
        StringBuilder csvBuilder = new StringBuilder();
        for(int field = 0; field < fields.size(); field++){
            csvBuilder.append(fields.get(field));
            csvBuilder.append(delimiter);
            if(field % columns == 0){
                csvBuilder.append("\n");
            }
        }
        return csvBuilder.toString();
    }
}
