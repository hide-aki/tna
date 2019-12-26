package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DepartmentRepository extends JpaRepository<Department,Long> , JpaSpecificationExecutor<Department> {
}
