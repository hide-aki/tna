package com.jazasoft.tna.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Data
@Entity
@Table(name = "orders")
public class Order extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String poRef;

    @Column(nullable = false)
    private Integer orderQty;

    @Column(nullable = false)
    private String style;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date orderDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date exFactoryDate;

    private String remarks;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Season season;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Buyer buyer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private GarmentType garmentType;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Timeline timeline;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) &&
                Objects.equals(poRef, order.poRef) &&
                Objects.equals(orderQty, order.orderQty) &&
                Objects.equals(style, order.style) &&
                Objects.equals(orderDate, order.orderDate) &&
                Objects.equals(exFactoryDate, order.exFactoryDate) &&
                Objects.equals(remarks, order.remarks) &&
                Objects.equals(season, order.season) &&
                Objects.equals(buyer, order.buyer) &&
                Objects.equals(garmentType, order.garmentType) &&
                Objects.equals(timeline, order.timeline) &&
                Objects.equals(buyerId, order.buyerId) &&
                Objects.equals(timelineId, order.timelineId) &&
                Objects.equals(garmentTypeId, order.garmentTypeId) &&
                Objects.equals(seasonId, order.seasonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, poRef, orderQty, style, orderDate, exFactoryDate, remarks, season, buyer, garmentType, timeline, buyerId, timelineId, garmentTypeId, seasonId);
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
                ", season=" + season +
                ", buyer=" + buyer +
                ", garmentType=" + garmentType +
                ", timeLine=" + timeline +
                ", buyerId=" + buyerId +
                ", timelineId=" + timelineId +
                ", garmentTypeId=" + garmentTypeId +
                ", seasonId=" + seasonId +
                '}';
    }
}
