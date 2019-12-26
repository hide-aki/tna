package com.jazasoft.tna.service;

import com.jazasoft.tna.entity.Buyer;
import com.jazasoft.tna.entity.Season;
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
public class SeasonService {
    private final Logger logger = LoggerFactory.getLogger(SeasonService.class);

    private SeasonRepository seasonRepository;

    public SeasonService(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    public List<Season> findAll() {
        return seasonRepository.findAll();
    }

    public Page<Season> findAll(Pageable pageable) {
        return seasonRepository.findAll(pageable);
    }

    public Page<Season> findAll(Specification<Season> spec, Pageable pageable) {
        return seasonRepository.findAll(spec, pageable);
    }

    public Season findOne(Long id) {
        return seasonRepository.findById(id).orElse(null);
    }

    @Transactional(value = "tenantTransactionManager")
    public Season save(Season season) {
        return seasonRepository.save(season);
    }

    @Transactional(value = "tenantTransactionManager")
    public Season update(Season season) {
        Season mSeason = seasonRepository.findById(season.getId()).orElseThrow();
        mSeason.setName(season.getName());
        mSeason.setDesc(season.getDesc());
        return mSeason;
    }

    @Transactional(value = "tenantTransactionManager")
    public void delete(Long id) {
        logger.trace("delete : id ={}", id);
        seasonRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        logger.trace("exists : id ={}", id);
        return seasonRepository.existsById(id);
    }
}
