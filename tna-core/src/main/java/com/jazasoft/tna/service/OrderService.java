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

    public Page<Order> findAll(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        page.forEach(order -> Hibernate.initialize(order.getSeason()));
        page.forEach(order -> Hibernate.initialize(order.getBuyer()));
        page.forEach(order -> Hibernate.initialize(order.getGarmentType()));
        page.forEach(order -> Hibernate.initialize(order.getBuyerId()));
        return page;
    }

    public Page<Order> findAll(Specification<Order> spec, Pageable pageable) {
        Page<Order> page = orderRepository.findAll(spec, pageable);
        page.forEach(order -> Hibernate.initialize(order.getSeason()));
        page.forEach(order -> Hibernate.initialize(order.getBuyer()));
        page.forEach(order -> Hibernate.initialize(order.getGarmentType()));
        page.forEach(order -> Hibernate.initialize(order.getBuyerId()));
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
        int currentLeadTime = (int)DAYS.between(DateUtils.toLocalDate(order.getOrderDate()), DateUtils.toLocalDate(order.getExFactoryDate()));


        Set<OActivity> oActivityList = new HashSet<>();
        for (TActivity tActivity: timeline.getTActivityList()) {
            OActivity oActivity = new OActivity();
            oActivity.setOrder(order);
            oActivity.setTActivity(tActivity);

            int leadTime = TnaUtils.getLeadTime(tActivity.getLeadTime(), currentLeadTime, standardLeadTime);

            oActivity.setSerialNo(tActivity.getSerialNo());
            oActivity.setName(tActivity.getName());
            oActivity.setOverridable(tActivity.getOverridable());
            oActivity.setLeadTime(leadTime);
            oActivity.setTimeFrom(tActivity.getTimeFrom());

            Set<OSubActivity> oSubActivityList = new HashSet<>();

            for (TSubActivity tSubActivity: tActivity.getTSubActivityList()) {
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
