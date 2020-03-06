package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.IConfigKeys;
import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.Constants;
import com.jazasoft.tna.dto.ExcelRowError;
import com.jazasoft.tna.entity.TActivity;
import com.jazasoft.tna.entity.TSubActivity;
import com.jazasoft.tna.entity.Timeline;
import com.jazasoft.tna.exception.ExcelUploadNotValidException;
import com.jazasoft.tna.service.TimelineService;
import com.jazasoft.util.StringUtils;
import com.jazasoft.util.Utils;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiUrls.ROOT_URL_TIMELINES)
public class TimelineRestController {

    private final Logger logger = LoggerFactory.getLogger(TimelineRestController.class);

    private final TimelineService timelineService;

    public TimelineRestController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "search", defaultValue = "") String search,
                                     HttpServletRequest request,
                                     Pageable pageable) {
        //Extract buyer privilege from Request
        List<String> buyerIds = new ArrayList<>();
        Object attrBuyer = request.getAttribute(Constants.REQ_ATTRIBUTE_BUYER);
        if (attrBuyer instanceof List) {
            buyerIds = (List<String>) attrBuyer;
        }
        //If buyer privilege is present and there is no filter on buyer, apply filter
        if (!search.contains("buyer") && !buyerIds.isEmpty()) {
            search = search.isEmpty() ? search : search + ";";
            search += "buyer.id=in=(" + Utils.getCsvFromIterable(buyerIds) + ")";
        }

        Page<Timeline> pages;
        if (search.trim().isEmpty()) {
            pages = timelineService.findAll(pageable);
        } else {
            Node rootNode = new RSQLParser().parse(search);
            Specification<Timeline> spec = rootNode.accept(new CustomRsqlVisitor<>());
            pages = timelineService.findAll(spec, pageable);
        }
        // If buyer privilege is present and there was already filter on buyer, Make sure that filter was not for other buyers
        if (!buyerIds.isEmpty() && search.contains("buyer")) {
            List<Timeline> timelines = pages.getContent();
            Set<Long> ids = buyerIds.stream().map(Long::parseLong).collect(Collectors.toSet());
            timelines = timelines.stream().filter(timeline -> timeline.getBuyer() != null && ids.contains(timeline.getBuyer().getId())).collect(Collectors.toList());
            pages = new PageImpl<>(timelines);
        }

        pages.forEach(timeline -> timeline.setTActivityList(null));
        pages.forEach(timeline -> timeline.setBuyerId(timeline.getBuyer() != null ? timeline.getBuyer().getId() : null));
        pages.forEach(timeline -> timeline.setGarmentTypeId(timeline.getGarmentType() != null ? timeline.getGarmentType().getId() : null));
        return ResponseEntity.ok(pages);
    }

    @GetMapping(ApiUrls.URL_TIMELINES_TIMELINE)
    public ResponseEntity<?> findOne(@PathVariable(value = "timelineId") Long id) {
        logger.trace("findOne(): id = {}", id);
        Timeline timeline = timelineService.findOne(id);
        if (timeline == null) {
            return ResponseEntity.notFound().build();
        }
        for (TActivity tActivity : timeline.getTActivityList()) {

            tActivity.setActivityId(tActivity.getActivity() != null ? tActivity.getActivity().getId() : null);
            tActivity.setTimelineId(tActivity.getTimeline() != null ? tActivity.getTimeline().getId() : null);
        //    tActivity.setActivity(tActivity.getActivity());

            for (TSubActivity tSubActivity : tActivity.getTSubActivityList()) {
                tSubActivity.setSubActivityId(tSubActivity.getSubActivity() != null ? tSubActivity.getSubActivity().getId() : null);
                tSubActivity.setTActivityId(tSubActivity.getTActivity() !=null ? tSubActivity.getTActivity().getId():null);
            }
        }
        timeline.setBuyerId(timeline.getBuyer() != null ? timeline.getBuyer().getId() : null);
        timeline.setGarmentTypeId(timeline.getGarmentType() != null ? timeline.getGarmentType().getId() : null);


        timeline.getTActivityList().forEach(tActivity -> tActivity.getActivity().setDepartment(null));
        timeline.getTActivityList().forEach(tActivity -> tActivity.getActivity().setSubActivityList(null));
//      timeline.getTActivityList().forEach(tActivity -> tActivity.setActivityId());


        timeline.getTActivityList().forEach(tActivity -> {
            tActivity.getTSubActivityList().forEach(tSubActivity -> tSubActivity.setTActivity(null));

        });
        return ResponseEntity.ok(timeline);
    }

    @PostMapping
    private ResponseEntity<?> save(@Valid @RequestBody Timeline timeline) {
        validate(timeline);

        timeline = timelineService.save(timeline);
        timeline.getTActivityList().forEach(tActivity -> {
            tActivity.getActivity().setSubActivityList(null);
            tActivity.getTSubActivityList().forEach(tSubActivity -> tSubActivity.setTActivity(null));
        });

        URI Location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(timeline.getId()).toUri();
        return ResponseEntity.created(Location).body(timeline);
    }

    @PutMapping(ApiUrls.URL_TIMELINES_TIMELINE)
    private ResponseEntity<?> update(@PathVariable("timelineId") Long id,
                                     @RequestParam(value = "action", defaultValue = "default") String action,
                                     @RequestBody Timeline timeline, HttpServletRequest request) {
        logger.trace("update(): id = {}", id);
        Pattern pattern = Pattern.compile("default|approve|approval_edit", Pattern.CASE_INSENSITIVE);
        if (!pattern.matcher(action).matches()) {
            return ResponseEntity.badRequest().body("Invalid action. Supported actions are " + pattern.pattern());
        }
        String username = (String)request.getAttribute(IConfigKeys.REQ_ATTRIBUTE_KEY_USER_NAME);
        if (!timelineService.exists(id)) {
            return ResponseEntity.notFound().build();
        }
        validate(timeline);
        timeline.setId(id);
        timeline.setApprovedBy(username);
        Timeline mTimeline = timelineService.update(timeline, action);

        mTimeline.getTActivityList().forEach(tActivity -> {
            if (tActivity.getActivity() != null) {
                tActivity.getActivity().setSubActivityList(null);
                tActivity.getActivity().setDepartment(null);
            }
            tActivity.getTSubActivityList().forEach(tSubActivity -> {
                tSubActivity.setTActivity(null);
                tSubActivity.setSubActivityId(tSubActivity.getSubActivity() != null ? tSubActivity.getSubActivity().getId() : null);
                tSubActivity.setTActivityId(tActivity.getId());
            });
            tActivity.setActivityId(tActivity.getActivity() != null ? tActivity.getId() : null);
            tActivity.setTimelineId(mTimeline.getId());
        });
        return ResponseEntity.ok(mTimeline);
    }

    @DeleteMapping(ApiUrls.URL_TIMELINES_TIMELINE)
    public ResponseEntity<?> deleteTimeline(@PathVariable("timelineId") Long id) {
        if (!timelineService.exists(id)) {
            return ResponseEntity.notFound().build();
        }
        timelineService.deleteTimeline(id);
        return ResponseEntity.noContent().build();
    }

    private void validate(Timeline timeline) {
        List<ExcelRowError> errors = new ArrayList<>();
        for (TActivity tActivity: timeline.getTActivityList()) {
            if (!StringUtils.hasText(tActivity.getTimeFrom())) {
                errors.add(new ExcelRowError("FROM", tActivity.getName(), "From value is required."));
            }
            if (tActivity.getLeadTime() == null) {
                errors.add(new ExcelRowError("LEAD TIME",  tActivity.getName(), "Lead Time value is required."));
            }
        }

        if (!errors.isEmpty()) {
            throw new ExcelUploadNotValidException("Timeline form has errors", errors);
        }
    }
}
