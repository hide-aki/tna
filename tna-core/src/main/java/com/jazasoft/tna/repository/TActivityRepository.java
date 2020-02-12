package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.Activity;
import com.jazasoft.tna.entity.TActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TActivityRepository extends JpaRepository<TActivity,Long>, JpaSpecificationExecutor<TActivity> {

  List<TActivity> findAllByActivity(Activity activity);
}
