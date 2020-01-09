package com.jazasoft.tna.service;


import com.jazasoft.tna.entity.Activity;
import com.jazasoft.tna.entity.DelayReason;
import com.jazasoft.tna.entity.Team;
import com.jazasoft.tna.repository.ActivityRepository;
import com.jazasoft.tna.repository.DelayReasonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DelayReasonService {
    private final Logger logger = LoggerFactory.getLogger(DelayReasonService.class);

    private DelayReasonRepository delayReasonRepository;
    private ActivityRepository activityRepository;

    public DelayReasonService(DelayReasonRepository delayReasonRepository, ActivityRepository activityRepository) {
        this.delayReasonRepository = delayReasonRepository;
        this.activityRepository = activityRepository;
    }

    public List<DelayReason> findAll() {
        List<DelayReason> delayReasonList = delayReasonRepository.findAll();
        delayReasonList.forEach(delayReason -> delayReason.setActivityId(delayReason.getActivity() != null ? delayReason.getActivity().getId() : null));
        return delayReasonList;
    }

    public Page<DelayReason> findAll(Pageable pageable) {
        Page<DelayReason> delayReasonList = delayReasonRepository.findAll(pageable);
        delayReasonList.forEach(delayReason -> delayReason.setActivityId(delayReason.getActivity() != null ? delayReason.getActivity().getId() : null));
        return delayReasonList;
    }

    public Page<DelayReason> findAll(Specification<DelayReason> spec, Pageable pageable) {
        Page<DelayReason> delayReasonsList = delayReasonRepository.findAll(spec, pageable);
        delayReasonsList.forEach(delayReason -> delayReason.setActivityId(delayReason.getActivity() != null ? delayReason.getActivity().getId() : null));
        return delayReasonsList;
    }

    public DelayReason findOne(Long id) {
        DelayReason delayReason = delayReasonRepository.findById(id).orElse(null);
        return delayReason;
    }


    @Transactional(value = "tenantTransactionManager")
    public DelayReason save(DelayReason delayReason) {
        Activity activity = activityRepository.findById(delayReason.getActivityId()).orElse(null);
        delayReason.setActivity(activity);
        return delayReasonRepository.save(delayReason);
    }

    @Transactional(value = "tenantTransactionManager")
    public DelayReason update(DelayReason delayReason) {
        DelayReason mDelayReason = delayReasonRepository.findById(delayReason.getId()).orElseThrow();

        System.out.println("Database Delay" + mDelayReason.getName() + "request delay" + delayReason.getName());
        mDelayReason.setName(delayReason.getName());
        if (!mDelayReason.getActivity().getId().equals(delayReason.getActivityId())) {
            mDelayReason.setActivityId(delayReason.getActivityId());
            mDelayReason.setActivity(activityRepository.findById(delayReason.getActivityId()).orElse(null));
        }
        return mDelayReason;
    }

    @Transactional(value = "tenantTransactionManager")
    public void delete(Long id) {
        delayReasonRepository.deleteById(id);
    }

    public Boolean exists(Long id) {
        return delayReasonRepository.existsById(id);
    }
}
