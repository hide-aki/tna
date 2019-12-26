package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.GarmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GarmentTypeRepository extends JpaRepository<GarmentType, Long>, JpaSpecificationExecutor<GarmentType> {
}
