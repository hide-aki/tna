package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.entity.OActivity;
import com.jazasoft.tna.entity.OSubActivity;
import com.jazasoft.tna.entity.Order;
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
    pages.forEach(order -> order.setBuyerId(order.getBuyer() != null ? order.getBuyer().getId() : null));
    pages.forEach(order -> order.setOActivityList(null));
    return ResponseEntity.ok(pages);
  }

  @GetMapping(ApiUrls.URL_ORDERS_ORDER)
  public ResponseEntity<?> findOne(@PathVariable("orderId") Long id) {
    Order mOrder = orderService.findOne(id);
    if (mOrder == null) {
      return ResponseEntity.notFound().build();
    }

    mOrder.setBuyerId(mOrder.getBuyer() != null ? mOrder.getBuyer().getId() : null);
    mOrder.setGarmentTypeId(mOrder.getGarmentType() != null ? mOrder.getGarmentType().getId() : null);
    mOrder.setSeasonId(mOrder.getSeason() != null ? mOrder.getSeason().getId() : null);

    mOrder.getOActivityList().forEach(oActivity -> {
      oActivity.setTActivity(null);
      oActivity.getOSubActivityList().forEach(oSubActivity -> {
        oSubActivity.setOActivityId(oActivity.getId());
        oSubActivity.setOActivity(null);
        oSubActivity.setTSubActivity(null);
      });
    });
    return ResponseEntity.ok(mOrder);
  }

  @PostMapping
  public ResponseEntity<?> save(@RequestBody Order order) {

    order = orderService.save(order);

    order.getOActivityList().forEach(oActivity -> {
      oActivity.setTActivity(null);
      oActivity.getOSubActivityList().forEach(oSubActivity -> {
        oSubActivity.setOActivity(null);
        oSubActivity.setTSubActivity(null);
      });
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
    mOrder.setGarmentTypeId(order.getGarmentTypeId());
    mOrder.setSeasonId(order.getSeasonId());

    mOrder.getOActivityList().forEach(oActivity -> {
      oActivity.setTActivity(null);
      oActivity.getOSubActivityList().forEach(oSubActivity -> {
        oSubActivity.setOActivity(null);
        oSubActivity.setTSubActivity(null);
      });
    });
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

  @PutMapping(ApiUrls.URL_ORDERS_ORDER + URL_ORDERS_ORDER_ACTIVITIES_ACTIVITY + URL_ORDERS_ORDER_SUBACTIVITIES_SUBACTIVITY)
  public ResponseEntity<?> updateOrderSubActivity(@PathVariable(value = "orderId") Long orderId,
                                                  @PathVariable(value = "activityId") Long activityId,
                                                  @PathVariable(value = "subActivityId") Long subActivityId,
                                                  @RequestBody OSubActivity oSubActivity) {
    oSubActivity.setId(subActivityId);
    OSubActivity mSubActivity = orderService.updateOrderOSubActivity(activityId, oSubActivity);

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
