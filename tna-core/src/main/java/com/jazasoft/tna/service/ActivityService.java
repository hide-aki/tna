package com.jazasoft.tna.service;

import com.jazasoft.tna.entity.Activity;
import com.jazasoft.tna.entity.SubActivity;
import com.jazasoft.tna.repository.ActivityRepository;
import com.jazasoft.tna.repository.DepartmentRepository;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional(value = "tenantTransactionManager", readOnly = true)
public class ActivityService {
    private final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    private final ActivityRepository activityRepository;
    private final DepartmentRepository departmentRepository;

    public ActivityService(ActivityRepository activityRepository, DepartmentRepository departmentRepository) {
        this.activityRepository = activityRepository;
        this.departmentRepository = departmentRepository;
    }


    public Page<Activity> findAll(Pageable pageable, String action) {
        Page<Activity> page = activityRepository.findAll(pageable);
        if (action.equalsIgnoreCase("timeline")) {
            page.forEach(activity -> Hibernate.initialize(activity.getSubActivityList()));
        }
        return page;
    }

    public Page<Activity> findAll(Specification<Activity> spec, Pageable pageable, String action) {
        Page<Activity> page = activityRepository.findAll(spec, pageable);
        if (action.equalsIgnoreCase("timeline")) {
            page.forEach(activity -> Hibernate.initialize(activity.getSubActivityList()));
        }
        return page;
    }

    public Activity findOne(Long id) {
        Activity activity = activityRepository.findById(id).orElse(null);
        if (activity != null) {
            Hibernate.initialize(activity.getSubActivityList());
        }
        return activity;
    }

    @Transactional(value = "tenantTransactionManager")
    public Activity saveActivity(Activity activity) {

         Activity mActivity = activityRepository.findTopByOrderBySerialNoDesc();

        System.out.println("Activity from database.........................................."+mActivity);

        int lastSerialNumber = mActivity.getSerialNo();

        activity.setSerialNo(++lastSerialNumber);

        if (activity.getDepartmentId() != null) {
            activity.setDepartment(departmentRepository.findById(activity.getDepartmentId()).orElse(null));
        }

        if (activity.getSubActivityList() != null) {
            for (SubActivity subActivity : activity.getSubActivityList()) {
                subActivity.setActivity(activity);
            }
        }
        return activityRepository.save(activity);
    }

    @Transactional(value = "tenantTransactionManager")
    public Activity updateActivity(Activity activity) {
        Activity mActivity = activityRepository.findById(activity.getId()).orElseThrow();
        mActivity.setName(activity.getName());
        mActivity.setSerialNo(activity.getSerialNo());
        mActivity.setDelayReason(activity.getDelayReason());
        mActivity.setNotify(activity.getNotify());
        mActivity.setCLevel(activity.getCLevel());

        mActivity.setDepartment(departmentRepository.findById(activity.getDepartmentId()).orElse(null));

        Set<Long> existingIds = activity.getSubActivityList().stream().filter(subActivity -> subActivity.getId() != null).map(SubActivity::getId).collect(Collectors.toSet());
        Set<SubActivity> removeSubActivityList = mActivity.getSubActivityList().stream().filter(subActivity -> !existingIds.contains(subActivity.getId())).collect(Collectors.toSet());
        Set<SubActivity> newSubActivityList = activity.getSubActivityList().stream().filter(subActivity -> subActivity.getId() == null).collect(Collectors.toSet());

        removeSubActivityList.forEach(mActivity::removeActivity);
        newSubActivityList.forEach(mActivity::addSubActivity);
        existingIds.forEach(id -> {
            SubActivity subActivity = activity.getSubActivityList().stream().filter(p -> id.equals(p.getId())).findAny().orElse(null);
            SubActivity mSubActivity = mActivity.getSubActivityList().stream().filter(p -> id.equals(p.getId())).findAny().orElse(null);
            if (subActivity != null && mSubActivity != null) {
                mSubActivity.setName(subActivity.getName());
                mSubActivity.setDesc(subActivity.getDesc());
            }
        });
        return mActivity;
    }

    public List<Activity> updateActivities(List<Activity> activityList) {
        List<Activity> mActivityList = activityRepository.findAll();

        for (Activity mActivity: mActivityList) {
            Activity activity = activityList.stream().filter(a -> mActivity.getId().equals(a.getId())).findAny().orElse(null);
            if (activity != null) {
                mActivity.setSerialNo(activity.getSerialNo());
            }
        }
//
//        List<Long> existingIds = mActivityList.stream().map(Activity::getId).collect(Collectors.toList());
//
//        existingIds.forEach(id ->{
//             Activity mActivity = mActivityList.stream().filter(activity -> activity.getId() !=null && activity.getId().equals(id)).findAny().get();
//            Activity activity = activityList.stream().filter(activity1 -> activity1.getId() !=null && activity1.getId().equals(id)).findAny().get();
//
//            mActivity.setSerialNo(activity.getSerialNo());
//        });


        return mActivityList;
    }

    @Transactional(value = "tenantTransactionManager")
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    public Boolean exists(Long id) {
        return activityRepository.existsById(id);
    }

}
