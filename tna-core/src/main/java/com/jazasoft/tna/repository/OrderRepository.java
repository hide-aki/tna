package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long>, JpaSpecificationExecutor<Order>, RevisionRepository<Order, Long, Integer> {

  @Override
  @EntityGraph("order.findAll")
  List<Order> findAll();

  @Override
  @EntityGraph("order.findAll")
  List<Order> findAllById(Iterable<Long> ids);

  @Override
  @EntityGraph("order.findAll")
  List<Order> findAll(Specification<Order> spec);

  @Override
  @EntityGraph("order.findAll")
  Page<Order> findAll(Pageable pageable);

  @Override
  @EntityGraph("order.findAll")
  Page<Order> findAll(Specification<Order> spec, Pageable pageable);

  @Override
  @EntityGraph("order.findOne")
  Optional<Order> findById(Long id);

}
