package com.jazasoft.tna.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.*;

@NoArgsConstructor
@Data
@Entity
@Table(name = "o_activity")
public class OActivity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String name;

    private Integer serialNo;

    private Boolean overridable;

    @Column(nullable = false)
    private Integer leadTime;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date completedDate;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date dueDate;


    private String delayReason;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    private String timeFrom;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonProperty("tActivity")
    @JoinColumn(name = "t_activity_id")
    private TActivity tActivity;

    @OneToMany(mappedBy = "oActivity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("oSubActivityList")
    private Set<OSubActivity> oSubActivityList = new HashSet<>();

    @Transient
    private Long orderId;

    @Transient
    @JsonProperty("tActivityId")
    private Long tActivityId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OActivity oActivity = (OActivity) o;
        return Objects.equals(id, oActivity.id) &&
                Objects.equals(name, oActivity.name) &&
                Objects.equals(orderId, oActivity.orderId) &&
                Objects.equals(tActivityId, oActivity.tActivityId) &&
                Objects.equals(serialNo, oActivity.serialNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, orderId, tActivityId, serialNo);
    }

    @Override
    public String toString() {
        return "OActivity{" +
                "id=" + id +
                ", leadTime=" + leadTime +
                ", completedDate=" + completedDate +
                ", dueDate=" + dueDate +
                ", delayReason='" + delayReason + '\'' +
                ", remarks='" + remarks + '\'' +
                ", name='" + name + '\'' +
                ", timeFrom='" + timeFrom + '\'' +
                ", order=" + order +
                ", tActivity=" + tActivity +
                ", serialNo=" + serialNo +
                '}';
    }
}


