package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.SubActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubActivityRepository extends JpaRepository<SubActivity,Long> {
}
