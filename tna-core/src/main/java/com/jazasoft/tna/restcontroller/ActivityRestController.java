package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.entity.Activity;
import com.jazasoft.tna.entity.SubActivity;
import com.jazasoft.tna.service.ActivityService;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(ApiUrls.ROOT_URL_ACTIVITIES)
public class ActivityRestController {
    private final Logger logger = LoggerFactory.getLogger(ActivityRestController.class);

    private final ActivityService activityService;

    public ActivityRestController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "search", defaultValue = "") String search, Pageable pageable) {
        Page<Activity> pages;
        if (search.trim().isEmpty()) {
            pages = activityService.findAll(pageable);
        } else {
            Node rootNode = new RSQLParser().parse(search);
            Specification<Activity> spec = rootNode.accept(new CustomRsqlVisitor<>());
            pages = activityService.findAll(spec, pageable);
        }
        pages.forEach(activity -> activity.setSubActivityList(null));
        pages.forEach(activity -> activity.setDepartmentId(activity.getDepartment() != null ? activity.getDepartment().getId() : null));
        return ResponseEntity.ok(pages);
    }

    @GetMapping(ApiUrls.URL_ACTIVITIES_ACTIVITY)
    public ResponseEntity<?> findOne(@PathVariable(value = "activityId") Long id) {
        logger.trace("findOneActivity(): id = {}", id);
        Activity activity = activityService.findOne(id);
        if (activity == null) {
            return ResponseEntity.notFound().build();
        }
        activity.setDepartmentId(activity.getDepartment() != null ? activity.getDepartment().getId() : null);
        return ResponseEntity.ok(activity);
    }

    @PostMapping
    private ResponseEntity<?> saveActivity(@Valid @RequestBody Activity activity) {
        Set<Long> list = new HashSet<>();
        for (SubActivity subActivity : activity.getSubActivityList()) {
            if (!list.add(subActivity.getSubActivityId())) {
                return new ResponseEntity<>("Duplicate SubActivities. Each subActivityId must be unique", HttpStatus.CONFLICT);
            }
        }
//        activity.setId(null);
        activity = activityService.saveActivity(activity);
        URI Location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(activity.getId()).toUri();
        return ResponseEntity.created(Location).body(activity);
    }

    @DeleteMapping(ApiUrls.URL_ACTIVITIES_ACTIVITY)
    public ResponseEntity<?> deleteActivity(@PathVariable(value = "activityId") Long id) {
        if (!activityService.exists(id)) {
            return ResponseEntity.notFound().build();
        }
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

}
