package com.jazasoft.tna.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jazasoft.mtdb.service.EmailServiceImpl;
import com.jazasoft.tna.Constants;
import com.jazasoft.tna.dto.Log;
import com.jazasoft.tna.entity.*;
import com.jazasoft.tna.OState;
import com.jazasoft.tna.repository.*;
import com.jazasoft.tna.util.Graph;
import com.jazasoft.tna.util.MapBuilder;
import com.jazasoft.tna.util.Node;
import com.jazasoft.tna.util.TnaUtils;
import com.jazasoft.util.Assert;
import com.jazasoft.util.DateUtils;
import com.jazasoft.util.JsonUtils;
import com.jazasoft.util.Utils;
import org.hibernate.Hibernate;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ValueChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.jazasoft.tna.util.TnaUtils.daysBetween;
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
  private final Javers javers;
  private final EmailServiceImpl emailService;
  private final UserRepository userRepository;

  private final ExecutorService threadPool = Executors.newFixedThreadPool(10);


  public OrderService(OrderRepository orderRepository, BuyerRepository buyerRepository, TimelineRepository timelineRepository, GarmentTypeRepository garmentTypeRepository, SeasonRepository seasonRepository, OActivityRepository oActivityRepository, OSubActivityRepository oSubActivityRepository, Javers javers, EmailServiceImpl emailService, UserRepository userRepository) {
    this.orderRepository = orderRepository;
    this.buyerRepository = buyerRepository;
    this.timelineRepository = timelineRepository;
    this.garmentTypeRepository = garmentTypeRepository;
    this.seasonRepository = seasonRepository;
    this.oActivityRepository = oActivityRepository;
    this.oSubActivityRepository = oSubActivityRepository;
    this.javers = javers;
    this.emailService = emailService;
    this.userRepository = userRepository;
  }

  public List<Order> findAll() {
    List<Order> orderList = orderRepository.findAll();
    orderList.forEach(order -> Hibernate.initialize(order.getOActivityList()));
    return orderList;
  }

  public List<Order> findAll(Specification<Order> spec) {
    List <Order> orderList =  orderRepository.findAll(spec);
    orderList.forEach(order -> Hibernate.initialize(order.getOActivityList()));
    return orderList;
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

  public List<Log> findOrderLogs(Long id) {
    Revisions<Integer, Order> revisions = orderRepository.findRevisions(id);

    List<Revision<Integer, Order>> revisionList = revisions.getContent().stream().sorted((a, b) -> a.getRevisionNumber().isPresent() && b.getRevisionNumber().isPresent() ? a.getRevisionNumber().get() - b.getRevisionNumber().get() : 0).collect(Collectors.toList());

    List<Log> logs = new ArrayList<>();

    Revision<Integer, Order> prev = null;
    for (Revision<Integer, Order> curr: revisionList) {
      Log log = new Log();

      MyRevisionEntity myRevisionEntity = curr.getMetadata().getDelegate();
      log.setTimestamp(myRevisionEntity.getTimestamp());
      log.setUser(myRevisionEntity.getUsername());

      String data = null;
      try {
        Order order = curr.getEntity();
        order.setOActivityList(null);
        data = JsonUtils.toString(order);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }

      String diffStr = null;
      String event = null;
      if (prev == null) { // Created Event
        event = "Order Created";
      } else {
        event = "Order Updated";
        Diff diff = javers.compare(new com.jazasoft.tna.audit.Order(prev.getEntity()), new com.jazasoft.tna.audit.Order(curr.getEntity()));
        if (diff.hasChanges()) {
          diffStr = diff.prettyPrint();
        }
      }
      log.setEvent(event);
      log.setData(data);
      log.setDiff(diffStr);

      logs.add(log);
      prev = curr;
    }

    return logs;
  }

  public List<Log> findActivityLogs(Long orderId, Long oActivityId) {
    Revisions<Integer, OActivity> revisions = oActivityRepository.findRevisions(oActivityId);

    List<Revision<Integer, OActivity>> revisionList = revisions.getContent().stream().sorted((a, b) -> a.getRevisionNumber().isPresent() && b.getRevisionNumber().isPresent() ? b.getRevisionNumber().get() - a.getRevisionNumber().get() : 0).collect(Collectors.toList());

    List<Log> logs = new ArrayList<>();

    Integer finalLeadTime = null;
    Revision<Integer, OActivity> prev = null;
    for (Revision<Integer, OActivity> curr: revisionList) {
      if (prev != null) {
        if (finalLeadTime == null) {
          OActivity mActivity = oActivityRepository.findById(curr.getEntity().getId()).orElse(null);
          if (mActivity != null) {
            finalLeadTime = mActivity.getFinalLeadTime();
          }
        }
        Log log = new Log();

        MyRevisionEntity myRevisionEntity = curr.getMetadata().getDelegate();
        log.setTimestamp(myRevisionEntity.getTimestamp());
        log.setUser(myRevisionEntity.getUsername());

        String diffStr = null;
        String event = null;
        Diff diff = javers.compare(new com.jazasoft.tna.audit.Activity(curr.getEntity()), new com.jazasoft.tna.audit.Activity(prev.getEntity()));
        if (diff.hasChanges()) {

          diffStr = diff.prettyPrint();
          if (diff.getPropertyChanges("leadTime").size() == 0) {
            event = "Activity Updated";
          } else  {
            event = "Timeline Overridden";
            PropertyChange propertyChange = diff.getPropertyChanges("leadTime").get(0);
            if (propertyChange instanceof ValueChange) {
              ValueChange valueChange = (ValueChange)propertyChange;
              int difference = ((Integer) valueChange.getLeft()) - ((Integer) valueChange.getRight());

              com.jazasoft.tna.audit.Activity oldEntity = new com.jazasoft.tna.audit.Activity(curr.getEntity());
              com.jazasoft.tna.audit.Activity newEntity = new com.jazasoft.tna.audit.Activity(prev.getEntity());
              newEntity.setFinalLeadTime(finalLeadTime);
              finalLeadTime += difference;
              oldEntity.setFinalLeadTime(finalLeadTime);
              oldEntity.setLeadTime(null);
              newEntity.setLeadTime(null);

              diff = javers.compare(oldEntity, newEntity);
              diffStr = diff.prettyPrint();
            }
          }
        }
        log.setEvent(event);
        log.setDiff(diffStr);

        logs.add(log);
      }
      prev = curr;
    }

    return logs;
  }

  @Transactional(value = "tenantTransactionManager")
  public Order save(Order order) {
    Timeline timeline = timelineRepository.findById(order.getTimelineId()).orElseThrow();

    Assert.isTrue(timeline.getApproved(), "Timeline not approved. Only approved timeline can be used to create Order.");

    order.setTimeline(timeline.getName());

    if (order.getBuyerId() != null) {
      order.setBuyer(buyerRepository.findById(order.getBuyerId()).orElse(null));
    }
    if (order.getGarmentTypeId() != null) {
      order.setGarmentType(garmentTypeRepository.findById(order.getGarmentTypeId()).orElse(null));
    }
    if (order.getSeasonId() != null) {
      order.setSeason(seasonRepository.findById(order.getSeasonId()).orElse(null));
    }
    order.setState(OState.RUNNING.getValue());
    order.setDelayed(false);

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
      mOrder.setEtdDate(order.getEtdDate());
      mOrder.setState(order.getState());
      mOrder.setDelayed(order.getDelayed());

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


  /**
   *
   *
   * Notification Handling:
   *
   *    find changedActivityList
   *
   *    for each changedActivity: changedActivityList
   *      buyerId = ... // from Order of this activity
   *      departmentIds = ... // get from changedActivity
   *
   *      users = ... // fetch users from database applying above two filter
   *
   *      send email to above users
   *
   *
   * @param orderList
   * @param departmentId
   * @return
   */
  @Transactional(value = "tenantTransactionManager")
  public List<Order> updateAll(List<Order> orderList, Long departmentId) {
    // Fetch Orders from database
    Set<Long> ids = orderList.stream().map(Order::getId).collect(Collectors.toSet());
    List<Order> mOrderList = orderRepository.findAllById(ids);

    List<OActivity> changedActivityList = new ArrayList<>();

    DateFormat df = new SimpleDateFormat("dd/MM/yy");

    for (Order order : orderList) {
      Order mOrder = mOrderList.stream().filter(o -> o.getId().equals(order.getId())).findAny().orElseThrow(() -> new RuntimeException("Order with id " + order.getId() + " not found."));
      if (order.getOActivityList() == null || mOrder.getOActivityList() == null) continue;

      for (OActivity oActivity : order.getOActivityList()) {

        OActivity mActivity = mOrder.getOActivityList().stream().filter(a -> a.getId().equals(oActivity.getId())).findAny().orElse(null);

        if (mActivity != null) {
          Hibernate.initialize(mActivity.getOSubActivityList());

          if (mActivity.getCompletedDate() != null && oActivity.getCompletedDate() != null && !df.format(mActivity.getCompletedDate()).equals(df.format(oActivity.getCompletedDate())) && mActivity.getTActivity().getActivity().getNotify() != null) {
            changedActivityList.add(mActivity);
          }

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
          }

          //Set Delayed Flag
          if (LocalDate.now().isAfter(DateUtils.toLocalDate(mActivity.getDueDate()))) {
            boolean isDelayed = mActivity.getCompletedDate() == null || DateUtils.toLocalDate(mActivity.getCompletedDate()).isAfter(DateUtils.toLocalDate(mActivity.getDueDate()));
            long delayDays = mActivity.getCompletedDate() == null ? daysBetween(mActivity.getDueDate(), new Date()) : daysBetween(mActivity.getDueDate(), mActivity.getCompletedDate());
            if (isDelayed && delayDays <= Constants.WEEKLY_MEETING) {
              mActivity.setDelayed(true);
            }
          }
        }
      }

      boolean isDelayed = mOrder.getOActivityList().stream().anyMatch(oActivity -> Boolean.TRUE.equals(oActivity.getDelayed()));
      mOrder.setDelayed(isDelayed);
    }

    Map<Long, Map<String, Object>> activityUserMap = new HashMap<>();

    for (OActivity mActivity: changedActivityList) {
      Long buyerId = mActivity.getOrder().getBuyer().getId();
      Set<Long> departmentIds = Utils.getListFromCsv(mActivity.getTActivity().getActivity().getNotify()).stream().map(Long::parseLong).collect(Collectors.toSet());

      // Fetch user from database by applying department filter
      List<User> users = userRepository.findAll(byDepartmentIdsIn(departmentIds));
      // Filter user by applying buyerId filter in memory
      users = users.stream().filter(user -> {
        if (user.getBuyerIds() == null) return false;
        Set<Long> buyerIds = Utils.getListFromCsv(user.getBuyerIds()).stream().map(Long::parseLong).collect(Collectors.toSet());
        return buyerIds.contains(buyerId);
      }).collect(Collectors.toList());

      List<String> toUsers = users.stream().filter(user -> user.getEmail() != null).map(User::getEmail).collect(Collectors.toList());

      String[] to = new String[toUsers.size()];
      toUsers.toArray(to);
      String subject = "" + mActivity.getName() + " is completed";
      String body = "" + mActivity.getName() + " is completed at " + mActivity.getCompletedDate() + ".";
      logger.debug("to ={}", to);

      activityUserMap.put(mActivity.getId(), new MapBuilder<String, Object>().put("to", to).put("subject", subject).put("body", body).build());
    }

    Runnable task = () -> {
        activityUserMap.values().forEach(map -> {
            String[] to =  (String[]) map.get("to");
            String subject = (String) map.get("subject");
            String body = (String) map.get("body");
            emailService.sendSimpleEmail(to, subject, body);
        });
    };

    threadPool.execute(task);
    return mOrderList;
  }

  @Deprecated
  @Transactional(value = "tenantTransactionManager")
  public OActivity updateActivity(Long orderId, OActivity oActivity) {
    Order mOrder = orderRepository.getOne(orderId);
    OActivity mOActivity = oActivityRepository.findOneByOrderAndId(mOrder, oActivity.getId()).orElseThrow(() -> new RuntimeException("Activity with id = " + oActivity.getId() + " not found for orderId = " + orderId));

    mOActivity.setCompletedDate(oActivity.getCompletedDate());
    mOActivity.setDelayReason(oActivity.getDelayReason());
    mOActivity.setRemarks(oActivity.getRemarks());
    return mOActivity;
  }

  @Deprecated
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

  private Specification<User> byDepartmentIdsIn(Set<Long> departmentIds) {
    return ((root, query, cb) -> root.get("departmentId").in(departmentIds));
  }
}
