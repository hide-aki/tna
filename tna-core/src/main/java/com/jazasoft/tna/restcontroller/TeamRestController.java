package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.entity.Department;
import com.jazasoft.tna.entity.Team;
import com.jazasoft.tna.service.DepartmentService;
import com.jazasoft.tna.service.TeamService;
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
@RequestMapping(ApiUrls.ROOT_URL_TEAMS)
public class TeamRestController {
    private final Logger logger = LoggerFactory.getLogger(TeamRestController.class);

    private TeamService teamService;

    public TeamRestController( TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "search", defaultValue = "") String search, Pageable pageable) {
        logger.trace("findAll()");
        Page<Team> pages;
        if (search.trim().isEmpty()) {
            pages = teamService.findAll(pageable);
        } else {
            Node rootNode = new RSQLParser().parse(search);
            Specification<Team> spec = rootNode.accept(new CustomRsqlVisitor<>());
            pages = teamService.findAll(spec, pageable);
        }
        return ResponseEntity.ok(pages);
    }

    @GetMapping(ApiUrls.URL_TEAMS_TEAM)
    public ResponseEntity<?> findOne(@PathVariable("teamId") long id) {
        logger.trace("findOne(): id = {}", id);
        if (!teamService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(teamService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody Team team) {
        logger.trace("save():\n {}", team.toString());
        team = teamService.save(team);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(team.getId()).toUri();
        return ResponseEntity.created(location).body(team);
    }

    @PutMapping(ApiUrls.URL_TEAMS_TEAM)
    public ResponseEntity<?> update(@PathVariable("teamId") Long id,@Validated @RequestBody Team team) {
        logger.trace("update(): id = {} \n {}", id, team);
        if (!teamService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        team.setId(id);
        team = teamService.update(team);
        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @DeleteMapping(ApiUrls.URL_TEAMS_TEAM)
    public ResponseEntity<?> delete(@PathVariable("teamId") long id) {
        logger.trace("delete(): id = {}", id);
        if (!teamService.exists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        teamService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
