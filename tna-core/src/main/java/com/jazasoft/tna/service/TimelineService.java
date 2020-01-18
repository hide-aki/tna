package com.jazasoft.tna.service;


import com.jazasoft.tna.entity.TActivity;
import com.jazasoft.tna.entity.TSubActivity;
import com.jazasoft.tna.entity.Timeline;
import com.jazasoft.tna.repository.ActivityRepository;
import com.jazasoft.tna.repository.BuyerRepository;
import com.jazasoft.tna.repository.SubActivityRepository;
import com.jazasoft.tna.repository.TimelineRepository;
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
public class TimelineService {

    private final Logger logger = LoggerFactory.getLogger(TimelineService.class);

    private final TimelineRepository timelineRepository;
    private final BuyerRepository buyerRepository;
    private final ActivityRepository activityRepository;
    private final SubActivityRepository subActivityRepository;

    public TimelineService(TimelineRepository timelineRepository, BuyerRepository buyerRepository, ActivityRepository activityRepository, SubActivityRepository subActivityRepository) {
        this.timelineRepository = timelineRepository;
        this.buyerRepository = buyerRepository;
        this.activityRepository = activityRepository;
        this.subActivityRepository = subActivityRepository;
    }

    public Page<Timeline> findAll(Pageable pageable) {
        Page<Timeline> page = timelineRepository.findAll(pageable);
        page.forEach(timeline -> Hibernate.initialize(timeline.getBuyer()));
        return page;
    }

    public Page<Timeline> findAll(Specification<Timeline> spec, Pageable pageable) {
        Page<Timeline> page = timelineRepository.findAll(spec, pageable);
        page.forEach(timeline -> Hibernate.initialize(timeline.getBuyer()));
        return page;
    }

    public Timeline findOne(Long id) {
        Timeline timeline = timelineRepository.findById(id).orElse(null);
        if (timeline != null) {
            Hibernate.initialize(timeline.getTActivityList());
            timeline.getTActivityList().forEach(tActivity -> Hibernate.initialize(tActivity.getTSubActivityList()));
            timeline.getTActivityList().forEach(tActivity -> tActivity.getTSubActivityList().forEach(tSubActivity -> Hibernate.initialize(tSubActivity.getSubActivity())));
            Hibernate.initialize(timeline.getBuyer());
            timeline.getTActivityList().forEach(tActivity -> Hibernate.initialize(tActivity.getActivity()));
        }
        return timeline;
    }

    @Transactional(value = "tenantTransactionManager")
    public Timeline save(Timeline timeline) {
        if (timeline.getBuyerId() != null) {
            timeline.setBuyer(buyerRepository.findById(timeline.getBuyerId()).orElse(null));
        }

        //todo: validate subActivity is the child of Activity.

        if (timeline.getTActivityList() != null) {
            for (TActivity tActivity : timeline.getTActivityList()) {
                tActivity.setTimeline(timeline);
                if (tActivity.getActivityId() != null) {
                    tActivity.setActivity(activityRepository.findById(tActivity.getActivityId()).orElse(null));
                }

                if (tActivity.getTSubActivityList() != null) {
                    for (TSubActivity tSubActivity : tActivity.getTSubActivityList()) {
                        tSubActivity.setTActivity(tActivity);
                        if (tSubActivity.getSubActivityId() != null) {
                            tSubActivity.setSubActivity(subActivityRepository.findById(tSubActivity.getSubActivityId()).orElse(null));
                        }
                    }
                }
            }
        }
        return timelineRepository.save(timeline);
    }

