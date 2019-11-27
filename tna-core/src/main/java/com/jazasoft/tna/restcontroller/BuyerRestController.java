package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.entity.Buyer;
import com.jazasoft.tna.service.BuyerService;
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
@RequestMapping(ApiUrls.ROOT_URL_BUYERS)
public class BuyerRestController {
    private final Logger logger = LoggerFactory.getLogger(BuyerRestController.class);

    private BuyerService buyerService;

    public BuyerRestController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "search", defaultValue = "") String search, Pageable pageable) {
        logger.trace("findAll()");
        Page<Buyer> pages;
        if (search.trim().isEmpty()) {
            pages = buyerService.findAll(pageable);
        } else {
            Node rootNode = new RSQLParser().parse(search);
            Specification<Buyer> spec = rootNode.accept(new CustomRsqlVisitor<>());
            pages = buyerService.findAll(spec, pageable);
        }
        return ResponseEntity.ok(pages);
    }

    @GetMapping(ApiUrls.URL_BUYERS_BUYER)
    public ResponseEntity<?> findOne(@PathVariable("buyerId") long id) {
        logger.trace("findOne(): id = {}", id);
        if (!buyerService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(buyerService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody Buyer buyer) {
        logger.trace("save():\n {}", buyer.toString());
        buyer = buyerService.save(buyer);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(buyer.getId()).toUri();
        return ResponseEntity.created(location).body(buyer);
    }

    @PutMapping(ApiUrls.URL_BUYERS_BUYER)
    public ResponseEntity<?> update(@PathVariable("buyerId") long id, @Validated @RequestBody Buyer buyer) {
        logger.trace("update(): id = {} \n {}", id, buyer);
        if (!buyerService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        buyer.setId(id);
        buyer = buyerService.update(buyer);
        return new ResponseEntity<>(buyer, HttpStatus.OK);
    }

    @DeleteMapping(ApiUrls.URL_BUYERS_BUYER)
    public ResponseEntity<?> delete(@PathVariable("buyerId") long id) {
        logger.trace("delete(): id = {}", id);
        if (!buyerService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        buyerService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
