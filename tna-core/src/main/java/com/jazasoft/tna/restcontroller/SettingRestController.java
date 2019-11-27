package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.IConfigKeys;
import com.jazasoft.mtdb.dto.Setting;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.service.ConfigServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mdzahidraza on 15/12/17.
 */
@RestController
@RequestMapping(ApiUrls.ROOT_URL_SETTINGS)
public class SettingRestController {
  private final Logger logger = LoggerFactory.getLogger(SettingRestController.class);

  @Autowired
  private ConfigServiceImpl configService;

  @GetMapping
  public ResponseEntity<?> findAllSettings(HttpServletRequest req){
    logger.trace("findAllSettings");
    String tenant = (String) req.getAttribute(IConfigKeys.REQ_ATTRIBUTE_KEY_TENANT);
    Map<String, Object> result = new HashMap<>();
    result.put("settings", configService.readAllSettings(tenant));
    result.put("groups", configService.readGroups());
    return ResponseEntity.ok(result);
  }

  @GetMapping(ApiUrls.URL_SETTINGS_SETTING)
  public ResponseEntity<?> findOneSetting(@PathVariable("key") String key, HttpServletRequest req){
    logger.trace("getSetting: key = {}", key);
    String tenant = (String) req.getAttribute(IConfigKeys.REQ_ATTRIBUTE_KEY_TENANT);
    return ResponseEntity.ok(configService.readSetting(tenant, key));
  }


  @PutMapping(ApiUrls.URL_SETTINGS_SETTING)
  public ResponseEntity<?> updateSetting(@PathVariable("key") String key, @RequestBody Setting setting, HttpServletRequest req){
    logger.debug("updateSetting: key = {}", key);
    String tenant = (String) req.getAttribute(IConfigKeys.REQ_ATTRIBUTE_KEY_TENANT);
    boolean result = configService.updateSetting(tenant, key, setting);
    if (result) {
      return ResponseEntity.ok(setting);
    }
    return new ResponseEntity<>(HttpStatus.CONFLICT);
  }
}
