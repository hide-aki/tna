package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.entity.Timeline;
import com.jazasoft.tna.service.TimelineService;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(ApiUrls.ROOT_URL_TIMELINES)
public class TimelineRestController {

    private final Logger logger = LoggerFactory.getLogger(TimelineRestController.class);

    private final TimelineService timelineService;

    public TimelineRestController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "search", defaultValue = "")String search, Pageable pageable){
        Page<Timeline> pages;
        if (search.trim().isEmpty()) {
            pages = timelineService.findAll(pageable);
        } else {
            Node rootNode = new RSQLParser().parse(search);
            Specification<Timeline> spec = rootNode.accept(new CustomRsqlVisitor<>());
            pages = timelineService.findAll(spec, pageable);
        }
        pages.forEach(timeline -> timeline.setTActivityList(null));
        pages.forEach(timeline -> timeline.setBuyerId(timeline.getBuyer() != null ? timeline.getBuyer().getId() : null));
        return ResponseEntity.ok(pages);
    }

    @GetMapping(ApiUrls.URL_TIMELINES_TIMELINE)
    public ResponseEntity<?> findOne(@PathVariable(value = "timelineId") Long id) {
        logger.trace("findOne(): id = {}", id);
        Timeline timeline = timelineService.findOne(id);
        if (timeline == null) {
            return ResponseEntity.notFound().build();
        }
        timeline.setBuyerId(timeline.getBuyer() != null ? timeline.getBuyer().getId() : null);
        return ResponseEntity.ok(timeline);
    }

    @PostMapping
    private ResponseEntity<?> save(@Valid @RequestBody Timeline timeline) {
//        Set<Long> list = new HashSet<>();
//        for (TActivity tActivity : timeline.getTActivityList()) {
//            if (!list.add(tActivity.getTActivityId())) {
//                return new ResponseEntity<>("Duplicate SubActivities. Each subActivityId must be unique", HttpStatus.CONFLICT);
//            }
//        }
//        activity.setId(null);
        timeline = timelineService.save(timeline);
        timeline.getTActivityList().forEach(tActivity -> {
            tActivity.getActivity().setSubActivityList(null);
            tActivity.getTSubActivityList().forEach(tSubActivity -> tSubActivity.setTActivity(null));
        });

        URI Location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(timeline.getId()).toUri();
        return ResponseEntity.created(Location).body(timeline);
    }
}
