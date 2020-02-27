package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Long> , JpaSpecificationExecutor<Department> {

    Optional<Department> findByName(String name);
}
