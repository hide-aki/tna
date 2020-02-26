package com.jazasoft.tna.audit;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

import java.util.Date;

@NoArgsConstructor
@Data
@TypeName("Order")
public class Order {
  @Id
  private Long id;
  private String poRef;
  private String style;
  private Integer orderQty;
  private Date orderDate;
  private Date exFactoryDate;
  private Date etdDate;
  private Long seasonId;
  private Long buyerId;
  private Long garmentTypeId;

  public Order(com.jazasoft.tna.entity.Order order) {
    this.id = order.getId();
    this.poRef = order.getPoRef();
    this.style = order.getStyle();
    this.orderQty = order.getOrderQty();
    this.exFactoryDate = order.getExFactoryDate();
    this.etdDate = order.getEtdDate();
    this.seasonId = order.getSeason() != null ? order.getSeason().getId() : null;
    this.buyerId = order.getBuyer() != null ? order.getBuyer().getId() : null;
    this.garmentTypeId = order.getGarmentType() != null ? order.getGarmentType().getId() : null;
  }
}
