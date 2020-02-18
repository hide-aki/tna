package com.jazasoft.tna.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jazasoft.mtdb.entity.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.*;

@Data
@Entity
@Table(name = "o_activity")
public class OActivity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String name;

    @Column(nullable = false)
    private Integer leadTime;

    // Final Lead Time using Order Date as Reference
    private Integer finalLeadTime;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date completedDate;

    private String delayReason;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
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

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date dueDate;


    public OActivity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OActivity oActivity = (OActivity) o;
        return Objects.equals(id, oActivity.id) &&
                Objects.equals(name, oActivity.name) &&
                Objects.equals(orderId, oActivity.orderId) &&
                Objects.equals(tActivityId, oActivity.tActivityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, orderId, tActivityId);
    }

    @Override
    public String toString() {
        return "OActivity{" +
                "id=" + id +
                ", leadTime=" + leadTime +
                ", completedDate=" + completedDate +
                ", delayReason='" + delayReason + '\'' +
                ", remarks='" + remarks + '\'' +
                ", name='" + name + '\'' +
                ", order=" + order +
                ", tActivity=" + tActivity +
                ", dueDate=" + dueDate +
                '}';
    }
}


