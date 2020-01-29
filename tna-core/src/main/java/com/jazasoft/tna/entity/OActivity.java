package com.jazasoft.tna.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

    @Column(nullable = false)
    private Integer leadTime;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date completedDate;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date dueDate;


    private String delayReason;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @NotEmpty
    private String activityName;

    private String timeFrom;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonProperty("tActivity")
    @JoinColumn(name = "t_activity_id")
    private TActivity tActivity;

    @OneToMany(mappedBy = "oActivity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("oSubActivityList")
    @JsonIgnore
    private Set<OSubActivity> oSubActivityList = new HashSet<>();

    @Transient
    private Long orderId;

    @Transient
    @JsonProperty("tActivityId")
    private Long tActivityId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OActivity)) return false;
        OActivity oActivity = (OActivity) o;
        return Objects.equals(id, oActivity.id) &&
                Objects.equals(leadTime, oActivity.leadTime) &&
                Objects.equals(completedDate, oActivity.completedDate) &&
                Objects.equals(dueDate, oActivity.dueDate) &&
                Objects.equals(delayReason, oActivity.delayReason) &&
                Objects.equals(remarks, oActivity.remarks) &&
                Objects.equals(activityName, oActivity.activityName) &&
                Objects.equals(timeFrom, oActivity.timeFrom) &&
                Objects.equals(order, oActivity.order) &&
                Objects.equals(tActivity, oActivity.tActivity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, leadTime, completedDate, dueDate, delayReason, remarks, activityName, timeFrom, order, tActivity);
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
                ", activityName='" + activityName + '\'' +
                ", timeFrom='" + timeFrom + '\'' +
                ", order=" + order +
                ", tActivity=" + tActivity +
                '}';
    }
}
