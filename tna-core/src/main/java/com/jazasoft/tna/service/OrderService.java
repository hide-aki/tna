package com.jazasoft.tna.service;

import com.jazasoft.tna.Constants;
import com.jazasoft.tna.entity.*;
import com.jazasoft.tna.repository.*;
import com.jazasoft.tna.util.Graph;
import com.jazasoft.tna.util.Node;
import com.jazasoft.tna.util.TnaUtils;
import com.jazasoft.util.DateUtils;
import com.jazasoft.util.Utils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    int currentLeadTime = (int) DAYS.between(DateUtils.toLocalDate(order.getOrderDate()), DateUtils.toLocalDate(order.getExFactoryDate()));

    Set<OActivity> oActivityList = new HashSet<>();
    for (TActivity tActivity : timeline.getTActivityList()) {
      OActivity oActivity = new OActivity();
      oActivity.setOrder(order);
      oActivity.setTActivity(tActivity);

      oActivity.setName(tActivity.getName());
      oActivity.setLeadTime(TnaUtils.getLeadTime(tActivity.getLeadTime(), currentLeadTime, timeline.getStdLeadTime()));

      Set<OSubActivity> oSubActivityList = new HashSet<>();

      for (TSubActivity tSubActivity : tActivity.getTSubActivityList()) {
        OSubActivity oSubActivity = new OSubActivity();
        oSubActivity.setTSubActivity(tSubActivity);
        oSubActivity.setOActivity(oActivity);

        oSubActivity.setLeadTime(TnaUtils.getLeadTime(tSubActivity.getLeadTime(), currentLeadTime, timeline.getStdLeadTime()));
        oSubActivity.setName(tSubActivity.getSubActivity().getName());

        oSubActivityList.add(oSubActivity);
      }
      oActivity.setOSubActivityList(oSubActivityList);

      oActivityList.add(oActivity);
    }

    order.setOActivityList(oActivityList);

    // Set FinalLeadTime
    List<Node> nodeList = order.getOActivityList().stream().map(oActivity -> new Node(oActivity.getTActivity().getId(), oActivity.getLeadTime(), oActivity.getTActivity().getTimeFrom())).collect(Collectors.toList());
    Graph graph = new Graph(nodeList);

    // Create Edges
    for (OActivity oActivity : order.getOActivityList()) {
      if (!Constants.FROM_ORDER_DATE.equals(oActivity.getTActivity().getTimeFrom())) {
        List<Long> ids = Utils.getListFromCsv(oActivity.getTActivity().getTimeFrom()).stream().map(Long::parseLong).collect(Collectors.toList());
        for (Long id : ids) {
          graph.addEdge(oActivity.getTActivity().getId(), id);
        }
      }
    }

    for (OActivity oActivity : order.getOActivityList()) {
      oActivity.setFinalLeadTime(graph.getFinalLeadTime(oActivity.getTActivity().getId()));
      oActivity.setDueDate(DateUtils.fromLocalDate(DateUtils.toLocalDate(order.getOrderDate()).plusDays(oActivity.getFinalLeadTime())));

      for (OSubActivity oSubActivity: oActivity.getOSubActivityList()) {
        oSubActivity.setDueDate(DateUtils.fromLocalDate(DateUtils.toLocalDate(oActivity.getDueDate()).plusDays(oSubActivity.getLeadTime())));
      }
    }
    return orderRepository.save(order);
  }

  @Transactional(value = "tenantTransactionManager")
  public Order update(Order order, String action) {
    Order mOrder = orderRepository.findById(order.getId()).orElseThrow();

    if (action.equalsIgnoreCase("default")) {
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
    } else if (action.equalsIgnoreCase("override")) {
      for (OActivity oActivity : order.getOActivityList()) {
        OActivity mOActivity = mOrder.getOActivityList().stream().filter(a -> a.getId().equals(oActivity.getId())).findAny().orElse(null);
        if (mOActivity != null && Boolean.TRUE.equals(mOActivity.getTActivity().getOverridable())) {
          int diff =  oActivity.getFinalLeadTime() - mOActivity.getFinalLeadTime();
          mOActivity.setLeadTime(mOActivity.getLeadTime() + diff);
        }
      }
      // Set FinalLeadTime
      List<Node> nodeList = mOrder.getOActivityList().stream().map(oActivity -> new Node(oActivity.getTActivity().getId(), oActivity.getLeadTime(), oActivity.getTActivity().getTimeFrom())).collect(Collectors.toList());
      Graph graph = new Graph(nodeList);

      // Create Edges
      for (OActivity oActivity : mOrder.getOActivityList()) {
        if (!Constants.FROM_ORDER_DATE.equals(oActivity.getTActivity().getTimeFrom())) {
          List<Long> ids = Utils.getListFromCsv(oActivity.getTActivity().getTimeFrom()).stream().map(Long::parseLong).collect(Collectors.toList());
          for (Long id : ids) {
            graph.addEdge(oActivity.getTActivity().getId(), id);
          }
        }
      }

      for (OActivity oActivity : mOrder.getOActivityList()) {
        oActivity.setFinalLeadTime(graph.getFinalLeadTime(oActivity.getTActivity().getId()));
        oActivity.setDueDate(DateUtils.fromLocalDate(DateUtils.toLocalDate(mOrder.getOrderDate()).plusDays(oActivity.getFinalLeadTime())));

        for (OSubActivity oSubActivity: oActivity.getOSubActivityList()) {
          oSubActivity.setDueDate(DateUtils.fromLocalDate(DateUtils.toLocalDate(oActivity.getDueDate()).plusDays(oSubActivity.getLeadTime())));
        }
      }
    }

    return mOrder;
  }


  @Transactional(value = "tenantTransactionManager")
  public List<Order> updateAll(List<Order> orderList, Long departmentId) {
    // Fetch Orders from database
    Set<Long> ids = orderList.stream().map(Order::getId).collect(Collectors.toSet());
    List<Order> mOrderList = orderRepository.findAllById(ids);

    for (Order order : orderList) {
      Order mOrder = mOrderList.stream().filter(o -> o.getId().equals(order.getId())).findAny().orElseThrow(() -> new RuntimeException("Order with id " + order.getId() + " not found."));
      if (order.getOActivityList() == null || mOrder.getOActivityList() == null) continue;

      for (OActivity oActivity : order.getOActivityList()) {

        OActivity mActivity = mOrder.getOActivityList().stream().filter(a -> a.getId().equals(oActivity.getId())).findAny().orElse(null);
        if (mActivity != null) {
          if (departmentId == -1L || (mActivity.getTActivity() != null && mActivity.getTActivity().getDepartment() != null && departmentId.equals(mActivity.getTActivity().getDepartment().getId()))) {
            mActivity.setCompletedDate(oActivity.getCompletedDate());
            mActivity.setDelayReason(oActivity.getDelayReason());
            mActivity.setRemarks(oActivity.getRemarks());
            if (oActivity.getOSubActivityList() == null || mActivity.getOSubActivityList() == null)
              continue;

            for (OSubActivity oSubActivity : oActivity.getOSubActivityList()) {
              OSubActivity mSubActivity = mActivity.getOSubActivityList().stream().filter(sa -> sa.getId().equals(oSubActivity.getId())).findAny().orElse(null);
              if (mSubActivity == null) continue;
              mSubActivity.setCompletedDate(oSubActivity.getCompletedDate());
              mSubActivity.setRemarks(oSubActivity.getRemarks());
            }
          } else {
            Hibernate.initialize(mActivity.getOSubActivityList());
          }
        }
      }
    }
    return mOrderList;
  }

  @Transactional(value = "tenantTransactionManager")
  public OActivity updateActivity(Long orderId, OActivity oActivity) {
    Order mOrder = orderRepository.getOne(orderId);
    OActivity mOActivity = oActivityRepository.findOneByOrderAndId(mOrder, oActivity.getId()).orElseThrow(() -> new RuntimeException("Activity with id = " + oActivity.getId() + " not found for orderId = " + orderId));

    mOActivity.setCompletedDate(oActivity.getCompletedDate());
    mOActivity.setDelayReason(oActivity.getDelayReason());
    mOActivity.setRemarks(oActivity.getRemarks());
    return mOActivity;
  }

  @Transactional(value = "tenantTransactionManager")
  public OSubActivity updateSubActivity(Long oActivityId, OSubActivity oSubActivity) {

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
