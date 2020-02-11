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

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

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

  /**
   * Fetch all orders.
   *
   * <p>
   *
   * Param: view
   *  <ul>
   *    <li>{@code list} - Do not initialize activities</li>
   *    <li>{@code grid} - Initialize activities but not sub activities</li>
   *    <li>{@code grid-deep} Initialize activities as well as sub activities</li>
   *  </ul>
   *
   * @param search
   * @param view values - [list, grid, grid-deep]
   * @param pageable
   * @return
   */
  @GetMapping
  public ResponseEntity<?> findAll(@RequestParam(value = "search", defaultValue = "") String search,
                                   @RequestParam(value = "view", defaultValue = "list") String view,
                                   Pageable pageable) {
    Page<Order> pages;
    if (search.trim().isEmpty()) {
      pages = orderService.findAll(pageable, view);
    } else {
      Node rootNode = new RSQLParser().parse(search);
      Specification<Order> spec = rootNode.accept(new CustomRsqlVisitor<>());
      pages = orderService.findAll(spec, pageable, view);
    }
    if (view.equalsIgnoreCase("list")) {
      pages.forEach(order -> order.setOActivityList(null));
    }  else if (view.equalsIgnoreCase("grid")) {
      pages.forEach(order -> {
        order.getOActivityList().forEach(oActivity -> {
          if (oActivity.getTActivity() != null) {
            oActivity.getTActivity().setTimeline(null);
            oActivity.getTActivity().setActivity(null);
            oActivity.getTActivity().setTSubActivityList(null);
          }
          oActivity.setOSubActivityList(null);
        });
      });
    } else if (view.equalsIgnoreCase("grid-deep")) {
      pages.forEach(order -> {
        order.getOActivityList().forEach(oActivity -> {
          if (oActivity.getTActivity() != null) {
            oActivity.getTActivity().setTimeline(null);
            oActivity.getTActivity().setActivity(null);
            oActivity.getTActivity().setTSubActivityList(null);
          }
          oActivity.getOSubActivityList().forEach(oSubActivity -> {
            oSubActivity.setTSubActivity(null);
            oSubActivity.setOActivity(null);
          });
        });
      });
    }
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
      if (oActivity.getTActivity() != null) {
        oActivity.getTActivity().setTimeline(null);
        oActivity.getTActivity().setActivity(null);
        oActivity.getTActivity().setTSubActivityList(null);
      }
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
      if (oActivity.getTActivity() != null) {
        oActivity.getTActivity().setTimeline(null);
        oActivity.getTActivity().setActivity(null);
        oActivity.getTActivity().setTSubActivityList(null);
      }
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
      if (oActivity.getTActivity() != null) {
        oActivity.getTActivity().setTimeline(null);
        oActivity.getTActivity().setActivity(null);
        oActivity.getTActivity().setTSubActivityList(null);
      }
      oActivity.getOSubActivityList().forEach(oSubActivity -> {
        oSubActivity.setOActivity(null);
        oSubActivity.setTSubActivity(null);
      });
    });
    return ResponseEntity.ok(mOrder);
  }

  @PutMapping
  public ResponseEntity<?> updateAll(@RequestBody List<Order> orderList, HttpServletRequest request) {

//    List<String> buyerIds = (List<String>) request.getAttribute(Constants.REQ_ATTRIBUTE_BUYER);

    List<Order> mOrderList = orderService.updateAll(orderList);
    mOrderList.forEach(order -> {
      order.getOActivityList().forEach(oActivity -> {
        if (oActivity.getTActivity() != null) {
          oActivity.getTActivity().setTimeline(null);
          oActivity.getTActivity().setActivity(null);
          oActivity.getTActivity().setTSubActivityList(null);
        }
        oActivity.getOSubActivityList().forEach(oSubActivity -> {
          oSubActivity.setOActivityId(oActivity.getId());
          oSubActivity.setOActivity(null);
          oSubActivity.setTSubActivity(null);
        });
      });
    });
    return ResponseEntity.ok(mOrderList);
  }

  @PutMapping(ApiUrls.URL_ORDERS_ORDER + URL_ORDERS_ORDER_ACTIVITIES_ACTIVITY)
  public ResponseEntity<?> updateOrderActivity(@PathVariable(value = "orderId") Long orderId,
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
