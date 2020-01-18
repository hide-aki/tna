package com.jazasoft.tna.service;

import com.jazasoft.tna.entity.*;
import com.jazasoft.tna.repository.*;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(value = "tenantTransactionManager", readOnly = true)
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final BuyerRepository buyerRepository;
    private final TimelineRepository timelineRepository;
    private final GarmentTypeRepository garmentTypeRepository;
    private final SeasonRepository seasonRepository;
    private final TActivityRepository tActivityRepository;
    private final OActivityRepository oActivityRepository;
    private final TSubActivityRepository tSubActivityRepository;


    public OrderService(OrderRepository orderRepository, BuyerRepository buyerRepository, TimelineRepository timelineRepository, GarmentTypeRepository garmentTypeRepository, SeasonRepository seasonRepository, TActivityRepository tActivityRepository, OActivityRepository oActivityRepository, TSubActivityRepository tSubActivityRepository) {
        this.orderRepository = orderRepository;
        this.buyerRepository = buyerRepository;
        this.timelineRepository = timelineRepository;
        this.garmentTypeRepository = garmentTypeRepository;
        this.seasonRepository = seasonRepository;
        this.tActivityRepository = tActivityRepository;
        this.oActivityRepository = oActivityRepository;
        this.tSubActivityRepository = tSubActivityRepository;
    }

    public Page<Order> findAll(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        page.forEach(order -> Hibernate.initialize(order.getSeason()));
        page.forEach(order -> Hibernate.initialize(order.getBuyer()));
        page.forEach(order -> Hibernate.initialize(order.getGarmentType()));
        page.forEach(order -> Hibernate.initialize(order.getTimeline()));
        return page;
    }

    public Page<Order> findAll(Specification<Order> spec, Pageable pageable) {
        Page<Order> page = orderRepository.findAll(spec, pageable);
        page.forEach(order -> Hibernate.initialize(order.getSeason()));
        page.forEach(order -> Hibernate.initialize(order.getBuyer()));
        page.forEach(order -> Hibernate.initialize(order.getGarmentType()));
        page.forEach(order -> Hibernate.initialize(order.getTimeline()));
        return page;
    }

    public Order findOne(Long id){
        Order order = orderRepository.findById(id).orElse(null);
        if(order != null){
            Hibernate.initialize(order.getOActivityList());
            order.getOActivityList().forEach(oActivity -> Hibernate.initialize(oActivity.getOSubActivityList()));
            Hibernate.initialize(order.getTimeline().getTActivityList());
            order.getOActivityList().forEach(oActivity -> oActivity.getOSubActivityList().forEach(oSubActivity -> Hibernate.initialize(oSubActivity.getTSubActivity())));
            order.getOActivityList().forEach(oActivity -> oActivity.getTActivity().getTSubActivityList().forEach(tSubActivity -> Hibernate.initialize(tSubActivity.getSubActivity())));

        }
        return order;
    }

    @Transactional(value = "tenantTransactionManager")
    public Order save(Order order) {
        Timeline timeline = timelineRepository.findById(order.getTimelineId()).orElseThrow();

        if (order.getBuyerId() != null) {
            order.setBuyer(buyerRepository.findById(order.getBuyerId()).orElse(null));
        }
        if (order.getTimelineId() != null) {
            order.setTimeline(timeline);
        }
        if (order.getGarmentTypeId() != null) {
            order.setGarmentType(garmentTypeRepository.findById(order.getGarmentTypeId()).orElse(null));
        }
        if (order.getSeasonId() != null) {
            order.setSeason(seasonRepository.findById(order.getSeasonId()).orElse(null));
        }

        Set<OActivity> oActivityList = timeline.getTActivityList().stream().map(tActivity -> {
            Hibernate.initialize(tActivity.getActivity().getDepartment());

            OActivity oActivity = new OActivity();
            oActivity.setOrder(order);
            oActivity.setOrderId(order.getId());
            oActivity.setTActivity(tActivity);
            oActivity.setTActivityId(tActivity.getId());

            //setting leadTime in oActivity
            int leadTimeNormal = tActivity.getLeadTimeNormal();
            int leadTimeOptimal = tActivity.getLeadTimeOptimal();
            int leadTime = getActivityLeadTime(leadTimeNormal, leadTimeOptimal);

            oActivity.setLeadTime(leadTime);
            oActivity.setActivityName(tActivity.getActivity().getName());
            oActivity.setTimeFrom(tActivity.getTimeFrom());

            Set<OSubActivity> oSubActivityList = tActivity.getTSubActivityList().stream().map(tSubActivity -> {
                OSubActivity oSubActivity = new OSubActivity();
                oSubActivity.setTSubActivity(tSubActivity);
                oSubActivity.setTSubActivityId(tSubActivity.getId());
                oSubActivity.setOActivity(oActivity);
                oSubActivity.setOActivityId(oActivity.getId());

                //setting leadTime in oSubActivity
                int leadTimeNormalSub = tSubActivity.getLeadTimeNormal();
                int leadTimeSub = getSubActivityLeadTime(leadTimeNormalSub);
                oSubActivity.setLeadTime(leadTimeSub);
                oSubActivity.setSubActivityName(tSubActivity.getSubActivity().getName());

                return oSubActivity;
            }).collect(Collectors.toSet());

            oActivity.setOSubActivityList(oSubActivityList);

            return oActivity;
        }).collect(Collectors.toSet());

        order.setOActivityList(oActivityList);

        return orderRepository.save(order);
    }

    public int getActivityLeadTime(int leadTimeNormal, int leadTimeOptimal) {
        int leadTime = leadTimeNormal + leadTimeOptimal / 2;
        return leadTime;
    }

    public int getSubActivityLeadTime(int leadTimeNormal) {
        return leadTimeNormal;
    }

    public boolean exists(Long id){
        return orderRepository.existsById(id);
    }

    @Transactional(value = "tenantTransactionManager")
    public void delete(Long id){
        orderRepository.deleteById(id);
    }
}
