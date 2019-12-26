package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.entity.GarmentType;
import com.jazasoft.tna.entity.Season;
import com.jazasoft.tna.service.GarmentTypeService;
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
@RequestMapping(ApiUrls.ROOT_URL_GARMENT_TYPES)
public class GarmentTypeRestController {

    private final Logger logger = LoggerFactory.getLogger(GarmentTypeRestController.class);

    private GarmentTypeService garmentTypeService;

    public GarmentTypeRestController(GarmentTypeService garmentTypeService) {
        this.garmentTypeService = garmentTypeService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "search", defaultValue = "") String search, Pageable pageable) {
        logger.trace("findAll()");
        Page<GarmentType> pages;
        if (search.trim().isEmpty()) {
            pages = garmentTypeService.findAll(pageable);
        } else {
            Node rootNode = new RSQLParser().parse(search);
            Specification<GarmentType> spec = rootNode.accept(new CustomRsqlVisitor<>());
            pages = garmentTypeService.findAll(spec, pageable);
        }
        return ResponseEntity.ok(pages);
    }

    @GetMapping(ApiUrls.URL_GARMENT_TYPES_GARMENT_TYPE)
    public ResponseEntity<?> findOne(@PathVariable("garmentTypeId") long id) {
        logger.trace("findOne(): id = {}", id);
        if (!garmentTypeService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(garmentTypeService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody GarmentType garmentType) {
        logger.trace("save():\n {}", garmentType.toString());
        garmentType = garmentTypeService.save(garmentType);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(garmentType.getId()).toUri();
        return ResponseEntity.created(location).body(garmentType);
    }

    @PutMapping(ApiUrls.URL_GARMENT_TYPES_GARMENT_TYPE)
    public ResponseEntity<?> update(@PathVariable("garmentTypeId") Long id,@Validated @RequestBody GarmentType garmentType) {
        logger.trace("update(): id = {} \n {}", id, garmentType);
        if (!garmentTypeService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        garmentType.setId(id);
        garmentType = garmentTypeService.update(garmentType);
        return new ResponseEntity<>(garmentType, HttpStatus.OK);
    }

    @DeleteMapping(ApiUrls.URL_GARMENT_TYPES_GARMENT_TYPE)
    public ResponseEntity<?> delete(@PathVariable("garmentTypeId") long id) {
        logger.trace("delete(): id = {}", id);
        if (!garmentTypeService.exists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        garmentTypeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
