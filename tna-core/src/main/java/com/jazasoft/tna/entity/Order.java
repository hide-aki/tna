package com.jazasoft.tna.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "order.findAll",
        attributeNodes = {
            @NamedAttributeNode("buyer"),
            @NamedAttributeNode("season"),
            @NamedAttributeNode("garmentType"),
        }
    ),
    @NamedEntityGraph(
        name = "order.findOne",
        attributeNodes = {
            @NamedAttributeNode("buyer"),
            @NamedAttributeNode("season"),
            @NamedAttributeNode("garmentType"),
            @NamedAttributeNode(value = "oActivityList", subgraph = "order.oActivityList"),

        },
        subgraphs = {
            @NamedSubgraph(
                name = "order.oActivityList",
                attributeNodes = {
                    @NamedAttributeNode("oSubActivityList")
                }
            )
        }
    )
})
@NoArgsConstructor
@Data
@Entity
@Table(name = "orders")
public class Order extends Auditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  String timeline;

  @NotEmpty
  @Audited
  @Column(nullable = false)
  private String poRef;

  @NotNull
  @Audited
  @Column(nullable = false)
  private Integer orderQty;

  @NotEmpty
  @Audited
  @Column(nullable = false)
  private String style;

  @NotNull
  @Audited
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date orderDate;

  @NotNull
  @Audited
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date exFactoryDate;

  private String remarks;

  private String state;

  private Boolean delayed;

  @Audited(targetAuditMode = NOT_AUDITED)
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Season season;

  @Audited(targetAuditMode = NOT_AUDITED)
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Buyer buyer;

  @Audited(targetAuditMode = NOT_AUDITED)
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private GarmentType garmentType;

  @Audited
  @Temporal(TemporalType.TIMESTAMP)
  private Date etdDate;


  @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonProperty("oActivityList")
  private Set<OActivity> oActivityList = new HashSet<>();


  //Transient Fields
  @Transient
  private Long buyerId;

  @Transient
  private Long timelineId;

  @Transient
  private Long garmentTypeId;

  @Transient
  private Long seasonId;

  public Boolean getDelayed() {
    return delayed != null ? delayed : false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Order)) return false;
    Order order = (Order) o;
    return Objects.equals(id, order.id) &&
        Objects.equals(poRef, order.poRef) &&
        Objects.equals(style, order.style) &&
        Objects.equals(buyerId, order.buyerId) &&
        Objects.equals(timelineId, order.timelineId) &&
        Objects.equals(garmentTypeId, order.garmentTypeId) &&
        Objects.equals(seasonId, order.seasonId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, poRef, style, buyerId, timelineId, garmentTypeId, seasonId);
  }

  @Override
  public String toString() {
    return "Order{" +
        "id=" + id +
        ", poRef='" + poRef + '\'' +
        ", orderQty=" + orderQty +
        ", style='" + style + '\'' +
        ", orderDate=" + orderDate +
        ", exFactoryDate=" + exFactoryDate +
        ", remarks='" + remarks + '\'' +
        ", buyerId=" + buyerId +
        ", timelineId=" + timelineId +
        ", garmentTypeId=" + garmentTypeId +
        ", seasonId=" + seasonId +
        ", state=" + state +
        ", delayed=" + delayed +
        '}';
  }
}
