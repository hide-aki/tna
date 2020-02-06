package com.jazasoft.tna;

import com.jazasoft.mtdb.AbstractExceptionHandler;
import com.jazasoft.tna.exception.ExcelUploadNotValidException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

@ControllerAdvice
public class GenericExceptionHandler extends AbstractExceptionHandler {

  @Override
  public ResponseEntity<?> handleConflict(DataIntegrityViolationException e) {
    e.printStackTrace();
    String cause = e.getRootCause().getMessage();
    if (cause.contains("duplicate")) {
      ConstraintViolationException cve = null;
      Exception ex = e;
      while (ex.getCause() != null) {
        if (ex.getCause() instanceof ConstraintViolationException) {
          cve = (ConstraintViolationException) ex.getCause();
          logger.error("constraint violated = {}", cve.getConstraintName());
        }
        ex = (Exception) ex.getCause();
      }
      if (cve != null) {
        String message = messageSource.getMessage(cve.getConstraintName(), null, e.getRootCause().getMessage(), LocaleContextHolder.getLocale());
        if (message != null && message.trim().length() != 0) {
          return response(HttpStatus.CONFLICT, 40901, message, e.getRootCause().getMessage(), "");
        }
      }
    }
    return super.handleConflict(e);
  }

  @ExceptionHandler(ExcelUploadNotValidException.class)
  public ResponseEntity<?> processRollError(ExcelUploadNotValidException e){
    logger.debug("processRollError: {}", e.getMessage());
    return new ResponseEntity<>(e.getRollErrors(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler
  ResponseEntity<?> handleRestTemplateHttpStatusCodeException(HttpStatusCodeException e) {
    e.printStackTrace();
    return response(e.getStatusCode(), e.getStatusCode().value(), e.getResponseBodyAsString());
  }

  @Override
  public ResponseEntity<?> handleException(Exception e) {
    e.printStackTrace();
    return super.handleException(e);
  }
}
