package com.jazasoft.tna.restcontroller;


import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.entity.Department;
import com.jazasoft.tna.entity.GarmentType;
import com.jazasoft.tna.service.DepartmentService;
import com.jazasoft.tna.service.GarmentTypeService;
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
@RequestMapping(ApiUrls.ROOT_URL_DEPARTMENTS)
public class DepartmentRestController {
    private final Logger logger = LoggerFactory.getLogger(DepartmentRestController.class);

    private DepartmentService departmentService;

    public DepartmentRestController( DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "search", defaultValue = "") String search, Pageable pageable) {
        logger.trace("findAll()");
        Page<Department> pages;
        if (search.trim().isEmpty()) {
            pages = departmentService.findAll(pageable);
        } else {
            Node rootNode = new RSQLParser().parse(search);
            Specification<Department> spec = rootNode.accept(new CustomRsqlVisitor<>());
            pages = departmentService.findAll(spec, pageable);
        }
        return ResponseEntity.ok(pages);
    }

    @GetMapping(ApiUrls.URL_DEPARTMENTS_DEPARTMENT)
    public ResponseEntity<?> findOne(@PathVariable("departmentId") long id) {
        logger.trace("findOne(): id = {}", id);
        if (!departmentService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(departmentService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody Department department) {
        logger.trace("save():\n {}", department.toString());
        department = departmentService.save(department);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(department.getId()).toUri();
        return ResponseEntity.created(location).body(department);
    }

    @PutMapping(ApiUrls.URL_DEPARTMENTS_DEPARTMENT)
    public ResponseEntity<?> update(@PathVariable("departmentId") Long id,@Validated @RequestBody Department department) {
        logger.trace("update(): id = {} \n {}", id, department);
        if (!departmentService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        department.setId(id);
        department = departmentService.update(department);
        return new ResponseEntity<>(department, HttpStatus.OK);
    }

    @DeleteMapping(ApiUrls.URL_DEPARTMENTS_DEPARTMENT)
    public ResponseEntity<?> delete(@PathVariable("departmentId") long id) {
        logger.trace("delete(): id = {}", id);
        if (!departmentService.exists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        departmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
