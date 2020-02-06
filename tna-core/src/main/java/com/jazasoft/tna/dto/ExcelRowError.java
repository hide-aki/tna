package com.jazasoft.tna.dto;

/**
 * Created by razamd on 4/14/2017.
 */
public class ExcelRowError {
    private String column;
    private int row;
    private String message;

    public ExcelRowError() {
    }

    public ExcelRowError(String column, int row, String message) {
        this.column = column;
        this.row = row;
        this.message = message;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
