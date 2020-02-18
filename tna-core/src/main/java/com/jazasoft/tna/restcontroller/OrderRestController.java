package com.jazasoft.tna.restcontroller;

import com.jazasoft.mtdb.dto.RestError;
import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.ApiUrls;
import com.jazasoft.tna.Constants;
import com.jazasoft.tna.entity.OActivity;
import com.jazasoft.tna.entity.OSubActivity;
import com.jazasoft.tna.entity.Order;
import com.jazasoft.tna.service.OrderService;
import com.jazasoft.util.Utils;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
  @SuppressWarnings("unchecked")
  public ResponseEntity<?> findAll(@RequestParam(value = "search", defaultValue = "") String search,
                                   @RequestParam(value = "view", defaultValue = "list") String view,
                                   HttpServletRequest request,
                                   Pageable pageable) {
    // Extract Buyer Privilege from Request
    List<String> buyerIds = new ArrayList<>();
    Object attrBuyer = request.getAttribute(Constants.REQ_ATTRIBUTE_BUYER);
    if (attrBuyer instanceof List) {
      buyerIds = (List<String>) attrBuyer;
    }
    // If buyer privilege is present and there is no filter on buyer, apply filter
    if (!search.contains("buyer") && !buyerIds.isEmpty()) {
      search = search.isEmpty() ? search : search + ";";
      search += "buyer.id=in=(" + Utils.getCsvFromIterable(buyerIds) + ")";
    }

    // Fetch User from database
    Page<Order> pages;
    if (search.trim().isEmpty()) {
      pages = orderService.findAll(pageable, view);
    } else {
      Node rootNode = new RSQLParser().parse(search);
      Specification<Order> spec = rootNode.accept(new CustomRsqlVisitor<>());
      pages = orderService.findAll(spec, pageable, view);
    }

    // If buyer privilege is present and there was already filter on buyer, Make sure that filter was not for other buyers
    if (!buyerIds.isEmpty() && search.contains("buyer")) {
      List<Order> orders = pages.getContent();
      Set<Long> ids = buyerIds.stream().map(Long::parseLong).collect(Collectors.toSet());
      orders = orders.stream().filter(order -> order.getBuyer() != null && ids.contains(order.getBuyer().getId())).collect(Collectors.toList());
      pages = new PageImpl<>(orders);
    }

    // Sanitize data by remove unncessary fields
    if (view.equalsIgnoreCase("list")) {
      pages.forEach(order -> {
        order.setOActivityList(null);
        sanitize(order);
      });
    }  else if (view.equalsIgnoreCase("grid")) {
      pages.forEach(order -> {
        order.getOActivityList().forEach(oActivity -> oActivity.setOSubActivityList(null));
        sanitize(order);
      });
    } else if (view.equalsIgnoreCase("grid-deep")) {
      pages.forEach(this::sanitize);
    }
    return ResponseEntity.ok(pages);
  }

  @GetMapping(ApiUrls.URL_ORDERS_ORDER)
  public ResponseEntity<?> findOne(@PathVariable("orderId") Long id) {
    Order mOrder = orderService.findOne(id);
    if (mOrder == null) {
      return ResponseEntity.notFound().build();
    }
    sanitize(mOrder);
    return ResponseEntity.ok(mOrder);
  }

  @PostMapping
  public ResponseEntity<?> save(@RequestBody Order order, HttpServletRequest request) {
    // Extract Buyer Privilege from Request
    List<String> buyerIds = new ArrayList<>();
    Object attrBuyer = request.getAttribute(Constants.REQ_ATTRIBUTE_BUYER);
    if (attrBuyer instanceof List) {
      buyerIds = (List<String>) attrBuyer;
    }
    if (order.getBuyerId() == null) {
      RestError error = new RestError(400, 40000, "'buyerId' is Required");
      return ResponseEntity.badRequest().body(error);
    }
    if (!buyerIds.isEmpty() && !buyerIds.contains(String.valueOf(order.getBuyerId()))) {
      RestError error = new RestError(400, 40000, "Access denied. You do not have privilege to create Order for this buyer.");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    order = orderService.save(order);

    sanitize(order);

    URI Location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(order.getId()).toUri();
    return ResponseEntity.created(Location).body(order);
  }

  @PutMapping(ApiUrls.URL_ORDERS_ORDER)
  private ResponseEntity<?> update(@PathVariable("orderId") Long id,
                                   @RequestParam(value = "action", defaultValue = "default") String action,
                                   @RequestBody Order order,HttpServletRequest request) {
    logger.trace("update(): id = {}", id);
    Pattern pattern = Pattern.compile("default|override", Pattern.CASE_INSENSITIVE);
    if (!pattern.matcher(action).matches()){
      return ResponseEntity.badRequest().body("Invalid action. Supported actions are " + pattern.pattern());
    }
    if (!orderService.exists(id)) {
      return ResponseEntity.notFound().build();
    }
    // Extract Buyer Privilege from Request
    List<String> buyerIds = new ArrayList<>();
    Object attrBuyer = request.getAttribute(Constants.REQ_ATTRIBUTE_BUYER);
    if (attrBuyer instanceof List) {
      buyerIds = (List<String>) attrBuyer;
    }
    if (order.getBuyerId() == null) {
      RestError error = new RestError(400, 40000, "'buyerId' is Required");
      return ResponseEntity.badRequest().body(error);
    }
    if (!buyerIds.isEmpty() && !buyerIds.contains(String.valueOf(order.getBuyerId()))) {
      RestError error = new RestError(400, 40000, "Access denied. You do not have privilege to update order for this buyer.");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    order.setId(id);
    Order mOrder = orderService.update(order,action);

    sanitize(mOrder);
    return ResponseEntity.ok(mOrder);
  }

  @PutMapping
  public ResponseEntity<?> updateAll(@RequestBody List<Order> orderList, HttpServletRequest request) {
    // Extract Buyer Privilege from Request
    List<String> buyerIds = new ArrayList<>();
    Object attrBuyer = request.getAttribute(Constants.REQ_ATTRIBUTE_BUYER);
    if (attrBuyer instanceof List) {
      buyerIds = (List<String>) attrBuyer;
    }
    // Restrict if trying update Orders of buyers for which privilege is not provided
    if (!buyerIds.isEmpty()) {
      Set<Long> ids = buyerIds.stream().map(Long::parseLong).collect(Collectors.toSet());
      Set<String> rIds = orderList.stream().map(Order::getBuyerId).filter(id -> !ids.contains(id)).map(String::valueOf).collect(Collectors.toSet());
      if (!rIds.isEmpty()) {
        RestError error = new RestError(400, 40000, "Access denied. You do not have privilege to update order for buyer with ids = [" + Utils.getCsvFromIterable(rIds) +"].");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
      }
    }

    Long departmentId = -1L;
    Object attrDepartment = request.getAttribute(Constants.REQ_ATTRIBUTE_DEPARTMENT);
    if (attrDepartment instanceof List) {
      List<Long> ids = ((List<String>)attrDepartment).stream().map(Long::parseLong).collect(Collectors.toList());
      if (ids.size() > 1) {
        RestError error = new RestError(409, 409, "User cannot belong to multiple Department");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
      }
      if (!ids.isEmpty()) {
        departmentId = ids.get(0);
      }
    }

    List<Order> mOrderList = orderService.updateAll(orderList, departmentId);
    mOrderList.forEach(this::sanitize);
    return ResponseEntity.ok(mOrderList);
  }

  @PutMapping(ApiUrls.URL_ORDERS_ORDER + URL_ORDERS_ORDER_ACTIVITIES_ACTIVITY)
  public ResponseEntity<?> updateActivity(@PathVariable(value = "orderId") Long orderId,
                                          @PathVariable(value = "activityId") Long activityId,
                                          @RequestBody OActivity oActivity) {
    oActivity.setId(activityId);
    OActivity mActivity = orderService.updateActivity(orderId, oActivity);
    mActivity.setOSubActivityList(null);
    if (mActivity.getTActivity() != null) {
      mActivity.getTActivity().setTimeline(null);
      mActivity.getTActivity().setActivity(null);
      mActivity.getTActivity().setTSubActivityList(null);
      mActivity.getTActivity().setDepartmentId(mActivity.getTActivity().getDepartment() != null ? mActivity.getTActivity().getDepartment().getId() : null);
    }
    return ResponseEntity.ok(mActivity);
  }

  @PutMapping(ApiUrls.URL_ORDERS_ORDER + URL_ORDERS_ORDER_ACTIVITIES_ACTIVITY + URL_ORDERS_ORDER_SUBACTIVITIES_SUBACTIVITY)
  public ResponseEntity<?> updateSubActivity(@PathVariable(value = "orderId") Long orderId,
                                                  @PathVariable(value = "activityId") Long activityId,
                                                  @PathVariable(value = "subActivityId") Long subActivityId,
                                                  @RequestBody OSubActivity oSubActivity) {
    oSubActivity.setId(subActivityId);
    OSubActivity mSubActivity = orderService.updateSubActivity(activityId, oSubActivity);

    mSubActivity.setOActivityId(mSubActivity.getOActivity() != null ? mSubActivity.getOActivity().getId() : null);
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

  // Sanitize by remove unnecessary fields before sending JSON
  private void sanitize(Order order) {
    if (order == null) return;
    order.setBuyerId(order.getBuyer() != null ? order.getBuyer().getId() : null);
    order.setGarmentTypeId(order.getGarmentType() != null ? order.getGarmentType().getId() : null);
    order.setSeasonId(order.getSeason() != null ? order.getSeason().getId() : null);
    if (order.getOActivityList() != null) {
      order.getOActivityList().forEach(oActivity -> {

        if (oActivity.getTActivity() != null) {
          oActivity.getTActivity().setTimeline(null);
          oActivity.getTActivity().setActivity(null);
          oActivity.getTActivity().setTSubActivityList(null);
          oActivity.getTActivity().setDepartmentId(oActivity.getTActivity().getDepartment() != null ? oActivity.getTActivity().getDepartment().getId() : null);
        }
        if (oActivity.getOSubActivityList() != null) {
          oActivity.getOSubActivityList().forEach(oSubActivity -> {
            oSubActivity.setOActivityId(oActivity.getId());
            oSubActivity.setOActivity(null);
            oSubActivity.setTSubActivity(null);
          });
        }

      });
    }
  }
}
