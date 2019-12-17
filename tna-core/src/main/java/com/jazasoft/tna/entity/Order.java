package com.jazasoft.tna.entity;

import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.HashSet;
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
    private Date exFactoryData;

    private String remarks;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Season season;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Buyer buyer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private GarmentType garmentType;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Timeline timeLine;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<OActivity> activityList = new HashSet<>();
}
