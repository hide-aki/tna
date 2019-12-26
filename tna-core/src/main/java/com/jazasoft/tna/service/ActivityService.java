package com.jazasoft.tna.service;

import com.jazasoft.tna.entity.Activity;
import com.jazasoft.tna.entity.SubActivity;
import com.jazasoft.tna.repository.ActivityRepository;
import com.jazasoft.tna.repository.DepartmentRepository;
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
        return activityRepository.findAll(spec, pageable);
    }

    public Activity findOne(Long id) {
        return activityRepository.findById(id).orElse(null);
    }

//    public Activity updateActivity(Activity activity){
//        Activity mActivity = activityRepository.findById(activity.getId()).orElseThrow();
//        mActivity.setName(activity.getName());
//        mActivity.setSerialNo(activity.getSerialNo());
//        mActivity.setNotify(activity.getNotify());
//        mActivity.setCLevel(activity.getCLevel());
//
//        Set<Long> existingIds = activity.getSubActivityList().stream().filter(subActivity -> subActivity.getId() != null).map(SubActivity::getId).collect(Collectors.toSet());
//
//        Set<SubActivity> removeSubActivityList = activity.getSubActivityList().stream().filter(subActivity -> existingIds.contains(subActivity.getId())).collect(Collectors.toSet());
//
//
//
//    }

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

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    public Boolean exists(Long id) {
        return activityRepository.existsById(id);
    }

}
