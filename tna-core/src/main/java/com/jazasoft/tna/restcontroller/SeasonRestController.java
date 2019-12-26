package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;

import com.jazasoft.tna.entity.Season;
import com.jazasoft.tna.service.SeasonService;
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
@RequestMapping(ApiUrls.ROOT_URL_SEASONS)
public class SeasonRestController {

    private final Logger logger = LoggerFactory.getLogger(SeasonRestController.class);

    private SeasonService seasonService;

    public SeasonRestController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "search", defaultValue = "") String search, Pageable pageable) {
        logger.trace("findAll()");
        Page<Season> pages;
        if (search.trim().isEmpty()) {
            pages = seasonService.findAll(pageable);
        } else {
            Node rootNode = new RSQLParser().parse(search);
            Specification<Season> spec = rootNode.accept(new CustomRsqlVisitor<>());
            pages = seasonService.findAll(spec, pageable);
        }
        return ResponseEntity.ok(pages);
    }

    @GetMapping(ApiUrls.URL_SEASONS_SEASON)
    public ResponseEntity<?> findOne(@PathVariable("seasonId") long id) {
        logger.trace("findOne(): id = {}", id);
        if (!seasonService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(seasonService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody Season season) {
        logger.trace("save():\n {}", season.toString());
        season = seasonService.save(season);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(season.getId()).toUri();
        return ResponseEntity.created(location).body(season);
    }

    @PutMapping(ApiUrls.URL_SEASONS_SEASON)
    public ResponseEntity<?> update(@PathVariable("seasonId") Long id,@Validated @RequestBody Season season) {
        logger.trace("update(): id = {} \n {}", id, season);
        if (!seasonService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        season.setId(id);
        season = seasonService.update(season);
        return new ResponseEntity<>(season, HttpStatus.OK);
    }

    @DeleteMapping(ApiUrls.URL_SEASONS_SEASON)
    public ResponseEntity<?> delete(@PathVariable("seasonId") long id) {
        logger.trace("delete(): id = {}", id);
        if (!seasonService.exists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
         seasonService.delete(id);
         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
