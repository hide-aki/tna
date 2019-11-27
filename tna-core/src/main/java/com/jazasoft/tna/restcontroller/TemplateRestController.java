package com.jazasoft.tna.restcontroller;

import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.service.TemplateStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiUrls.ROOT_URL_TEMPLATES)
public class TemplateRestController {

  @Autowired
  TemplateStorageService storageService;

  @GetMapping
  public ResponseEntity<?> getTemplate(@RequestParam("name") String name) {
    String templateFile = name + ".xlsx";

    Resource resource = storageService.loadAsResource(templateFile);

    return ResponseEntity
        .ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
  }

}
