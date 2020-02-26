package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.dto.RestError;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.Constants;
import com.jazasoft.tna.entity.view.Task;
import com.jazasoft.tna.service.CalendarService;
import com.jazasoft.tna.util.TnaUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiUrls.ROOT_URL_CALENDAR)
public class CalendarRestController {

  private final CalendarService calendarService;

  public CalendarRestController(CalendarService calendarService) {
    this.calendarService = calendarService;
  }

  @SuppressWarnings("unchecked")
  @GetMapping
  public ResponseEntity<?> findAll(@RequestParam(value = "search", defaultValue = "") String search,
                                   @RequestParam(value = "action", defaultValue = "activity") String action,
                                   HttpServletRequest request) {
    Pattern pattern = Pattern.compile("activity|full", Pattern.CASE_INSENSITIVE);
    if (!pattern.matcher(action).matches()){
      return ResponseEntity.badRequest().body("Invalid action. Supported actions are " + pattern.pattern());
    }
    if (!search.contains("dueDate")) {
      return ResponseEntity.badRequest().body("Due Date filter is required. add dueDate filter in 'search' param.");
    }
    // Extract Buyer Privilege from Request
    Set<Long> buyerIds = new HashSet<>();
    Object attrBuyer = request.getAttribute(Constants.REQ_ATTRIBUTE_BUYER);
    if (attrBuyer instanceof List) {
      buyerIds = ((List<String>) attrBuyer).stream().map(Long::parseLong).collect(Collectors.toSet());
    }
    Long departmentId = -1L;
    Object attrDepartment = request.getAttribute(Constants.REQ_ATTRIBUTE_DEPARTMENT);
    if (attrDepartment instanceof List) {
      List<Long> ids = ((List<String>)attrDepartment).stream().map(Long::parseLong).collect(Collectors.toList());
      if (ids.size() > 1) {
        RestError error = new RestError(409, 409, "User cannot belong to multiple Department");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
      }
      if (!ids.isEmpty()) {
        departmentId = ids.get(0);
      }
    }


    if (search.contains("buyer")) {
      TnaUtils.removeKeyFromSearch(search, "buyer");
    }
    if (search.contains("department")) {
      TnaUtils.removeKeyFromSearch(search, "department");
    }

    List<Task> taskList = calendarService.findAll(action, search, buyerIds, departmentId);

    return ResponseEntity.ok(taskList);
  }
}
