package com.jazasoft.tna.repository.view;

import com.jazasoft.tna.entity.view.TaskActivityView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskActivityViewRepository extends JpaRepository<TaskActivityView, Long>, JpaSpecificationExecutor<TaskActivityView> {
}
