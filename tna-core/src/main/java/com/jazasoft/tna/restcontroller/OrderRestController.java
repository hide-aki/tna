package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.entity.Order;
import com.jazasoft.tna.entity.Timeline;
import com.jazasoft.tna.service.OrderService;
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

import java.net.URI;

@RestController
@RequestMapping(ApiUrls.ROOT_URL_OREDRS)
public class OrderRestController {

    Logger logger = LoggerFactory.getLogger(OrderRestController.class);

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "search", defaultValue = "") String search, Pageable pageable) {
        Page<Order> pages;
        if (search.trim().isEmpty()) {
            pages = orderService.findAll(pageable);
        } else {
            Node rootNode = new RSQLParser().parse(search);
            Specification<Order> spec = rootNode.accept(new CustomRsqlVisitor<>());
            pages = orderService.findAll(spec, pageable);
        }
        pages.forEach(order -> order.getTimeline().setTActivityList(null));
        pages.forEach(order -> order.setOActivityList(null));
        return ResponseEntity.ok(pages);
    }

    @GetMapping(ApiUrls.URL_ORDERS_ORDERS)
    public ResponseEntity<?> findOne(@PathVariable("orderId") Long id){
        Order order = orderService.findOne(id);
        if (order == null){
            return ResponseEntity.notFound().build();
        }
        order.getTimeline().setBuyer(null);
        order.getTimeline().getTActivityList().forEach(tActivity -> tActivity.setActivity(null));
        order.getTimeline().getTActivityList().forEach(tActivity -> tActivity.setTSubActivityList(null));
        order.getTimeline().setTActivityList(null);
        order.getOActivityList().forEach(oActivity -> oActivity.getOSubActivityList().forEach(oSubActivity -> oSubActivity.setOActivity(null)));

        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Order order){
        order = orderService.save(order);

        order.getTimeline().setBuyer(null);
        order.getOActivityList().forEach(oActivity -> {
            oActivity.getTActivity().getActivity().setSubActivityList(null);
            oActivity.getTActivity().setTSubActivityList(null);
            oActivity.getOSubActivityList().forEach(oSubActivity -> oSubActivity.setOActivity(null));
        });
        URI Location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(order.getId()).toUri();
        return ResponseEntity.created(Location).body(order);
    }

    @DeleteMapping(ApiUrls.URL_ORDERS_ORDERS)
    public ResponseEntity<?> delete(@PathVariable("orderId") Long id){
        if (!orderService.exists(id)){
            return ResponseEntity.notFound().build();
        }
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
