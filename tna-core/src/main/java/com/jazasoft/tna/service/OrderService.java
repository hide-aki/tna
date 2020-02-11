package com.jazasoft.tna.service;

import com.jazasoft.tna.Constants;
import com.jazasoft.tna.entity.*;
import com.jazasoft.tna.repository.*;
import com.jazasoft.tna.util.TnaUtils;
import com.jazasoft.util.Assert;
import com.jazasoft.util.DateUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Transactional(value = "tenantTransactionManager", readOnly = true)
public class OrderService {

  private final Logger logger = LoggerFactory.getLogger(OrderService.class);

  private final OrderRepository orderRepository;
  private final BuyerRepository buyerRepository;
  private final TimelineRepository timelineRepository;
  private final GarmentTypeRepository garmentTypeRepository;
  private final SeasonRepository seasonRepository;
  private final OActivityRepository oActivityRepository;
  private final OSubActivityRepository oSubActivityRepository;

  public OrderService(OrderRepository orderRepository, BuyerRepository buyerRepository, TimelineRepository timelineRepository, GarmentTypeRepository garmentTypeRepository, SeasonRepository seasonRepository, OActivityRepository oActivityRepository, OSubActivityRepository oSubActivityRepository) {
    this.orderRepository = orderRepository;
    this.buyerRepository = buyerRepository;
    this.timelineRepository = timelineRepository;
    this.garmentTypeRepository = garmentTypeRepository;
    this.seasonRepository = seasonRepository;
    this.oActivityRepository = oActivityRepository;
    this.oSubActivityRepository = oSubActivityRepository;
  }

  public Page<Order> findAll(Pageable pageable, String view) {
    Page<Order> page = orderRepository.findAll(pageable);
    if (view.equalsIgnoreCase("grid")) {
      page.forEach(order -> Hibernate.initialize(order.getOActivityList()));
    }
    if (view.equalsIgnoreCase("grid-deep")) {
      page.forEach(order -> {
        Hibernate.initialize(order.getOActivityList());
        order.getOActivityList().forEach(oActivity -> Hibernate.initialize(oActivity.getOSubActivityList()));
      });
    }
    return page;
  }

  public Page<Order> findAll(Specification<Order> spec, Pageable pageable, String view) {
    Page<Order> page = orderRepository.findAll(spec, pageable);
    if (view.equalsIgnoreCase("grid")) {
      page.forEach(order -> Hibernate.initialize(order.getOActivityList()));
    }
    if (view.equalsIgnoreCase("grid-deep")) {
      page.forEach(order -> {
        Hibernate.initialize(order.getOActivityList());
        order.getOActivityList().forEach(oActivity -> Hibernate.initialize(oActivity.getOSubActivityList()));
      });
    }
    return page;
  }

  public Order findOne(Long id) {
    Order order = orderRepository.findById(id).orElse(null);
    return order;
  }

  @Transactional(value = "tenantTransactionManager")
  public Order save(Order order) {
    Timeline timeline = timelineRepository.findById(order.getTimelineId()).orElseThrow();

    if (order.getBuyerId() != null) {
      order.setBuyer(buyerRepository.findById(order.getBuyerId()).orElse(null));
    }
    if (order.getGarmentTypeId() != null) {
      order.setGarmentType(garmentTypeRepository.findById(order.getGarmentTypeId()).orElse(null));
    }
    if (order.getSeasonId() != null) {
      order.setSeason(seasonRepository.findById(order.getSeasonId()).orElse(null));
    }

    // Calculate Standard Lead Time
    int standardLeadTime = TnaUtils.getStandardLeadTime(timeline.getTActivityList());
    int currentLeadTime = (int) DAYS.between(DateUtils.toLocalDate(order.getOrderDate()), DateUtils.toLocalDate(order.getExFactoryDate()));


    Set<OActivity> oActivityList = new HashSet<>();
    for (TActivity tActivity : timeline.getTActivityList()) {
      OActivity oActivity = new OActivity();
      oActivity.setOrder(order);
      oActivity.setTActivity(tActivity);

      int leadTime = TnaUtils.getLeadTime(tActivity.getLeadTime(), currentLeadTime, standardLeadTime);

      oActivity.setName(tActivity.getName());
      oActivity.setLeadTime(leadTime);
      oActivity.setTimeFrom(tActivity.getTimeFrom());

      Set<OSubActivity> oSubActivityList = new HashSet<>();

      for (TSubActivity tSubActivity : tActivity.getTSubActivityList()) {
        OSubActivity oSubActivity = new OSubActivity();
        oSubActivity.setTSubActivity(tSubActivity);
        oSubActivity.setOActivity(oActivity);

        //TODO: implement actual logic
        oSubActivity.setLeadTime(tSubActivity.getLeadTime());
        oSubActivity.setName(tSubActivity.getSubActivity().getName());

        oSubActivityList.add(oSubActivity);
      }
      oActivity.setOSubActivityList(oSubActivityList);

      oActivityList.add(oActivity);
    }

    order.setOActivityList(oActivityList);

    return orderRepository.save(order);
  }

