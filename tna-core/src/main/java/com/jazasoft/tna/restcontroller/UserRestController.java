package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.IConfigKeys;
import com.jazasoft.mtdb.dto.RestError;
import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.entity.User;
import com.jazasoft.tna.service.UserService;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping(ApiUrls.ROOT_URL_USERS)
public class UserRestController {
  private final Logger logger = LoggerFactory.getLogger(UserRestController.class);

  private UserService userService;

  public UserRestController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<?> findAll(@RequestParam(value = "action", defaultValue = "default") String action,
                                   @RequestParam(value = "search", defaultValue = "") String search,
                                   Pageable pageable,
                                   HttpServletRequest request) {
    logger.trace("findAll: action = {}", action);
    Pattern pattern = Pattern.compile("default|sync", Pattern.CASE_INSENSITIVE);
    if (!pattern.matcher(action).matches()) {
      RestError error = new RestError(400, 40001, "Unsupported action. Supported actions are - " + pattern.pattern());
      return ResponseEntity.badRequest().body(error);
    }
    if (action.equalsIgnoreCase("sync")) {
      String tenantId = (String) request.getAttribute(IConfigKeys.REQ_ATTRIBUTE_KEY_TENANT);
      userService.syncUsers(tenantId);
    }
    Page<User> pages;
    if (search.trim().isEmpty()) {
      pages = userService.findAll(pageable);
    } else {
      Node rootNode = new RSQLParser().parse(search);
      Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<>());
      pages = userService.findAll(spec, pageable);
    }
    return ResponseEntity.ok(pages);
  }

  @GetMapping(ApiUrls.URL_USERS_USER)
  public ResponseEntity<?> findOne(@PathVariable("userId") long id) {
    logger.trace("findOne(): id = {}", id);
    if (!userService.exists(id)) {
      return ResponseEntity.notFound().build();
    }
    User user = userService.findOne(id);
    return ResponseEntity.ok(user);
  }

  @PostMapping
  public ResponseEntity<?> save(@Valid @RequestBody User user) {
    logger.trace("save");
    user = userService.save(user);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
    return ResponseEntity.created(location).body(user);
  }

  @PostMapping(ApiUrls.ROOT_URL_USERS_BATCH_SAVE)
  @SuppressWarnings("unchecked")
  public ResponseEntity<?> batchSave(@RequestBody List<User> userList) {
    if (userList.isEmpty()) {
      return ResponseEntity.badRequest().body("User List cannot be empty");
    }
    userList = userService.saveBatch(userList);
    return ResponseEntity.ok(userList);
  }

  @PutMapping(ApiUrls.URL_USERS_USER)
  public ResponseEntity<?> update(@PathVariable("userId") long id, @Validated @RequestBody User user) {
    logger.trace("update: id = {} \n {}", id, user);
    if (!userService.exists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    user.setId(id);
    user = userService.update(user);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }
//
//  @PutMapping(ApiUrls.URL_USERS_REGISTER_PUSH_TOKEN)
//  public ResponseEntity<?> updateUser(@RequestParam("userId") long userId,
//                                      @RequestParam("pushToken") String pushToken) {
//    userService.registerPushToken(userId, pushToken);
//    return ResponseEntity.ok("OK");
//  }

  @DeleteMapping(ApiUrls.URL_USERS_USER)
  public ResponseEntity<?> delete(@PathVariable("userId") long id) {
    logger.trace("delete: id = {}", id);
    if (!userService.exists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    userService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
