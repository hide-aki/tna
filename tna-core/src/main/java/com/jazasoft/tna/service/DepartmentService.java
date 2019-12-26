package com.jazasoft.tna.service;

import com.jazasoft.tna.entity.Department;
import com.jazasoft.tna.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(value = "tenantTransactionManager", readOnly = true)
public class DepartmentService {
    private final Logger logger = LoggerFactory.getLogger(DepartmentService.class);

    private DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public Page<Department> findAll(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }

    public Page<Department> findAll(Specification<Department> spec, Pageable pageable) {
        return departmentRepository.findAll(spec, pageable);
    }

    public Department findOne(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    @Transactional(value = "tenantTransactionManager")
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Transactional(value = "tenantTransactionManager")
    public Department update(Department department) {
        Department mDepartment = departmentRepository.findById(department.getId()).orElseThrow();
        mDepartment.setName(department.getName());
        mDepartment.setDesc(department.getDesc());
        return mDepartment;
    }

    @Transactional(value = "tenantTransactionManager")
    public void delete(Long id) {
        logger.trace("delete : id ={}", id);
        departmentRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        logger.trace("exists : id ={}", id);
        return departmentRepository.existsById(id);
    }
}