  @Transactional(value = "tenantTransactionManager")
  public Order update(Order order) {
    Order mOrder = orderRepository.findById(order.getId()).orElseThrow();

    mOrder.setPoRef(order.getPoRef());
    mOrder.setOrderQty(order.getOrderQty());
    mOrder.setStyle(order.getStyle());
    mOrder.setOrderDate(order.getOrderDate());
    mOrder.setRemarks(order.getRemarks());
    mOrder.setExFactoryDate(order.getExFactoryDate());

    if (order.getBuyerId() != null) {
      mOrder.setBuyer(buyerRepository.findById(order.getBuyerId()).orElse(null));
    }
    if (order.getGarmentTypeId() != null) {
      mOrder.setGarmentType(garmentTypeRepository.findById(order.getGarmentTypeId()).orElse(null));
    }
    if (order.getSeasonId() != null) {
      mOrder.setSeason(seasonRepository.findById(order.getSeasonId()).orElse(null));

    }

    return mOrder;
  }

  @Transactional(value = "tenantTransactionManager")
  public List<Order> updateAll(List<Order> orderList) {
    Set<Long> ids = orderList.stream().map(Order::getId).collect(Collectors.toSet());
    List<Order> mOrderList = orderRepository.findAllById(ids);

    for (Order order: orderList) {
      Order mOrder = mOrderList.stream().filter(o -> o.getId().equals(order.getId())).findAny().orElseThrow(() -> new RuntimeException("Order with id " + order.getId() + " not found."));
      if (order.getOActivityList() == null || mOrder.getOActivityList() == null) continue;
      for (OActivity oActivity: order.getOActivityList()) {
        OActivity mActivity = mOrder.getOActivityList().stream().filter(a -> a.getId().equals(oActivity.getId())).findAny().orElse(null);
        if (mActivity == null) continue;
        mActivity.setCompletedDate(oActivity.getCompletedDate());
        mActivity.setDelayReason(oActivity.getDelayReason());
        mActivity.setRemarks(oActivity.getRemarks());
        if (oActivity.getOSubActivityList() == null || mActivity.getOSubActivityList() == null) continue;
        for (OSubActivity oSubActivity: oActivity.getOSubActivityList()) {
          OSubActivity mSubActivity = mActivity.getOSubActivityList().stream().filter(sa -> sa.getId().equals(oSubActivity.getId())).findAny().orElse(null);
          if (mSubActivity == null) continue;
          mSubActivity.setCompletedDate(oSubActivity.getCompletedDate());
          mSubActivity.setRemarks(oSubActivity.getRemarks());
        }
      }
    }
    return mOrderList;
  }

  @Transactional(value = "tenantTransactionManager")
  public OActivity updateOrderActivity(Long orderId, OActivity oActivity) {

    Order mOrder = orderRepository.getOne(orderId);

    OActivity mOActivity = oActivityRepository.findOneByOrderAndId(mOrder, oActivity.getId()).orElseThrow(() -> new RuntimeException("Activity with id = " + oActivity.getId() + " not found for orderId = " + orderId));
    mOActivity.setCompletedDate(oActivity.getCompletedDate());
    mOActivity.setDelayReason(oActivity.getDelayReason());
    mOActivity.setRemarks(oActivity.getRemarks());

    return mOActivity;
  }

  @Transactional(value = "tenantTransactionManager")
  public OSubActivity updateOrderOSubActivity(Long oActivityId, OSubActivity oSubActivity) {

    OActivity mOActivity = oActivityRepository.getOne(oActivityId);

    OSubActivity mOSubActivity = oSubActivityRepository.findOneByOActivityAndId(mOActivity, oSubActivity.getId()).orElseThrow(() -> new RuntimeException("SubActivity with id = " + oSubActivity.getId() + " not found for activityId = " + oActivityId));
    mOSubActivity.setCompletedDate(oSubActivity.getCompletedDate());
    mOSubActivity.setRemarks(oSubActivity.getRemarks());

    return mOSubActivity;

  }

  public boolean exists(Long id) {
    return orderRepository.existsById(id);
  }

  @Transactional(value = "tenantTransactionManager")
  public void delete(Long id) {
    orderRepository.deleteById(id);
  }
}
