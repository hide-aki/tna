package com.jazasoft.tna.service;


import com.jazasoft.tna.Constants;
import com.jazasoft.tna.entity.*;
import com.jazasoft.tna.repository.ActivityRepository;
import com.jazasoft.tna.repository.BuyerRepository;
import com.jazasoft.tna.repository.SubActivityRepository;
import com.jazasoft.tna.repository.TimelineRepository;
import com.jazasoft.tna.util.TnaUtils;
import com.jazasoft.util.Utils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
                    Activity activity = activityRepository.findById(tActivity.getActivityId()).orElseThrow(() -> new RuntimeException("Activity with id = " + tActivity.getActivityId() + " not found."));
                    tActivity.setActivity(activity);
                    // Copy of Parent: Activity
                    tActivity.setSerialNo(activity.getSerialNo());
                    tActivity.setName(activity.getName());
                    tActivity.setOverridable(activity.getOverridable());
                    tActivity.setDelayReasons(activity.getDelayReasons());
                    tActivity.setDepartment(activity.getDepartment());
                }

                if (tActivity.getTSubActivityList() != null) {
                    for (TSubActivity tSubActivity : tActivity.getTSubActivityList()) {
                        tSubActivity.setTActivity(tActivity);
                        if (tSubActivity.getSubActivityId() != null) {
                            SubActivity subActivity = subActivityRepository.findById(tSubActivity.getSubActivityId()).orElseThrow(() -> new RuntimeException("Sub Activity with id = " + tSubActivity.getSubActivityId() + " not found."));
                            tSubActivity.setSubActivity(subActivity);
                            tSubActivity.setName(subActivity.getName());
                        }
                    }
                }
            }
        }
        Timeline mTimeline = timelineRepository.save(timeline);

        for (TActivity mActivity : mTimeline.getTActivityList()) {
            if (mActivity.getTimeFrom() != null && !Constants.FROM_ORDER_DATE.equalsIgnoreCase(mActivity.getTimeFrom())) {
                Set<Long> activityIds = Utils.getListFromCsv(mActivity.getTimeFrom()).stream().map(String::trim).map(Long::parseLong).collect(Collectors.toSet());
                List<String> tActivityIds = mTimeline.getTActivityList().stream().filter(ta -> ta.getActivity() != null && activityIds.contains(ta.getActivity().getId())).map(TActivity::getId).map(String::valueOf).collect(Collectors.toList());
                mActivity.setTimeFrom(Utils.getCsvFromIterable(tActivityIds));
            }
        }

        mTimeline.setStdLeadTime(TnaUtils.getStdLeadTime(mTimeline.getTActivityList()));

        return mTimeline;
    }

    @Transactional(value = "tenantTransactionManager")
    public Timeline update(Timeline timeline, String action) {

        Timeline mTimeline = timelineRepository.findById(timeline.getId()).orElseThrow();
        if (action.equalsIgnoreCase("approve")) {
            mTimeline.setApproved(true);
            mTimeline.setApprovedBy(timeline.getApprovedBy());
            for (TActivity mActivity : mTimeline.getTActivityList()) {
                mActivity.setPrevLeadTime(mActivity.getLeadTime());
            }
        } else if (action.equalsIgnoreCase("approval_edit")) {
            for (TActivity tActivity : timeline.getTActivityList()) {
                TActivity mActivity = mTimeline.getTActivityList().stream().filter(a -> a.getId().equals(tActivity.getId())).findAny().orElse(null);
                if (mActivity != null) {
                    mActivity.setTimeFrom(tActivity.getTimeFrom());
                    mActivity.setLeadTime(tActivity.getLeadTime());

                    if (mActivity.getTimeFrom() != null && !mActivity.getTimeFrom().equals(Constants.FROM_ORDER_DATE)) {
                        Set<Long> activityIds = Utils.getListFromCsv(mActivity.getTimeFrom()).stream().map(String::trim).map(Long::parseLong).collect(Collectors.toSet());
                        List<String> tActivityIds = mTimeline.getTActivityList().stream().filter(ta -> ta.getActivity() != null && activityIds.contains(ta.getActivity().getId())).map(TActivity::getId).map(String::valueOf).collect(Collectors.toList());
                        mActivity.setTimeFrom(Utils.getCsvFromIterable(tActivityIds));
                    }
                }
            }
        } else if (action.equalsIgnoreCase("default")) {
            //update Own fields
            mTimeline.setName(timeline.getName());
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

            for (TActivity tActivity : newTActivityList) {
                tActivity.setTimeline(mTimeline);
                Activity activity = activityRepository.findById(tActivity.getActivityId()).orElseThrow(() -> new RuntimeException("Activity with id = " + tActivity.getActivityId() + " not found."));
                tActivity.setActivity(activity);

                // Copy of Parent: Activity
                tActivity.setSerialNo(activity.getSerialNo());
                tActivity.setName(activity.getName());
                tActivity.setOverridable(activity.getOverridable());
                tActivity.setDelayReasons(activity.getDelayReasons());
                tActivity.setDepartment(activity.getDepartment());

                if (tActivity.getTSubActivityList() == null) continue;
                for (TSubActivity tSubActivity : tActivity.getTSubActivityList()) {
                    tSubActivity.setTActivity(tActivity);
                    SubActivity subActivity = subActivityRepository.findById(tSubActivity.getSubActivityId()).orElseThrow(() -> new RuntimeException("Sub Activity with id = " + tSubActivity.getSubActivityId() + " not found."));
                    tSubActivity.setSubActivity(subActivity);
                    tSubActivity.setName(subActivity.getName());
                }
            }
            mTimeline.getTActivityList().addAll(newTActivityList);

            existingTActivityIds.forEach(id -> {
                TActivity tActivity = timeline.getTActivityList().stream().filter(a -> id.equals(a.getId())).findAny().orElse(null);
                TActivity mTActivity = mTimeline.getTActivityList().stream().filter(a -> id.equals(a.getId())).findAny().orElse(null);
                if (tActivity != null && mTActivity != null) {
                    mTActivity.setLeadTime(tActivity.getLeadTime());
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

                    for (TSubActivity tSubActivity : newList) {
                        tSubActivity.setTActivity(mTActivity);
                        SubActivity subActivity = subActivityRepository.findById(tSubActivity.getSubActivityId()).orElseThrow(() -> new RuntimeException("Sub Activity with id = " + tSubActivity.getSubActivityId() + " not found."));
                        tSubActivity.setSubActivity(subActivity);
                        tSubActivity.setName(subActivity.getName());
                    }
                    mTActivity.getTSubActivityList().addAll(newList);

                    existingIds.forEach(subActivityId -> {
                        TSubActivity tSubActivity = tActivity.getTSubActivityList().stream().filter(o -> subActivityId.equals(o.getId())).findAny().orElse(null);
                        TSubActivity mTSubActivity = mTActivity.getTSubActivityList().stream().filter(o -> subActivityId.equals(o.getId())).findAny().orElse(null);
                        if (tSubActivity != null && mTSubActivity != null) {
                            mTSubActivity.setLeadTime(tSubActivity.getLeadTime());
                            SubActivity subActivity = subActivityRepository.findById(tSubActivity.getSubActivityId()).orElseThrow(() -> new RuntimeException("Sub Activity with id = " + tSubActivity.getSubActivityId() + " not found."));
                            mTSubActivity.setSubActivity(subActivity);
                            mTSubActivity.setName(subActivity.getName());
                        }
                    });
                }
            });

            for (TActivity mActivity : mTimeline.getTActivityList()) {
                if (mActivity.getTimeFrom() != null && !(mActivity.getTimeFrom().equals("O"))) {
                    Set<Long> activityIds = Utils.getListFromCsv(mActivity.getTimeFrom()).stream().map(String::trim).map(Long::parseLong).collect(Collectors.toSet());
                    List<String> tActivityIds = mTimeline.getTActivityList().stream().filter(ta -> ta.getActivity() != null && activityIds.contains(ta.getActivity().getId())).map(TActivity::getId).map(String::valueOf).collect(Collectors.toList());
                    mActivity.setTimeFrom(Utils.getCsvFromIterable(tActivityIds));
                }
            }
            mTimeline.setApproved(false);
        }

        if (!action.equalsIgnoreCase("approve")) {
            mTimeline.setStdLeadTime(TnaUtils.getStdLeadTime(mTimeline.getTActivityList()));
        }

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
