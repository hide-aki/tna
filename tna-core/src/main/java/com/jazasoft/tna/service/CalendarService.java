package com.jazasoft.tna.service;

import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.dto.Task;
import com.jazasoft.tna.entity.OActivity;
import com.jazasoft.tna.entity.OSubActivity;
import com.jazasoft.tna.repository.OActivityRepository;
import com.jazasoft.tna.repository.OSubActivityRepository;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.JoinType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(value = "tenantTransactionManager", readOnly = true)
public class CalendarService {

  private final OActivityRepository oActivityRepository;
  private final OSubActivityRepository oSubActivityRepository;

  public CalendarService(OActivityRepository oActivityRepository, OSubActivityRepository oSubActivityRepository) {
    this.oActivityRepository = oActivityRepository;
    this.oSubActivityRepository = oSubActivityRepository;
  }

  public List<Task> findAll(String action, String search, Set<Long> buyerIds, Long departmentId) {

    Node rootNode = new RSQLParser().parse(search);
    Specification<OActivity> aSpec = rootNode.accept(new CustomRsqlVisitor<>());

    aSpec = Specification.where(aSpec).and(byABuyerIds(buyerIds)).and(byADepartmentId(departmentId));
    List<OActivity> oActivityList = oActivityRepository.findAll(aSpec);

    List<Task> taskList = oActivityList.stream().map(Task::new).collect(Collectors.toList());

    if (action.equalsIgnoreCase("full")) {
      Specification<OSubActivity> saSpec = rootNode.accept(new CustomRsqlVisitor<>());
      saSpec = Specification.where(saSpec).and(bySABuyerIds(buyerIds)).and(bySADepartmentId(departmentId));
      List<OSubActivity> oSubActivityList = oSubActivityRepository.findAll(saSpec);

      taskList.addAll(oSubActivityList.stream().map(Task::new).collect(Collectors.toList()));
    }

    return taskList;
  }

  Specification<OActivity> byABuyerIds(Set<Long> buyerIds) {
    return ((root, query, cb) -> root.join("order", JoinType.LEFT).join("buyer", JoinType.LEFT).get("id").in(buyerIds));
  }

  Specification<OActivity> byADepartmentId(Long departmentId) {
    return ((root, query, cb) -> cb.equal(root.join("tActivity", JoinType.LEFT).join("department").get("id"), departmentId));
  }

  Specification<OSubActivity> bySABuyerIds(Set<Long> buyerIds) {
    return ((root, query, cb) -> root.join("oActivity", JoinType.LEFT).join("order", JoinType.LEFT).join("buyer", JoinType.LEFT).get("id").in(buyerIds));
  }

  Specification<OSubActivity> bySADepartmentId(Long departmentId) {
    return ((root, query, cb) -> cb.equal(root.join("oActivity").join("tActivity", JoinType.LEFT).join("department").get("id"), departmentId));
  }
}
