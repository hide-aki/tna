package com.jazasoft.tna.service;

import com.jazasoft.tna.entity.Buyer;
import com.jazasoft.tna.repository.BuyerRepository;
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
public class BuyerService {
    private final Logger logger = LoggerFactory.getLogger(BuyerService.class);

    private BuyerRepository buyerRepository;

    public BuyerService(BuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }


    public List<Buyer> findAll() {
        return buyerRepository.findAll();
    }

    public Page<Buyer> findAll(Pageable pageable) {
        return buyerRepository.findAll(pageable);
    }

    public Page<Buyer> findAll(Specification<Buyer> spec, Pageable pageable) {
        return buyerRepository.findAll(spec, pageable);
    }

    public Buyer findOne(Long id) {
        return buyerRepository.findById(id).orElse(null);
    }

    @Transactional(value = "tenantTransactionManager")
    public Buyer save(Buyer buyer) {
        return buyerRepository.save(buyer);
    }

    @Transactional(value = "tenantTransactionManager")
    public Buyer update(Buyer buyer) {
        Buyer mBuyer = buyerRepository.findById(buyer.getId()).orElseThrow();
        mBuyer.setName(buyer.getName());
        mBuyer.setDesc(buyer.getDesc());
        return mBuyer;
    }

    @Transactional(value = "tenantTransactionManager")
    public void delete(Long id) {
        logger.trace("delete: id = {}", id);
        buyerRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        logger.trace("exists: id = {}", id);
        return buyerRepository.existsById(id);
    }
}
