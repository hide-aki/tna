package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity,Long> , JpaSpecificationExecutor<Activity> {

    @Override
    @EntityGraph("activity.findAll")
    Page<Activity> findAll(Pageable pageable);

    @Override
    @EntityGraph("activity.findAll")
    Page<Activity> findAll(Specification<Activity> spec, Pageable pageable);

    @Override
    @EntityGraph("activity.findOne")
    Optional<Activity> findById(Long id);
}
