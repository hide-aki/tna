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
        Hibernate.initialize(timeline.getTActivityList());
        if (timeline != null) {
            Hibernate.initialize(timeline.getBuyer());
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
}
