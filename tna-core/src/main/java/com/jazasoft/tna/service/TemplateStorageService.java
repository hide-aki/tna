package com.jazasoft.tna.service;

import com.jazasoft.mtdb.storage.AbstractStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class TemplateStorageService extends AbstractStorageService {
  private final Logger logger = LoggerFactory.getLogger(TemplateStorageService.class);

  public static final String OPERATION_UPLOAD_TEMPLATE = "OPERATION_UPLOAD_TEMPLATE.xlsx";

  public TemplateStorageService() {
    init("templates");
  }

}
