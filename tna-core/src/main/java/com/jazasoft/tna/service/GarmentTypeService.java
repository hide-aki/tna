package com.jazasoft.tna.service;

import com.jazasoft.tna.entity.GarmentType;
import com.jazasoft.tna.entity.Season;
import com.jazasoft.tna.repository.GarmentTypeRepository;
import com.jazasoft.tna.repository.SeasonRepository;
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
public class GarmentTypeService {
    private final Logger logger = LoggerFactory.getLogger(GarmentTypeService.class);

    private GarmentTypeRepository garmentTypeRepository;

    public GarmentTypeService(GarmentTypeRepository garmentTypeRepository) {
        this.garmentTypeRepository = garmentTypeRepository;
    }

    public List<GarmentType> findAll() {
        return garmentTypeRepository.findAll();
    }

    public Page<GarmentType> findAll(Pageable pageable) {
        return garmentTypeRepository.findAll(pageable);
    }

    public Page<GarmentType> findAll(Specification<GarmentType> spec, Pageable pageable) {
        return garmentTypeRepository.findAll(spec, pageable);
    }

    public GarmentType findOne(Long id) {
        return garmentTypeRepository.findById(id).orElse(null);
    }

    @Transactional(value = "tenantTransactionManager")
    public GarmentType save(GarmentType garmentType) {
        return garmentTypeRepository.save(garmentType);
    }

    @Transactional(value = "tenantTransactionManager")
    public GarmentType update(GarmentType garmentType) {
        GarmentType mGarmentType = garmentTypeRepository.findById(garmentType.getId()).orElseThrow();
        mGarmentType.setName(garmentType.getName());
        mGarmentType.setDesc(garmentType.getDesc());
        return garmentType;
    }

    @Transactional(value = "tenantTransactionManager")
    public void delete(Long id) {
        logger.trace("delete : id ={}", id);
        garmentTypeRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        logger.trace("exists : id ={}", id);
        return garmentTypeRepository.existsById(id);
    }
}
