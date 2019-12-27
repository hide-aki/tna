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


    public Page<Activity> findAll(Pageable pageable) {
        return activityRepository.findAll(pageable);
    }

    public Page<Activity> findAll(Specification<Activity> spec, Pageable pageable) {
        Page<Activity> page = activityRepository.findAll(spec, pageable);
//        page.forEach(activity -> Hibernate.initialize(activity.getDepartment()));
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

    @Transactional(value = "tenantTransactionManager")
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    public Boolean exists(Long id) {
        return activityRepository.existsById(id);
    }

}