    @Transactional(value = "tenantTransactionManager")
    public Timeline update(Timeline timeline) {
        Timeline mTimeline = timelineRepository.findById(timeline.getId()).orElseThrow();
        mTimeline.getTActivityList().forEach(tActivity -> Hibernate.initialize(tActivity.getActivity().getDepartment()));
        mTimeline.getTActivityList().forEach(tActivity -> Hibernate.initialize(tActivity.getTSubActivityList()));

        //update Own fields
        mTimeline.setName(timeline.getName());
        mTimeline.setTnaType(timeline.getTnaType());
        mTimeline.setApproved(timeline.getApproved());
        mTimeline.setApprovedBy(timeline.getApprovedBy());
        mTimeline.setBuyer(buyerRepository.findById(timeline.getBuyerId()).orElse(null));
        mTimeline.setBuyerId(timeline.getBuyerId());

        //update Relational fields
        Set<Long> existingTActivityIds = timeline.getTActivityList().stream().
                filter(tActivity -> tActivity.getId() != null).map(TActivity::getId).
                collect(Collectors.toSet());
        Set<TActivity> removeTActivityList = mTimeline.getTActivityList().stream().
                filter(tActivity -> !existingTActivityIds.contains(tActivity.getId())).
                collect(Collectors.toSet());
        Set<TActivity> newTActivityList = timeline.getTActivityList().stream().
                filter(tActivity -> tActivity.getId() == null).
                collect(Collectors.toSet());

        removeTActivityList.forEach(mTimeline::removeTActivity);

        newTActivityList.forEach(mTimeline::addTActivity);
        for (TActivity tActivity: newTActivityList) {
            tActivity.setActivity(activityRepository.findById(tActivity.getActivityId()).orElse(null));
            tActivity.getTSubActivityList().forEach(tSubActivity -> {
                tSubActivity.setSubActivity(subActivityRepository.findById(tSubActivity.getSubActivityId()).orElse(null));
            });
        }

        existingTActivityIds.forEach(id -> {
            TActivity tActivity = timeline.getTActivityList().stream().filter(d -> d.getId() != null && d.getId().equals(id)).findAny().get();
            TActivity mTActivity = mTimeline.getTActivityList().stream().filter(d -> d.getId() != null && d.getId().equals(id)).findAny().get();

            mTActivity.setLeadTimeNormal(tActivity.getLeadTimeNormal());
            mTActivity.setLeadTimeOptimal(tActivity.getLeadTimeOptimal());
            mTActivity.setTimeFrom(tActivity.getTimeFrom());

            Set<Long> existingIds = tActivity.getTSubActivityList().stream().
                    filter(tSubActivity -> tSubActivity.getId() != null).map(TSubActivity::getId).
                    collect(Collectors.toSet());
            Set<TSubActivity> removeList = mTActivity.getTSubActivityList().stream().
                    filter(tSubActivity -> !existingIds.contains(tSubActivity.getId())).
                    collect(Collectors.toSet());
            Set<TSubActivity> newList = tActivity.getTSubActivityList().stream().
                    filter(tSubActivity -> tSubActivity.getId() == null).
                    collect(Collectors.toSet());

            removeList.forEach(mTActivity::removeTSubActivity);

            newList.forEach(mTActivity::addTSubActivity);
            for (TSubActivity tSubActivity: newList) {
                tSubActivity.setSubActivity(subActivityRepository.findById(tSubActivity.getSubActivityId()).orElse(null));
            }

            existingIds.forEach(subActivityId -> {
                TSubActivity tSubActivity = tActivity.getTSubActivityList().stream().filter(o -> o.getId() != null && o.getId().equals(subActivityId)).findAny().get();
                TSubActivity mTSubActivity = mTActivity.getTSubActivityList().stream().filter(o -> o.getId() != null && o.getId().equals(subActivityId)).findAny().get();

                mTSubActivity.setLeadTimeNormal(tSubActivity.getLeadTimeNormal());
                mTSubActivity.setSubActivity(subActivityRepository.findById(tSubActivity.getSubActivityId()).orElse(null));
            });
        });

        Hibernate.initialize(mTimeline.getBuyer());
        mTimeline.getTActivityList().forEach(tActivity -> {
            Hibernate.initialize(tActivity.getActivity());
            tActivity.getTSubActivityList().forEach(tSubActivity -> Hibernate.initialize(tSubActivity.getSubActivity()));
        });

        return mTimeline;
    }

    public boolean exists(Long id) {
        return timelineRepository.existsById(id);
    }

    @Transactional(value = "tenantTransactionManager")
    public void deleteTimeline(Long id) {
        timelineRepository.deleteById(id);
    }
}
