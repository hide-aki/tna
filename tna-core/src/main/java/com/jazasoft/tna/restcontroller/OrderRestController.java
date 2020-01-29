package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.entity.OActivity;
import com.jazasoft.tna.entity.OSubActivity;
import com.jazasoft.tna.entity.Order;
import com.jazasoft.tna.entity.SubActivity;
import com.jazasoft.tna.service.OrderService;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.jazasoft.tna.ApiUrls.URL_ORDERS_ORDER_ACTIVITIES_ACTIVITY;
import static com.jazasoft.tna.ApiUrls.URL_ORDERS_ORDER_SUBACTIVITIES_SUBACTIVITY;

@RestController
@RequestMapping(ApiUrls.ROOT_URL_ORDERS)
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
        pages.forEach(order -> order.setBuyerId(order.getBuyer() != null ? order.getBuyer().getId() : null));
        pages.forEach(order -> order.getTimeline().setBuyer(null));
        pages.forEach(order -> order.setOActivityList(null));
        return ResponseEntity.ok(pages);
    }

    @GetMapping(ApiUrls.URL_ORDERS_ORDER)
    public ResponseEntity<?> findOne(@PathVariable("orderId") Long id) {
        Order order = orderService.findOne(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        order.setBuyerId(order.getBuyer() != null ? order.getBuyer().getId() : null);
        order.setTimelineId(order.getTimeline() != null ? order.getTimeline().getId() : null);
        order.setGarmentTypeId(order.getTimeline() != null ? order.getTimeline().getId() : null);
        order.setSeasonId(order.getSeason() != null ? order.getSeason().getId() : null);

        order.getTimeline().setBuyer(null);
        order.getTimeline().getTActivityList().forEach(tActivity -> tActivity.setActivity(null));
        order.getTimeline().getTActivityList().forEach(tActivity -> tActivity.setTSubActivityList(null));
        order.getTimeline().setTActivityList(null);


        for (OActivity oActivity : order.getOActivityList()) {
            for (OSubActivity oSubActivity : oActivity.getOSubActivityList()) {
                oSubActivity.setOActivityId(oSubActivity.getOActivity() != null ? oSubActivity.getOActivity().getId() : null);
                oSubActivity.setTSubActivityId(oSubActivity.getTSubActivity() != null ? oSubActivity.getTSubActivity().getId() : null);
                oSubActivity.getTSubActivity().setTActivity(null);
            }
        }
        order.getOActivityList().forEach(oActivity -> oActivity.getOSubActivityList().forEach(oSubActivity -> oSubActivity.setOActivity(null)));
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Order order) {
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

    @PutMapping(ApiUrls.URL_ORDERS_ORDER)
    private ResponseEntity<?> update(@PathVariable("orderId") Long id, @RequestBody Order order) {
        logger.trace("update(): id = {}", id);
        if (!orderService.exists(id)) {
            return ResponseEntity.notFound().build();
        }
        order.setId(id);
        Order mOrder = orderService.update(order);

        //setting relational fields
        mOrder.setBuyerId(order.getBuyerId());
        mOrder.setTimelineId(order.getTimelineId());
        mOrder.setGarmentTypeId(order.getGarmentTypeId());
        mOrder.setSeasonId(order.getSeasonId());

        mOrder.getTimeline().getTActivityList().forEach(tActivity -> tActivity.getActivity().setSubActivityList(null));
        mOrder.getTimeline().getTActivityList().forEach(tActivity -> tActivity.getActivity().setDepartment(null));
        mOrder.getTimeline().getTActivityList().forEach(tActivity -> tActivity.getTSubActivityList().forEach(tSubActivity -> tSubActivity.setTActivity(null)));
        mOrder.getOActivityList().forEach(oActivity -> oActivity.getOSubActivityList().forEach(oSubActivity -> oSubActivity.setOActivity(null)));
        mOrder.getTimeline().getTActivityList().forEach(tActivity -> tActivity.getTSubActivityList().forEach(tSubActivity -> tSubActivity.setSubActivity(null)));
        mOrder.getOActivityList().forEach(oActivity -> oActivity.getOSubActivityList().forEach(oSubActivity -> oSubActivity.setTSubActivity(null)));
        mOrder.getOActivityList().forEach(oActivity -> oActivity.setTActivity(null));
        return ResponseEntity.ok(mOrder);
    }

    @PutMapping(ApiUrls.URL_ORDERS_ORDER + URL_ORDERS_ORDER_ACTIVITIES_ACTIVITY)
    public ResponseEntity updateOrderActivity(@PathVariable(value = "orderId") Long orderId,
                                              @PathVariable(value = "activityId") Long activityId,
                                              @RequestBody OActivity oActivity) {

        oActivity.setId(activityId);
        OActivity oActivity1 = orderService.updateOrderActivity(orderId, oActivity);

//        oActivity1.getOSubActivityList().forEach(oSubActivity -> oSubActivity.setOActivity(null));
//        oActivity1.getOSubActivityList().forEach(oSubActivity -> oSubActivity.setTSubActivity(null));
//        oActivity1.getTActivity().setTSubActivityList(null);
//        oActivity1.setOSubActivityList(null);

        oActivity1.setOSubActivityList(null);
        oActivity1.setTActivity(null);
        return ResponseEntity.ok(oActivity1);
    }

    @PutMapping(ApiUrls.URL_ORDERS_ORDER + URL_ORDERS_ORDER_ACTIVITIES_ACTIVITY+ URL_ORDERS_ORDER_SUBACTIVITIES_SUBACTIVITY)
    public ResponseEntity<?> updateOrderSubActivity(@PathVariable (value = "orderId")Long orderId,
                                                    @PathVariable (value = "activityId")Long activityId,
                                                    @PathVariable (value = "subActivityId")Long subActivityId,
                                                    @RequestBody OSubActivity oSubActivity ){
        oSubActivity.setId(subActivityId);
        OSubActivity mSubActivity =  orderService.updateOrderOSubActivity(activityId, oSubActivity);

        mSubActivity.setOActivity(null);
        mSubActivity.setTSubActivity(null);

        return ResponseEntity.ok(mSubActivity);
    }



    @DeleteMapping(ApiUrls.URL_ORDERS_ORDER)
    public ResponseEntity<?> delete(@PathVariable("orderId") Long id) {
        if (!orderService.exists(id)) {
            return ResponseEntity.notFound().build();
        }
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
