package com.jazasoft.tna.repository.view;

import com.jazasoft.tna.entity.view.TaskSubActivityView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskSubActivityViewRepository extends JpaRepository<TaskSubActivityView, Long>, JpaSpecificationExecutor<TaskSubActivityView> {
}
