package com.jazasoft.tna.restcontroller;


import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.entity.DelayReason;
import com.jazasoft.tna.entity.Team;
import com.jazasoft.tna.entity.Timeline;
import com.jazasoft.tna.service.DelayReasonService;
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

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(ApiUrls.ROOT_URL_DELAYREASONS)
public class DelayReasonRestController {

    private final Logger logger = LoggerFactory.getLogger(TeamRestController.class);

    private final DelayReasonService delayReasonService;

    public DelayReasonRestController(DelayReasonService delayReasonService) {
        this.delayReasonService = delayReasonService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "search", defaultValue = "") String search, Pageable pageable) {
        Page<DelayReason> pages;
        if (search.trim().isEmpty()) {
            pages = delayReasonService.findAll(pageable);
        } else {
            Node rootNode = new RSQLParser().parse(search);
            Specification<DelayReason> spec = rootNode.accept(new CustomRsqlVisitor<>());
            pages = delayReasonService.findAll(spec, pageable);
        }
        pages.forEach(delayReason -> delayReason.setActivityId(delayReason.getActivity() != null ? delayReason.getActivity().getId() : null));
        pages.forEach(delayReason -> delayReason.setActivity(null));
        return ResponseEntity.ok(pages);
    }

    @GetMapping(ApiUrls.URL_DELAYREASONS_DELAYREASON)
    public ResponseEntity<?> findOne(@PathVariable("delayReasonId") long id) {
        logger.trace("findOne(): id = {}", id);
        DelayReason delayReason = delayReasonService.findOne(id);
        if (!delayReasonService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        delayReason.setActivityId(delayReason.getActivity() != null ? delayReason.getActivity().getId() : null);
        delayReason.setActivity(null);
        return ResponseEntity.ok(delayReason);
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody DelayReason delayReason) {
        logger.trace("save():\n {}", delayReason.toString());
        delayReason = delayReasonService.save(delayReason);
        delayReason.getActivity().setSubActivityList(null);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(delayReason.getId()).toUri();
        return ResponseEntity.created(location).body(delayReason);
    }

    @PutMapping(ApiUrls.URL_DELAYREASONS_DELAYREASON)
    public ResponseEntity<?> update (@PathVariable (value = "delayReasonId") Long id,@Validated @RequestBody DelayReason delayReason){
        if (!delayReasonService.exists(id)){
            return ResponseEntity.notFound().build();
        }
        delayReason.setId(id);
        delayReason = delayReasonService.update(delayReason);
        delayReason.getActivity().setSubActivityList(null);
        delayReason.getActivity().setDepartment(null);
        return new  ResponseEntity<>(delayReason, HttpStatus.OK);
    }

    @DeleteMapping(ApiUrls.URL_DELAYREASONS_DELAYREASON)
    public ResponseEntity<?> delete(@PathVariable (value = "delayReasonId") Long id){
        if(!delayReasonService.exists(id)){
            return ResponseEntity.notFound().build();
        }
        delayReasonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
