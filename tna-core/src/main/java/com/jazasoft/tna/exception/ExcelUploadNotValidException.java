package com.jazasoft.tna.exception;

import com.jazasoft.tna.dto.ExcelRowError;

import java.util.List;

/**
 * Created by razamd on 4/14/2017.
 */
public class ExcelUploadNotValidException extends RuntimeException {
    private List<ExcelRowError> productErrors;

    public ExcelUploadNotValidException(String message, List<ExcelRowError> productErrors) {
        super(message);
        this.productErrors = productErrors;
    }

    public ExcelUploadNotValidException(String message, Throwable cause, List<ExcelRowError> productErrors) {
        super(message, cause);
        this.productErrors = productErrors;
    }

    public ExcelUploadNotValidException(Throwable cause, List<ExcelRowError> productErrors) {
        super(cause);
        this.productErrors = productErrors;
    }

    public ExcelUploadNotValidException(String message) {
        super(message);
    }

    public ExcelUploadNotValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelUploadNotValidException(Throwable cause) {
        super(cause);
    }

    public List<ExcelRowError> getRollErrors() {
        return productErrors;
    }

    public void setRollErrors(List<ExcelRowError> productErrors) {
        this.productErrors = productErrors;
    }
}
