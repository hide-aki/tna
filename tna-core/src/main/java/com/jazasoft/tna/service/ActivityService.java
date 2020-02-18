package com.jazasoft.tna.service;

import com.jazasoft.tna.entity.Activity;
import com.jazasoft.tna.entity.Department;
import com.jazasoft.tna.entity.SubActivity;
import com.jazasoft.tna.entity.TActivity;
import com.jazasoft.tna.repository.ActivityRepository;
import com.jazasoft.tna.repository.DepartmentRepository;
import com.jazasoft.tna.repository.TActivityRepository;
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
public class ActivityService {
    private final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    private final ActivityRepository activityRepository;
    private final TActivityRepository tActivityRepository;
    private final DepartmentRepository departmentRepository;

    public ActivityService(ActivityRepository activityRepository, TActivityRepository tActivityRepository, DepartmentRepository departmentRepository) {
        this.activityRepository = activityRepository;
        this.tActivityRepository = tActivityRepository;
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

        int lastSerialNumber = mActivity != null ? mActivity.getSerialNo() : 0;
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
        mActivity.setNotify(activity.getNotify());
        mActivity.setIsDefault(activity.getIsDefault());
        mActivity.setCLevel(activity.getCLevel());


        mActivity.setSerialNo(activity.getSerialNo());
        mActivity.setDelayReasons(activity.getDelayReasons());
        mActivity.setOverridable(activity.getOverridable());
        Department department = departmentRepository.findById(activity.getDepartmentId()).orElse(null);
        mActivity.setDepartment(department);

        // Changes above four fields must be propagated to all children
        List<TActivity> tActivityList = tActivityRepository.findAllByActivity(mActivity);
        for (TActivity tActivity: tActivityList) {
            tActivity.setSerialNo(activity.getSerialNo());
            tActivity.setDelayReasons(activity.getDelayReasons());
            tActivity.setOverridable(activity.getOverridable());
            tActivity.setCLevel(activity.getCLevel());
            tActivity.setDepartment(department);
        }

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
    public List<Activity> updateActivities(List<Activity> activityList) {
        List<Activity> mActivityList = activityRepository.findAll();

        for (Activity mActivity : mActivityList) {
            Activity activity = activityList.stream().filter(a -> mActivity.getId().equals(a.getId())).findAny().orElse(null);
            if (activity != null) {
                mActivity.setSerialNo(activity.getSerialNo());
                //Change is serial no should be propagated to all children
                List<TActivity> tActivityList = tActivityRepository.findAllByActivity(mActivity);
                tActivityList.forEach(tActivity -> tActivity.setSerialNo(activity.getSerialNo()));
            }
        }
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
