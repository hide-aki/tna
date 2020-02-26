package com.jazasoft.tna.service;

import com.jazasoft.mtdb.specification.CustomRsqlVisitor;
import com.jazasoft.tna.entity.view.Task;
import com.jazasoft.tna.entity.view.TaskActivityView;
import com.jazasoft.tna.entity.view.TaskSubActivityView;
import com.jazasoft.tna.repository.view.TaskActivityViewRepository;
import com.jazasoft.tna.repository.view.TaskSubActivityViewRepository;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional(value = "tenantTransactionManager", readOnly = true)
public class CalendarService {

  private final TaskActivityViewRepository taskActivityViewRepository;
  private final TaskSubActivityViewRepository taskSubActivityViewRepository;

  public CalendarService(TaskActivityViewRepository taskActivityViewRepository, TaskSubActivityViewRepository taskSubActivityViewRepository) {
    this.taskActivityViewRepository = taskActivityViewRepository;
    this.taskSubActivityViewRepository = taskSubActivityViewRepository;
  }

  public List<Task> findAll(String action, String search, Set<Long> buyerIds, Long departmentId) {
    List<Task> taskList = new ArrayList<>();
    if (buyerIds.isEmpty() || departmentId == null) return taskList;

    Node rootNode = new RSQLParser().parse(search);
    Specification<TaskActivityView> aSpec = rootNode.accept(new CustomRsqlVisitor<>());
    aSpec = Specification.where(aSpec).and(byABuyerIds(buyerIds)).and(byADepartmentId(departmentId));
    List<TaskActivityView> aTaskList = taskActivityViewRepository.findAll(aSpec);

    aTaskList.forEach(taskActivityView -> taskActivityView.setType("Activity"));
    taskList.addAll(aTaskList);

    if (action.equalsIgnoreCase("full")) {
      Specification<TaskSubActivityView> saSpec = rootNode.accept(new CustomRsqlVisitor<>());
      saSpec = Specification.where(saSpec).and(bySABuyerIds(buyerIds)).and(bySADepartmentId(departmentId));
      List<TaskSubActivityView> saTaskList = taskSubActivityViewRepository.findAll(saSpec);

      saTaskList.forEach(taskSubActivityView -> taskSubActivityView.setType("SubActivity"));
      taskList.addAll(saTaskList);
    }

    return taskList;
  }

  private Specification<TaskActivityView> byABuyerIds(Set<Long> buyerIds) {
    return ((root, query, cb) -> root.get("buyerId").in(buyerIds));
  }

  private Specification<TaskActivityView> byADepartmentId(Long departmentId) {
    return ((root, query, cb) -> cb.equal(root.get("departmentId"), departmentId));
  }

  private Specification<TaskSubActivityView> bySABuyerIds(Set<Long> buyerIds) {
    return ((root, query, cb) -> root.get("buyerId").in(buyerIds));
  }

  private Specification<TaskSubActivityView> bySADepartmentId(Long departmentId) {
    return ((root, query, cb) -> cb.equal(root.get("departmentId"), departmentId));
  }
}
