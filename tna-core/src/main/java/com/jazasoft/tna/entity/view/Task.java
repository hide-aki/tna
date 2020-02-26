package com.jazasoft.tna.entity.view;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.Date;

@NoArgsConstructor
@Data
@MappedSuperclass
public class Task {
  @Id
  private Long id;

  @Transient
  private String type;

  private Long buyerId;

  private Long orderId;

  private Long departmentId;

  private Long seasonId;

  private String buyer;

  private String season;

  private String poRef;

  private String style;

  private Integer orderQty;

  private String name;

  private Date dueDate;

  private Date completedDate;
}
